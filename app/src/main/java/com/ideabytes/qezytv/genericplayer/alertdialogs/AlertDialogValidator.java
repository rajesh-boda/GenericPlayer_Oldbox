package com.ideabytes.qezytv.genericplayer.alertdialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ideabytes.qezytv.genericplayer.R;

import org.json.JSONObject;

import com.ideabytes.qezytv.genericplayer.Settings;
import com.ideabytes.qezytv.genericplayer.asynctasks.AsyncTaskGetChannelInfo;
import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.constants.FolderAndURLConstants;
import com.ideabytes.qezytv.genericplayer.database.DatabaseHelper;
import com.ideabytes.qezytv.genericplayer.utils.DeviceInfo;
import com.ideabytes.qezytv.genericplayer.network.PingIP;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : AlertDialogValidator
 * author:  Suman
 * Created Date : 08-12-2015
 * Description : Customized dialog to validate customer based on device id and license key,this appears once in life time
 * of application installation
 * Modified Date : 25-04-2016
 * Reason:Network checking changed
 *************************************************************/

public class AlertDialogValidator extends Dialog implements DeviceConstants,FolderAndURLConstants {
    private static final String LOGTAG = "AlertDialogValidator";
    private Dialog dialog;
    private Activity activity; //Activity reference to show alert dialog
    private boolean editTextDeviceId = false;
    public AlertDialogValidator(Activity activity) {
       super(activity);
        this.activity = activity;
    }

