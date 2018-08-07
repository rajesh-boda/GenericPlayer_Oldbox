package com.ideabytes.qezytv.genericplayer.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.ideabytes.qezytv.genericplayer.ValidatorActivity;
import com.ideabytes.qezytv.genericplayer.VideoActivity;
import com.ideabytes.qezytv.genericplayer.alertdialogs.AlertDialog;
import com.ideabytes.qezytv.genericplayer.channel.ImageStorage;
import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.constants.FolderAndURLConstants;
import com.ideabytes.qezytv.genericplayer.database.GetChannelInfo;
import com.ideabytes.qezytv.genericplayer.database.InsertChannelInfo;
import com.ideabytes.qezytv.genericplayer.model.MyResultReceiver;
import com.ideabytes.qezytv.genericplayer.model.Pojo;
import com.ideabytes.qezytv.genericplayer.network.PingIP;
import com.ideabytes.qezytv.genericplayer.services.WatchDogToCheckPlayer;
import com.ideabytes.qezytv.genericplayer.services.WatchDogToUpdateStatus;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : AsyncTaskGetChannelInfo
 * author:  Suman
 * Created Date : 08-12-2015
 * Description : Async Task to post data to get channel information based on device id and license key
 * Modified Date : 27-04-2016
 * Reason: Checking connection succces with code 200
 *************************************************************/
