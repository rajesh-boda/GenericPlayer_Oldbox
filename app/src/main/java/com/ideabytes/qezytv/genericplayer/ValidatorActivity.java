package com.ideabytes.qezytv.genericplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.provider.Settings.Secure;

import com.ideabytes.qezytv.genericplayer.alertdialogs.AlertDialog;
import com.ideabytes.qezytv.genericplayer.alertdialogs.AlertDialogValidator;
import com.ideabytes.qezytv.genericplayer.asynctasks.AsyncTaskGetChannelInfo;
import com.ideabytes.qezytv.genericplayer.asynctasks.AsyncTaskGetLicenseKey;
import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.constants.FolderAndURLConstants;
import com.ideabytes.qezytv.genericplayer.database.InsertChannelInfo;
import com.ideabytes.qezytv.genericplayer.model.Pojo;
import com.ideabytes.qezytv.genericplayer.network.PingIP;
import com.ideabytes.qezytv.genericplayer.utils.DeviceInfo;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : ValidatorActivity
 * author:  Suman
 * Created Date : 08-12-2015
 * Description : This activity is to show a dialog which is to validate customer based on device id and license key
 * Modified Date : 25-04-2016
 * Reason: network checking code changed
 *************************************************************/
public class ValidatorActivity extends Activity implements DeviceConstants,FolderAndURLConstants {
    private static final String LOGTAG = "ValidatorActivity";
    private static Activity activity;
    String unique_id;
    private InsertChannelInfo insertChannelInfo ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //to show full screen
        unique_id = Secure.getString(getApplicationContext().getContentResolver(),Secure.ANDROID_ID);
        Log.v(LOGTAG,"Unique ID "+unique_id);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.validator_background);
           activity = this;
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("setup",MODE_PRIVATE);
            int key = sharedPreferences.getInt("setup",0);
            Log.v(LOGTAG,"key "+key);
            File licenseData = new File(FolderAndURLConstants.FOLDER_PATH_LICENSE +"/License.txt");
            if (key == 0 && !licenseData.exists()) {
                //this is to show alert dialog to validate user based on device id and license key
//                showDialog();
                Log.v(LOGTAG,"Get License Started ");
                AsyncTaskGetLicenseKey postDeviceData = new AsyncTaskGetLicenseKey(ValidatorActivity.this);
                //response of service as string
                String responseKey = postDeviceData.execute(unique_id).get();
                if(!responseKey.equalsIgnoreCase(DeviceConstants.EXCEPTION) || !responseKey.equalsIgnoreCase(DeviceConstants.VALUE_202)) {
                    generateNoteOnSD(unique_id + "::" + responseKey);
                    Log.v(LOGTAG, "Get License final key " + responseKey);
                }else{
                    showDialog();
                }
                insertChannelInfo = new InsertChannelInfo(getApplicationContext());
                insertChannelInfo.insertPlayerStatus("404");
            }  else if(key == 1) {
              //  Log.e(LOGTAG,"License setup done, loading video");
                //start streaming
                Intent intent = new Intent(ValidatorActivity.this, VideoActivity.class);
                Pojo.getInstance().setIsUserStopped(false);
                startActivity(intent);
                finish();
            } else {
               // Log.e(LOGTAG,"License activating using License.txt");
                insertChannelInfo = new InsertChannelInfo(getApplicationContext());
                insertChannelInfo.insertPlayerStatus("404");
                activateLicense();
            }
        } catch (Exception e) {
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(getApplicationContext());
            sendExceptionsToServer.sendMail(ValidatorActivity.this.getClass().getName(), Utils.convertExceptionToString(e), "Exception");
        }
    }

    private void activateLicense() {
        try {
            // Log.v(LOGTAG, "license data " + licenseData.exists()+" file path "+licenseData.getAbsolutePath());
            //when there is no text file, show license dialog to take device id and license key
            //if license text file is available, then get details from file and get license details
            File file = new File(FolderAndURLConstants.FOLDER_PATH_LICENSE, "License.txt");
            if (file.length() == 0) {
                Log.e(LOGTAG,"License text file empty");
                callMain(202, "License text file is empty");
                // file empty
            } else {
                // not empty
                String content = new Utils().readTextFile(FolderAndURLConstants.FOLDER_PATH_LICENSE, "License");
                //text file available read device id and license key from the file
               // Log.v(LOGTAG, "Boss, there is a license text file "+content);
                if (content.endsWith("::")) {
                   // Log.v(LOGTAG, "Boss, key is missing ");
                    callMain(202, "License key is missing from License text file");
                } else if (content.startsWith("::")) {
                   // Log.v(LOGTAG, "Boss, id is missing ");
                    callMain(202, "Device id is missing from License text file");
                } else if (!content.contains("::")) {
                    //Log.v(LOGTAG, "Boss, wrong format ");
                    callMain(202, "License text file in wrong format");
                } else {
                    String deviceId = content.split("::")[0];
                     //Log.v(LOGTAG, "device id " + deviceId);
                    String licenseKey = content.split("::")[1];
                   //Log.v(LOGTAG, "licenseKey " + licenseKey);
                   // Log.v(LOGTAG, "Boss, start player ");
                    launchPlayer(deviceId, licenseKey);

                }
            }
        } catch (Exception e) {
           // Log.e(LOGTAG,"exception ");
            callMain(202, "Facing technical issues, Please try again!");
            e.printStackTrace();
        }

    }

    public void generateNoteOnSD( String sBody) {
        try {
            File root = new File(FolderAndURLConstants.FOLDER_PATH_LICENSE);
            if (!root.exists()) {
                root.mkdirs();
            }
            String sFileName = "License.txt";
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            activateLicense();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * This method is to launch player by getting license info based on device id and license key read from text file
     * @param deviceId
     * @param licenseKey
     */
    private void launchPlayer(final String deviceId,final String licenseKey) {
        try {
            boolean netStatus = PingIP.isConnectingToInternet(getApplicationContext());
            if (netStatus) {
                //Log.v(LOGTAG, "Boss, launchPlayer");
                //delete license file once got details from it to validate
//                deleteLicenseFile();
                //getting device info from custom class which is required for php service to get license validation
                //and required information for license key to start playing may be errror response too for invalid license key
                // Log.v(LOGTAG, "deviceData " + deviceData);
                //calling async task to post device info to php service
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DeviceInfo deviceInfo = new DeviceInfo(getApplicationContext());
                        AsyncTaskGetChannelInfo postDeviceData = new AsyncTaskGetChannelInfo(ValidatorActivity.this);
                        //response of service as string
                        postDeviceData.execute(deviceInfo.getDeviceInfo(deviceId, licenseKey));
                    }
                });
               // Log.v(LOGTAG, "response " + response);
        } else {
                callMain(204, "Connect to working Internet Connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
            callMain(202, "Technical issue, Please try again later!");
        }
    }
    /**
     * This method is to show dialog on abnormal case from dashboard or Admin authentication message from
     * service
     * @param code
     * @param serviceResponse
     */
    private void callMain(final int code, final String serviceResponse) {
        //if any exception change license check status to false
        new Utils().setActivation(getApplicationContext(),0);
        //based on code show message to client
        switch (code) {
            case 204:
                Toast.makeText(activity.getApplicationContext(),serviceResponse,Toast.LENGTH_LONG).show();
                break;
            case 202:
                //delete license file once got details from it to validate or even in exception case
//                deleteLicenseFile();
                AlertDialog alertDialog = new AlertDialog(ValidatorActivity.this);
                alertDialog.showDialog(202,serviceResponse);
                //Toast.makeText(getApplicationContext(),serviceResponse,Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * show dialog to activate license
     */
    private void showDialog() {
        try {
           // Log.e(LOGTAG,"license setup not done, show dialod to activate license");
            AlertDialogValidator alertDialogValidator = new AlertDialogValidator(ValidatorActivity.this);
            alertDialogValidator.showDialog(WELCOME_MSG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is to delete License text file from sd card after activated license using License.txt
     */
    private void deleteLicenseFile() {
        File licenseData = new File(FolderAndURLConstants.FOLDER_PATH_LICENSE +"/License.txt");
        if (licenseData.exists()) {
            licenseData.delete();
        }
    }
    public static Activity getActivity() {
        return activity;
    }

    /**
     * Class used to get Bins from cloud
     */
    private class LoadLicenseAsync extends AsyncTask<String, Void, String> {

        private final boolean myLocation;

        public LoadLicenseAsync(boolean myLocation) {
            this.myLocation = myLocation;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            try {

                return null;

            } catch (Exception e) {

                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