    /**
     * This alert dialog is to validate user based on device id and license key
     * Author : Suman
     * @param message <>title of the alert dialog</>
     */
    public void showDialog(final String message) {
        dialog = new Dialog(activity, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this is not to close alert dialog on out side touch
        dialog.setCanceledOnTouchOutside(false);
        //setting view to alert dialog
        dialog.setContentView(R.layout.custon_dialog_validator);
        //settings params to dialog
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //title of the alert dialog
        final TextView tvTilte = (TextView) dialog.findViewById(R.id.tvTitle);
        tvTilte.setText("");//TODO if you dont want to show Generic Player title remove this
        //edit text to device id
        final EditText etDeviceId = (EditText) dialog.findViewById(R.id.etDeviceId);
        //Edit text to license key
        final EditText etLicenseKey = (EditText) dialog.findViewById(R.id.etLicenseKey);
        //if device with sd card creates database in sd card else creates in phone memory
        if (isSDcardPresent()) {
            //creates databse in specified path in sd card
            new DatabaseHelper(activity, "");
        } else {
            //creates database in phone memory /Android/data/package name/db name
            new DatabaseHelper(activity);
        }
        //button to close alert dialog and its click listener
        final Button btnConnect = (Button) dialog.findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         try {
                                            // boolean netStatus = new ConnectionDetector(activity.getApplicationContext()).isURLReachable();
                                             boolean netStatus = PingIP.isConnectingToInternet(activity.getApplicationContext());
                                            // Log.v(LOGTAG, "net status " + netStatus);
                                             if (netStatus) {
                                                 //get internet connectivity status
                                                 // Toast.makeText(activity.getApplicationContext(), statusNet, Toast.LENGTH_LONG).show();
                                                 //if there is network
                                                 //retrieve device id value from device id edit text
                                                 final String deviceId = etDeviceId.getText().toString().trim();
                                                 //retrieve license key value from license key edit text
                                                 final String licenseKey = etLicenseKey.getText().toString().trim();
                                                 if (deviceId.isEmpty() && licenseKey.isEmpty()) {
                                                     callMain(204, "Please Enter Device ID and License Key");
                                                 } else if (deviceId.isEmpty()) {
                                                     callMain(204, "Please Enter Device ID");
                                                 } else if (licenseKey.isEmpty()) {
                                                     callMain(204, "Please Enter License Key");
                                                 } else {
                                                     //call dismiss so that it wont through windows leaked exception
                                                     if (dialog.isShowing()) {
                                                         dialog.dismiss();
                                                     }
                                                     DeviceInfo deviceInfo = new DeviceInfo(activity.getApplicationContext());
                                                     JSONObject deviceData = deviceInfo.getDeviceInfo(deviceId, licenseKey);
                                                     // Log.v(LOGTAG, "deviceData " + deviceData);
                                                     //calling async task to post device info to php service
                                                     AsyncTaskGetChannelInfo postDeviceData = new AsyncTaskGetChannelInfo(activity);
                                                     //response of service as string
                                                     postDeviceData.execute(deviceData);
                                                     //Log.v(LOGTAG, "response " + response);
                                                 }
                                             } else {
                                                 callMain(204, "Connect to working Internet Connection");
                                                 // Toast.makeText(activity.getApplicationContext(), "Please Enter Required fields", Toast.LENGTH_SHORT).show();
                                             }
                                         } catch (Exception e) {
                                             Log.v(LOGTAG, "exception ");
                                             e.printStackTrace();
                                             // System.out.println("in exception");
                                             //on any exception in first service show dialog with technical error message
                                             //before that dismiss dialog
                                             //  dialog.cancel();
                                             etDeviceId.setText("");
                                             etLicenseKey.setText("");
                                             // AlertDialog alertDialog = new AlertDialog(activity);
                                             //alertDialog.showDialog(TECH_ERROR_MSG);
                                             callMain(202, "Technical issue, Please try again later!");
                                             //sending exceptions to email
                                             SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(activity.getApplicationContext());
                                             sendExceptionsToServer.sendMail(AlertDialogValidator.this.getClass().getName(), Utils.convertExceptionToString(e), "Exception");
                                             //  System.exit(0);
                                         }//catch
                                     }
                                 }
        );
        //settings button click listener in license dialog
        Button btnSettings = (Button) dialog.findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if dialog is showing dismiss dialog
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                new Utils().setActivation(activity.getApplicationContext(), 0);
                //navigate to Settings page
                Intent intent = new Intent(activity, Settings.class);
                intent.putExtra(MESSAGE,CHOOSE_OPTIONS);
                activity.startActivity(intent);
                activity.finish();
            }
        });
        //button click listener of dialog
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //restricting user on back button click, not go out of application
                    //  Log.v(LOGTAG," dialog back button pressed ");
                    return true;
                    // Toast.makeText(activity.getApplicationContext(),"Please enter Device id and License key",Toast.LENGTH_LONG).show();
                } else if (keyCode == KeyEvent.KEYCODE_DEL) {
                    // when back button overridden, edit text clearing will not work, so it programmatically
                    // Log.e(LOGTAG,"delete");
                    if (editTextDeviceId) {
                        clearText(etDeviceId);
                    } else {
                        clearText(etLicenseKey);
                    }
                }

                return false;
            }
        });
        //to check edit text focus
        etDeviceId.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editTextDeviceId = true;//assign device id edit text focused true
                return false;
            }
        });
        //to check edit text focus
        etLicenseKey.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editTextDeviceId = false;//assign device id edit text focused false, because license key edit text focused now
                return false;
            }
        });
        dialog.show();
        }//showDialog
    /**
     * This mehod is to return sd card status
     * @return sd card status (true/false)
     */
    public static boolean isSDcardPresent() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }
    /**
     * This method is to show dialog on abnormal case from dashboard or Admin authentication message from
     * service
     * @param code
     * @param serviceResponse
     */
    private void callMain(final int code, final String serviceResponse) {
        //if any exception change license check status to false
        new Utils().setActivation(activity.getApplicationContext(), 0);
        //based on code show message to client
        switch (code) {
            case 204:
                Toast.makeText(activity.getApplicationContext(),serviceResponse,Toast.LENGTH_LONG).show();
                break;
            case 202:
               // Logger.logVerbose(LOGTAG, " callMain ", " code: " + code);
                //Toast.makeText(activity.getApplicationContext(),serviceResponse,Toast.LENGTH_LONG).show();
                AlertDialog alertDialog = new AlertDialog(activity);
                alertDialog.showDialog(202,serviceResponse);
                break;
        }
    }
    //this method is to clear text from edit texts when click delete button in key board
    private void clearText(final EditText editText) {
        if(editText.getText().toString().trim().length() > 0) {
            String result = editText.getText().toString().substring(0,editText.getText().toString().length() - 1);
            editText.setText(result);
            editText.setSelection(result.length());
        }
    }
    }//class