public class AsyncTaskGetChannelInfo extends AsyncTask<JSONObject ,String,String> implements DeviceConstants,FolderAndURLConstants {
    private static final String LOGTAG = "AsyncTaskGetChannelInfo";
    private Context context;//application context
    private ProgressDialog progressDialog = null;//to display loading status
    private String deviceId;
    public AsyncTaskGetChannelInfo(Context context) {
        this.context = context;
    }
    @Override
    protected String doInBackground(JSONObject... params) {
        String response = "202";
        String finalResponse = null;
        try {
            StringBuilder sb= null;
            deviceId = params[0].getString(DEVICE);

            String data = URLEncoder.encode("device_id", "UTF-8")
                    + "=" + URLEncoder.encode(params[0].getString(DEVICE), "UTF-8");////aaa
            data += "&" + URLEncoder.encode("license_key", "UTF-8") + "="
                    + URLEncoder.encode(params[0].getString(LICENSE_KEY), "UTF-8");

            data += "&" + URLEncoder.encode(OS_TYPE, "UTF-8") + "="
                    + URLEncoder.encode(params[0].getString(OS_TYPE), "UTF-8");
            data += "&" + URLEncoder.encode(CLIENT_VERSION, "UTF-8") + "="
                    + URLEncoder.encode(params[0].getString(CLIENT_VERSION), "UTF-8");
            data += "&" + URLEncoder.encode(DEVICE_MODEL, "UTF-8") + "="
                    + URLEncoder.encode(params[0].getString(DEVICE_MODEL), "UTF-8");
            data += "&" + URLEncoder.encode(GEO_LANGITUDE, "UTF-8") + "="
                    + URLEncoder.encode(params[0].getString(GEO_LANGITUDE), "UTF-8");
            data += "&" + URLEncoder.encode(GEO_LATITUDE, "UTF-8") + "="
                    + URLEncoder.encode(params[0].getString(GEO_LATITUDE), "UTF-8");
            data += "&" + URLEncoder.encode(IP_ADDRESS, "UTF-8") + "="
                    + URLEncoder.encode(params[0].getString(IP_ADDRESS), "UTF-8");
            data += "&" + URLEncoder.encode("device", "UTF-8") + "="
                    + URLEncoder.encode("Android", "UTF-8");
            data += "&" + URLEncoder.encode(LOCATION, "UTF-8") + "="
                    + URLEncoder.encode(params[0].getString(LOCATION), "UTF-8");
            data += "&" + URLEncoder.encode(GEO_ADDRESS, "UTF-8") + "="
                    + URLEncoder.encode(params[0].getString(GEO_ADDRESS), "UTF-8");

            data += "&" + URLEncoder.encode(COUNTRY, "UTF-8") + "="
                    + URLEncoder.encode(params[0].getString(COUNTRY), "UTF-8");
            data += "&" + URLEncoder.encode(CITY, "UTF-8") + "="
                    + URLEncoder.encode(params[0].getString(CITY), "UTF-8");
            data += "&" + URLEncoder.encode(DATE_TIME, "UTF-8") + "="
                    + URLEncoder.encode(params[0].getString(DATE_TIME), "UTF-8");

            // Defined URL  where to send data
            URL url = new URL(SERVER_URL_TO_GET_CHANNEL_INFO);
            Log.v(LOGTAG, "url " + SERVER_URL_TO_GET_CHANNEL_INFO);
            // Send POST data request
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setConnectTimeout(DeviceConstants.TIME_OUT);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write( data );
            wr.flush();
            int code = conn.getResponseCode();
            //Log.e(LOGTAG, "server response code " + code);
            if (code == 200) {
                // Get the server response
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                sb = new StringBuilder();
                String line = null;
                // Read Server Response
                while((line = reader.readLine()) != null) {
                    // Append server response in string
                    sb.append(line + "\n");
                }
                // Log.i("Server Connection", sb.toString());
                response = sb.toString();//response from service
                JSONObject jsonObject = new JSONObject(response);
                //response code from service response
                String responseCode = jsonObject.optString("responseCode").toString().trim();
                //Log.v(LOGTAG, "response for license data" + response);
                //if response code is 200 then success, we have valid response from service
                if (responseCode.equals(RESPONSE_CODE_200)) {
                    //change license check activation status to true
                    new Utils().setActivation(context, 1);
                    //get valid channel information from json object that constructed with service response
                    JSONObject serviceReponse = jsonObject.getJSONObject(DATA);
                    Log.v(LOGTAG, "serviceResponse :" + serviceReponse.toString());
                    //add current version to service json response to store into database
                    String currentVersion = new Utils(context).getApkVersion();
                    serviceReponse.put(DeviceConstants.APK_VERSION, currentVersion);
                    //object of insert class to insert data into database table
                    InsertChannelInfo insertChannelInfo = new InsertChannelInfo(context);
                    //inserting channel info to table
                    insertChannelInfo.insertChannelInfo(serviceReponse);

                    Bitmap imageLogo = ImageStorage.getImage(FOLDER_TO_STORE_LOGO);
                    //if client is empty or not downloaded properly, display generic player default_logo as client default_logo
                    if (imageLogo == null) {
                        StringBuilder sb1 = new StringBuilder();
                        GetChannelInfo getChannelInfo = new GetChannelInfo(context);
                        //download client dialog_to_show_client_logo from url to show in alert dialog
                        try
                        {
                            URL urlForLogo = new URL(getChannelInfo.getChannelInfo().getString(CHANNEL_LOGO));
                            HttpURLConnection urlConnection = (HttpURLConnection) urlForLogo.openConnection();
                            urlConnection.setRequestMethod("GET");
                            urlConnection.setDoOutput(true);
                            urlConnection.setConnectTimeout(DeviceConstants.TIME_OUT);
                            urlConnection.connect();
                            // this will be useful so that you can show a typical 0-100% progress bar
                            int fileLength = urlConnection.getContentLength();
                            String filename= "logo.jpg";
                            String dirPath = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "GenericPlayer/Logo";
                            Log.v(LOGTAG, "dirPath:" + dirPath);
                            new Utils().createGenericFolderIfRequired(dirPath);
                            Log.v(LOGTAG,"Local filename:" + filename);
                            File file = new File(dirPath,filename);
                            //when there is no image file then only download new image file
                            FileOutputStream fileOutput = new FileOutputStream(file);
                            InputStream inputStream = urlConnection.getInputStream();
                            int totalSize = urlConnection.getContentLength();
                            Log.i(LOGTAG," logo totalSize: "+ totalSize) ;
                            int downloadedSize = 0;
                            byte[] buffer = new byte[1024];
                            int bufferLength = 0;
                            while ((bufferLength = inputStream.read(buffer)) > 0) {
                                fileOutput.write(buffer, 0, bufferLength);
                                downloadedSize += bufferLength;
                                sb1.append(bufferLength + "\n");
                                publishProgress("Downloading Logo, Please wait...  " + (int) (downloadedSize * 100 / fileLength)+"%");
                            }
                            Log.i(LOGTAG," logo downloadedSize: "+downloadedSize+"  logo totalSize: "+ totalSize) ;
                            fileOutput.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //finalResponse = sb1.toString();//response from service
                        finalResponse = RESPONSE_CODE_200;
                       // Log.v(LOGTAG," downloading default_logo service call response "+response);
                    }
                }  else if (responseCode.equalsIgnoreCase(RESPONSE_CODE_202)) {
                    //when response code is 204, there may be a authentication message from service
                    String serviceReponse = jsonObject.getString(DATA);
                    callMain(202, serviceReponse);
                    finalResponse = "202";
                    SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(context);
                    sendExceptionsToServer.getDeviceId(deviceId);
                    sendExceptionsToServer.sendMail("Qezytv | Admin Notification -  " + deviceId, serviceReponse, "");
                } else {
                    finalResponse = "202";
                    //when reponse code is 200 there may be some technical errors,
                    // show alert dialog on any abnormal response from server
                    callMain(202, "Facing issues,Please try again later");
                    // AlertDialog alertDialog = new AlertDialog(activity);
                    //alertDialog.showDialog(TECH_ERROR_MSG);
                    // Toast.makeText(activity.getApplicationContext(), "Facing issues,Please try again later", Toast.LENGTH_LONG).show();
                }
            } else {
                callMain(202,"Unable to reach server, Try again later");
            }
        } catch (Exception e) {
            finalResponse = "Exception";
            //sending exception to email
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(context);
            sendExceptionsToServer.sendMail(AsyncTaskGetChannelInfo.this.getClass().getName(), Utils.convertExceptionToString(e), "Exception");
        }
        return finalResponse;
    }

    protected void onProgressUpdate(String... progress) {
        super.onProgressUpdate();
        progressDialog.setMessage(""+progress[0]);
    }
    protected void onPostExecute(String result) {
        // execution of result of Long time consuming operation
        super.onPostExecute(result);
       // Log.w(LOGTAG, "on post execute " + result);
        progressDialog.cancel();
        if (result.equalsIgnoreCase(RESPONSE_CODE_200)) {
            try {
                //send activation message to server, so than they can send info to regi email and mobile
                boolean netStatus = PingIP.isConnectingToInternet(context);
                //send an email to admin on channel inactivation
                if (netStatus) {
                    JSONObject jsonObjectq = new JSONObject();
                    jsonObjectq.put(DEVICE_ID, deviceId);
                    jsonObjectq.put(CODE, CODE_LICENSE_ACTIVATED);
                    //this sends notification mail to server
                    AsyncTaskSendNotification asyncTaskSendNotification = new AsyncTaskSendNotification(context);
                    asyncTaskSendNotification.execute(jsonObjectq);
                }
                //on valid channel information from php service store them into local database table and
                //start streaming
                Intent intent = new Intent(context, VideoActivity.class);
                context.startActivity(intent);
                //start service to update video player status to server
                Intent statusService = new Intent(context, WatchDogToUpdateStatus.class);
                MyResultReceiver resultReceiver = new MyResultReceiver(null, context);
                statusService.putExtra("receiver", resultReceiver);
                statusService.putExtra("startTimeInterval", 60);
                statusService.putExtra("repeatTimeInterval", 30);
                context.startService(statusService);
                //start service which restarts activity when video activity not on top
                Intent serviceIntent = new Intent(context, WatchDogToCheckPlayer.class);
                Pojo.getInstance().setServiceCheckPlayer(serviceIntent);
                context.startService(serviceIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPreExecute() {
        //show progress dialog on connecting to server
        progressDialog = new  ProgressDialog(context);
        progressDialog.setTitle("Retrieving Channel Info");
        progressDialog.setMessage("Please wait...");
        progressDialog.setMax(100);
        progressDialog.show();
    }
    /**
     * This method is to show dialog on abnormal case from dashboard or Admin authentication message from
     * service
     * @param code
     * @param serviceResponse
     */
    private void callMain(final int code, final String serviceResponse) {
        //if any exception, set license activation to false
        new Utils().setActivation(context, 0);
        //dismiss progress dialog which is showing
        if (progressDialog != null)
            progressDialog.cancel();
        //based on code, show message to client
        switch (code) {
            case 204:
                Toast.makeText(context,serviceResponse,Toast.LENGTH_LONG).show();
                break;
            case 202:
                ValidatorActivity.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog alertDialog = new AlertDialog(context);
                        alertDialog.showDialog(202, serviceResponse);
                    }
                });
               // Toast.makeText(context, serviceResponse, Toast.LENGTH_LONG).show();
                break;
        }
    }

private void getData(final String result) {
    Bitmap imageLogo = null;
    try {
        JSONObject jsonObject = new JSONObject(result);
        //response code from service response
        String responseCode = jsonObject.optString("responseCode").toString().trim();
       // Log.v(LOGTAG, "responseCode " + responseCode);
        //if response code is 200 then success, we have valid response from service
        if (responseCode.equals(RESPONSE_CODE_200)) {
            //change license check activation status to true
            new Utils().setActivation(context, 1);
            //get valid channel information from json object that constructed with service response
            JSONObject serviceReponse = jsonObject.getJSONObject(DATA);
            //add current version to service json response to store into database
            String currentVersion = new Utils(context).getApkVersion();
            serviceReponse.put(DeviceConstants.APK_VERSION, currentVersion);
            //object of insert class to insert data into database table
            InsertChannelInfo insertChannelInfo = new InsertChannelInfo(context);
            //inserting channel info to table
            insertChannelInfo.insertChannelInfo(serviceReponse);
           // onProgressUpdate("Retrieving Channel Info");
            //send activation message to server, so than they can send info to regi email and mobile
            final String device_id = serviceReponse.getString(DEVICE_ID);
            boolean netStatus = PingIP.isConnectingToInternet(context);
            //send an email to admin on channel inactivation
            if (netStatus) {
                JSONObject jsonObjectq = new JSONObject();
                jsonObjectq.put(DEVICE_ID, device_id);
                jsonObjectq.put(MESSAGE, CODE_DEVICE_DEACTIVATED);
                //this sends notification mail to server
                AsyncTaskSendNotification asyncTaskSendNotification = new AsyncTaskSendNotification(context);
                asyncTaskSendNotification.execute(jsonObjectq);
            }
            imageLogo = ImageStorage.getImage(FOLDER_TO_STORE_LOGO);
            // Log.e(LOGTAG, "there is a Logo "+imageLogo);
            //if client is empty or not downloaded properly, display generic player default_logo as client default_logo
            if (imageLogo == null) {
                GetChannelInfo getChannelInfo = new GetChannelInfo(context);
                //download client dialog_to_show_client_logo from url to show in alert dialog
                AsyncTaskToDownloadClientLogo asyncTaskToDownloadClientLogo = new AsyncTaskToDownloadClientLogo(context);
                asyncTaskToDownloadClientLogo.execute(getChannelInfo.getChannelInfo().getString(CHANNEL_LOGO));
            }
            //on valid channel information from php service store them into local database table and
            //start streaming
            Intent intent = new Intent(context, VideoActivity.class);
            context.startActivity(intent);
            //start service to update video player status to server
            Intent statusService = new Intent(context, WatchDogToUpdateStatus.class);
            context.startService(statusService);
            //start service which restarts activity when video activity not on top
            Intent serviceIntent = new Intent(context, WatchDogToCheckPlayer.class);
            context.startService(serviceIntent);
            //callAsynchronousTask();

        } else if (responseCode.equalsIgnoreCase(RESPONSE_CODE_202)) {
            //when response code is 204, there may be a authentication message from service
            String serviceReponse = jsonObject.getString(DATA);
            callMain(202, serviceReponse);
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(context);
            sendExceptionsToServer.getDeviceId(deviceId);
            sendExceptionsToServer.sendMail("Qezytv | Admin Notification -  " + deviceId, serviceReponse, "");
        } else {
            //when reponse code is 200 there may be some technical errors,
            // show alert dialog on any abnormal response from server
            callMain(202, "Facing issues,Please try again later");
            // AlertDialog alertDialog = new AlertDialog(activity);
            //alertDialog.showDialog(TECH_ERROR_MSG);
            // Toast.makeText(activity.getApplicationContext(), "Facing issues,Please try again later", Toast.LENGTH_LONG).show();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}
