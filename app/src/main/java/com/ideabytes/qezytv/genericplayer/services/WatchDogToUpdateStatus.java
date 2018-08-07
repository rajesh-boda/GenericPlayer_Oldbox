package com.ideabytes.qezytv.genericplayer.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.ideabytes.qezytv.genericplayer.VideoActivity;
import com.ideabytes.qezytv.genericplayer.asynctasks.AsyncTaskConfirm;
import com.ideabytes.qezytv.genericplayer.asynctasks.AsyncTaskReGetChannelInfo;
import com.ideabytes.qezytv.genericplayer.asynctasks.AsyncTaskSendNotification;
import com.ideabytes.qezytv.genericplayer.asynctasks.AsyncTaskToDownloadClientLogo;
import com.ideabytes.qezytv.genericplayer.asynctasks.AsyncTaskToRebootConfirm;
import com.ideabytes.qezytv.genericplayer.asynctasks.AsyncTaskToRestartConfirm;
import com.ideabytes.qezytv.genericplayer.asynctasks.AsyncTaskToUpdateChannelInfo;
import com.ideabytes.qezytv.genericplayer.autoupdate.AsyncTaskToDownloadApk;
import com.ideabytes.qezytv.genericplayer.channel.ImageStorage;
import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.constants.FolderAndURLConstants;
import com.ideabytes.qezytv.genericplayer.database.GetChannelInfo;
import com.ideabytes.qezytv.genericplayer.database.UpdateChannelInfo;
import com.ideabytes.qezytv.genericplayer.logs.Logger;
import com.ideabytes.qezytv.genericplayer.model.Pojo;
import com.ideabytes.qezytv.genericplayer.network.PingIP;
import com.ideabytes.qezytv.genericplayer.utils.DeviceInfo;
import com.ideabytes.qezytv.genericplayer.utils.InputDeviceStatus;
import com.ideabytes.qezytv.genericplayer.utils.Utils;
import com.ideabytes.qezytv.genericplayer.volley.SendPayerStatusToServer;

import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.ExecutionException;


/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : WatchDogToUpdateStatus
 * author:  Viplov
 * Created Date : 06-04-2016
 * Description : This service is to update device status to dashboard
 * Modified Date : 25-04-2017
 * Reason: Network checking code changed
 *************************************************************/
public class WatchDogToUpdateStatus extends Service implements DeviceConstants {
    // Constant
    private String LOGTAG = "WatchDogToUpdateStatus";
    private String state = STATE_100;
    private String old_state = "200";
    private UpdateChannelInfo updateChannelInfo;
    private  int time_count = 0;
    private boolean downloading = false;
    private Utils utils = new Utils();
    private Handler handler = new Handler();
    @Override
    public void onCreate() {
        Log.v(LOGTAG, LOGTAG + " service created ");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Log.v(LOGTAG, LOGTAG + " service started ");
            deviceStatus(20, 30);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(timedTask);
        Log.v(LOGTAG, " service Destroyed ");
    }



    /**
     * This method is to post device status to service on a particular interval of time
     * @param statusObject
     */
    private void postDeviceStatus(final JSONObject statusObject) {

        Log.v(LOGTAG, statusObject.toString());
        try {
            boolean netStatus = PingIP.isConnectingToInternet(getApplicationContext());

            if (netStatus) {
                SendPayerStatusToServer sendPayerStatusToServer = new SendPayerStatusToServer(getApplicationContext());
                sendPayerStatusToServer=null;
                SendPayerStatusToServer.sendStatus(new SendPayerStatusToServer.VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Log.v(LOGTAG,"postDeviceStatus response "+response);
                        try {
                            String responseCode = response.optString("responseCode").toString().trim();
                            //String responseCode = "204";
                            // Logger.logDebug(LOGTAG, "postDeviceStatus", "responseCode " + responseCode);
                            if (responseCode.equalsIgnoreCase(RESPONSE_CODE_200)) {
                                String status = response.getJSONArray(DATA).getJSONObject(0).getString(STATUS);
                                //  Log.v(LOGTAG," active state " + state);
                                Log.v(LOGTAG," post status " + status);
                                // if (!state.equalsIgnoreCase(status)) {
                                //just show dialog_to_show_client_logo and stop playing video until status received as 1
                                if (status.equalsIgnoreCase(STATE_ZERO)) {
                                    Log.v(LOGTAG," De-activation called " + status);
                                    Utils.sendStatus(getApplicationContext(), 401);
                                    //sendActivationNotification(CODE_DEVICE_DEACTIVATED);
                                } else if (status.equalsIgnoreCase(STATE_ONE)) {
                                    GetChannelInfo getChannelInfo = new GetChannelInfo(getApplicationContext());
                                    if (!getChannelInfo.getStatus().equalsIgnoreCase("404")) {
                                        Utils.sendStatus(getApplicationContext(), 200);
                                    }
                                } else if (status.equalsIgnoreCase(STATE_TWO)) {

//                                            if(update_started == false) {
                                    Log.d(LOGTAG, "Now the system will update the software APK");
                                    software_update();
//                                                update_started = true;
//                                            }
//                                            else{
//
//                                            }
                                    //if status is 4 means we have update complete channel info in local database

                                    //updateChannelInfo();
                                } else if (status.equalsIgnoreCase(STATE_THREE)) {
                                    reboot();
                                    //if status is 3 means we have update complete channel info in local database
                                    Log.i(LOGTAG, "Now the system will be re-booted");
                                }
                                else if (status.equalsIgnoreCase(STATE_FOUR)){
                                    //No feature added into it yet
                                    restartApp();
                                    Log.i(LOGTAG, "No feature added in to it yet");
                                }
                                else {
                                    //restart player on active from dashboard (received status as 1)
                                    if (!state.equalsIgnoreCase(STATE_100)) {
                                        Log.e(LOGTAG, "Device activated");

                                    }
                                }
                                // }
                                state = status;
                            }
                            else if (responseCode.equalsIgnoreCase(RESPONSE_CODE_202)) {
                                Log.e(LOGTAG, "Insufficient inputs,just keep calm");
                                activateLicense();


                            } else {
                                Log.e(LOGTAG, "Server not responding(404),just keep calm");
                            }
                            // after posting device status to server delete cache
                            new Utils().deleteCache(getApplicationContext());
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(LOGTAG,"Exception from server post status,error is " + e);
                        }
                    }

                    @Override
                    public void onFailure(JSONObject response) {
                        Log.e(LOGTAG, "Server not responding(404),just keep calm");
                        startService(new Intent(WatchDogToUpdateStatus.this, WatchDogToUpdateStatus.class));
                    }
                }, statusObject);

            } else {
                Log.e(LOGTAG,"network lost so connot send status to server");
            }
        } catch (Exception e) {
            //when there is any exception from service just show client default_logo
            utils.generateNoteOnSD(utils.getPresentDateTime(getApplicationContext()),e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * This method is to update channel info when there is update from server end in channel info
     *
     */
    private void updateChannelInfo() {
        try {
            final GetChannelInfo getChannelInfo = new GetChannelInfo(getApplicationContext());

            JSONObject inputToService = getChannelInfo.getChannelInfo();
            AsyncTaskToUpdateChannelInfo asyncTaskToUpdateChannelInfo = new AsyncTaskToUpdateChannelInfo(getApplicationContext());
            String response = asyncTaskToUpdateChannelInfo.execute(inputToService).get();
            JSONObject jsonObjectUpdateInfo = new JSONObject(response);
            String responseCode = jsonObjectUpdateInfo.optString("responseCode").toString().trim();
            //  Logger.logDebug(LOGTAG, "updateChannelInfo()", "responseCode " + responseCode);
            // Logger.logDebug(LOGTAG, "updateChannelInfo()", "channelInfo " + response);
            // final String serviceData = jsonObjectUpdateInfo.optString(DATA).toString().trim();
            UpdateChannelInfo updateChannelInfo = new UpdateChannelInfo(getApplicationContext());
            if (responseCode.equalsIgnoreCase(RESPONSE_CODE_200)) {
                //when response is possitive , get updated info from server
                JSONObject serviceReponse = jsonObjectUpdateInfo.getJSONObject(DATA);
                //store data into database,update license details and channel info with new data
                updateChannelInfo.updateChannelInfo(serviceReponse);
                //get dialog_to_show_client_logo link from database(updated one)
                String logoLink = getChannelInfo.getChannelInfo().getString(CHANNEL_LOGO);
                //after updating client info to db, also download latest dialog_to_show_client_logo from server
                //before that delete old logo if any exists
                Bitmap imageLogo = ImageStorage.getImage(FolderAndURLConstants.FOLDER_TO_STORE_LOGO);
                // Log.e(LOGTAG, "there is a Logo "+imageLogo);
                //if client is empty or not downloaded properly, display generic player default_logo as client default_logo
                if (imageLogo == null) {
                    //download client dialog_to_show_client_logo from url to show in alert dialog
                    AsyncTaskToDownloadClientLogo asyncTaskToDownloadClientLogo = new AsyncTaskToDownloadClientLogo(getApplicationContext());
                    asyncTaskToDownloadClientLogo.execute(logoLink);
                }
                //after updating with new data restart the system, so that
                //channel link and default_logo will be updated
                //post confirmation on info updated , to server
                postConfirmationOnUpdate();
                //after updating channel updated status to server, call Video Activity to load new channel info
                VideoActivity.instance.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(LOGTAG,"After update channel info, restarting player");
                        VideoActivity.instance.showClientLogo(2);
                        VideoActivity.instance.error("After update channel info, restarting player");
                    }
                });
            }
        } catch (Exception e) {
            //when there is any exception from service just show client default_logo
            utils.generateNoteOnSD(utils.getPresentDateTime(getApplicationContext()), e.getMessage());
        }
    }

    /**
     * This method is to send confirmation to service on updating channel info yo database
     */
    private void postConfirmationOnUpdate() {
        try {
            GetChannelInfo getChannelInfo = new GetChannelInfo(getApplicationContext());
            JSONObject inputToService = getChannelInfo.getChannelInfo();
            SendPayerStatusToServer sendPayerStatusToServer = new SendPayerStatusToServer(getApplicationContext());
            sendPayerStatusToServer.postConfirmation(new SendPayerStatusToServer.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    Log.e(LOGTAG,"Updating channel done, sending confirmation to server");
                    // Logger.logDebug(LOGTAG, "postConfirmationOnUpdate()", " confirm " + inputToService);
                    String responseCode = response.optString("responseCode").toString().trim();
                    Logger.logDebug(LOGTAG, "postConfirmationOnUpdate()", "responseCode " + responseCode);
                    final String serviceData = response.optString(DATA).toString().trim();
//            if (responseCode.equalsIgnoreCase(RESPONSE_CODE_200)) {
//                String status = jsonObject.getJSONArray("data").getJSONObject(0).getString(STATUS);
//            }
                }

                @Override
                public void onFailure(JSONObject response) {
                    Log.e(LOGTAG,"Updating channel failed");
                }
            },inputToService);

        } catch (Exception e) {
            e.printStackTrace();
            utils.generateNoteOnSD(utils.getPresentDateTime(getApplicationContext()), e.getMessage());
        }
    }
    /**
     * This method is to create alert dialog when client inactivated in dashboard, have to show alert message
     * from service and close the application and kill complete user information from database
     * @param message
     */
    private void sendActivationNotification(final String message) {
        try {
            Log.e(LOGTAG, "Boss,Admin Inactivated me");
            final String device_id = new GetChannelInfo(getApplicationContext()).getChannelInfo().getString(DEVICE_ID);
            boolean netStatus = PingIP.isConnectingToInternet(getApplicationContext());
            //send an email to admin on channel inactivation
            if (netStatus) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(DEVICE_ID,device_id);
                jsonObject.put(CODE, message);
                //this sends notification mail to server
                AsyncTaskSendNotification asyncTaskSendNotification = new AsyncTaskSendNotification(getApplicationContext());
                asyncTaskSendNotification.execute(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
            utils.generateNoteOnSD(utils.getPresentDateTime(getApplicationContext()),e.getMessage());
        }
    }
    private void setPlayerState(final String activeStatus,final String streamStatus) {
        Pojo pojo = Pojo.getInstance();
        pojo.setStreamStatus(streamStatus);
        pojo.setActiveStatus(activeStatus);
    }


    /**
     * Timer to send video playing status to service in frequent time intervals (30 seconds)
     * /**
     * Time to update player state to ResultReceiver
     * Response code 404 states that player stopped
     * Response code 200 state that player running fine,
     * Response code 401 License de-activated,
     * Response code 201 License re-activated,
     * Based on Error codes ResultReceiver tunes player
     */
    private void deviceStatus(final int startTime,final int repeatInterval) {
//        Timer timer = new Timer();
////
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                GetChannelInfo getChannelInfo = new GetChannelInfo(getApplicationContext());
//                if (!getChannelInfo.getStatus().equalsIgnoreCase(old_state)){
//                    old_state = getChannelInfo.getStatus().toString();
//                    Log.e(LOGTAG,"old_state is " + old_state);
//                    try {
//                        sendStatus();
//                    } catch (Exception e) {
//                        // Toast.makeText(getApplicationContext()," net ex",Toast.LENGTH_LONG).show();
//                        e.printStackTrace();
//                        utils.generateNoteOnSD(utils.getPresentDateTime(getApplicationContext()), e.getMessage());
//                    }
//
//                }
//
//                if(time_count > 10) {
//                    Log.e(LOGTAG, "demon update status working");
//                    try {
//                        sendStatus();
//                    } catch (Exception e) {
//                        // Toast.makeText(getApplicationContext()," net ex",Toast.LENGTH_LONG).show();
//                        e.printStackTrace();
//                        utils.generateNoteOnSD(utils.getPresentDateTime(getApplicationContext()), e.getMessage());
//                    }
//                    time_count = 0;
//                }else{
//                    time_count++;
//                }
//            }
//        };
//        // here we can set the timer to reset watchdog time
//        timer.schedule(timerTask, 2 * 1000, 1 * 1000);

        handler.post(timedTask);

    }
    private Runnable timedTask = new Runnable(){

        @Override
        public void run() {
            GetChannelInfo getChannelInfo = new GetChannelInfo(getApplicationContext());
            if (!getChannelInfo.getStatus().equalsIgnoreCase(old_state)){
                old_state = getChannelInfo.getStatus().toString();
                Log.e(LOGTAG,"old_state is " + old_state);
                try {
                    sendStatus();
                } catch (Exception e) {
                    // Toast.makeText(getApplicationContext()," net ex",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    utils.generateNoteOnSD(utils.getPresentDateTime(getApplicationContext()), e.getMessage());
                }

            }

            if(time_count > 10) {
                Log.e(LOGTAG, "demon update status working");
                try {
                    sendStatus();
                } catch (Exception e) {
                    // Toast.makeText(getApplicationContext()," net ex",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    utils.generateNoteOnSD(utils.getPresentDateTime(getApplicationContext()), e.getMessage());
                }
                time_count = 0;
            }else{
                time_count++;
            }
            handler.postDelayed(timedTask, 2000);
        }};
    /**
     * For re-activating the License
     */

    private void activateLicense() {
        try {
            // Log.v(LOGTAG, "license data " + licenseData.exists()+" file path "+licenseData.getAbsolutePath());
            //when there is no text file, show license dialog to take device id and license key
            //if license text file is available, then get details from file and get license details
            File file = new File(FolderAndURLConstants.FOLDER_PATH_LICENSE, "License.txt");
            if (file.length() == 0) {
                Log.e(LOGTAG,"License text file empty");
               // callMain(202, "License text file is empty");
                // file empty
            } else {
                // not empty
                String content = new Utils().readTextFile(FolderAndURLConstants.FOLDER_PATH_LICENSE, "License");
                //text file available read device id and license key from the file
                // Log.v(LOGTAG, "Boss, there is a license text file "+content);
                if (content.endsWith("::")) {
                    // Log.v(LOGTAG, "Boss, key is missing ");
                   // callMain(202, "License key is missing from License text file");
                } else if (content.startsWith("::")) {
                    // Log.v(LOGTAG, "Boss, id is missing ");
                   // callMain(202, "Device id is missing from License text file");
                } else if (!content.contains("::")) {
                    //Log.v(LOGTAG, "Boss, wrong format ");
                   // callMain(202, "License text file in wrong format");
                }
                else {
                    String deviceId = content.split("::")[0];
                    //Log.v(LOGTAG, "device id " + deviceId);
                    String licenseKey = content.split("::")[1];
                    // Log.v(LOGTAG, "licenseKey " + licenseKey);
                    // Log.v(LOGTAG, "Boss, start player ");
                    launchPlayer(deviceId, licenseKey);
                }
            }
        } catch (Exception e) {
            // Log.e(LOGTAG,"exception ");
           // callMain(202, "Facing technical issues, Please try again!");
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

//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                        DeviceInfo deviceInfo = new DeviceInfo(getApplicationContext());
                        AsyncTaskReGetChannelInfo postDeviceData = new AsyncTaskReGetChannelInfo(getApplicationContext());
                        //response of service as string
                        postDeviceData.execute(deviceInfo.getDeviceInfo(deviceId, licenseKey));
//                    }
//                });
                // Log.v(LOGTAG, "response " + response);
            } else {
               // callMain(204, "Connect to working Internet Connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
           // callMain(202, "Technical issue, Please try again later!");
        }
    }
    /**
     * this method is for rebooting device
     */
    private void reboot() {

        try {
            GetChannelInfo getChannelInfo = new GetChannelInfo(getApplicationContext());
            JSONObject input = getChannelInfo.getChannelInfo();
            AsyncTaskToRebootConfirm asyncTaskToRebootConfirm = new AsyncTaskToRebootConfirm(getApplicationContext());
            String response = asyncTaskToRebootConfirm.execute(input).get();

            //start service to update video player status to server
            if (!isMyServiceRunning(WatchDogToUpdateStatus.class)) {
                try {
                    Intent statusService = new Intent(this, WatchDogToUpdateStatus.class);
                    stopService(statusService);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!isMyServiceRunning(WatchDogToCheckPlayer.class)) {
                try {
                    Intent statusService = new Intent(this, WatchDogToCheckPlayer.class);
                    stopService(statusService);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.e (LOGTAG,"Rebooting channel done, sending confirmation to server"+response);
            //commented by rajesh 04June18
            //Process rebootProcess = Runtime.getRuntime().exec("adb shell am broadcast -a android.intent.action.BOOT_COMPLETED");

            ////////////////////////////////////////////////////////////////////////
            Process proc=null;
            try {
                proc = Runtime.getRuntime().exec(new String[] { "su", "-c", "reboot" });
                //proc.waitFor();
            } catch (Exception ex) {
                Log.i(LOGTAG, "Could not reboot", ex);
            }
            if (proc != null)
            {
                try
                {
                    proc.waitFor();
                }
                catch (InterruptedException e)
                {
                    Log.i(LOGTAG, "Could not reboot2", e);
                    // Now handle this exception.
                }
            }
            ////////////////////////////////////////////////////

        }
        /*
        catch (IOException e) {
            e.printStackTrace();
        }
        */
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    /**
     * this method is for restarting the application with out going to the home screen. Instead of goin to the
     * home screen this app will start the GP_installer app which will show the Logo
     */
    private void restartApp(){
        try {

            Log.i(LOGTAG, "restarting app537");
            GetChannelInfo getChannelInfo = new GetChannelInfo(getApplicationContext());
            JSONObject input = getChannelInfo.getChannelInfo();
            AsyncTaskToRestartConfirm asyncTaskToRestartConfirm = new AsyncTaskToRestartConfirm(getApplicationContext());

            String response = asyncTaskToRestartConfirm.execute(input).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //by Rajesh 28June18--- need to kill the proces and need to is myservice running service.


       // https://stackoverflow.com/questions/15564614/how-to-restart-an-android-application-programmatically
        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);




//        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.ib.viplov.gp_installer");
//        if (launchIntent != null) {
//            launchIntent.putExtra("message", "restart");
//
//            startActivity(launchIntent);//null pointer check in case package name was not found
//            //VideoActivity.getActivity().finish();
//            //context.stopService(new Intent(context.getApplicationContext(), WatchDogToUpdateStatus.class));
//        }
//        String nameOfProcess = "com.ideabytes.qezytv.genericplayer";
//        ActivityManager  manager = (ActivityManager)WatchDogToUpdateStatus.this.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> listOfProcesses = manager.getRunningAppProcesses();
//        for (ActivityManager.RunningAppProcessInfo process : listOfProcesses)
//        {
//            if (process.processName.contains(nameOfProcess))
//            {
//                Log.e("Proccess" , process.processName + " : " + process.pid);
//                android.os.Process.killProcess(process.pid);
//                android.os.Process.sendSignal(process.pid, android.os.Process.SIGNAL_KILL);
//                manager.killBackgroundProcesses(process.processName);
//                break;
//            }
//        }

    }
    /**
     * this method is for apk updation
     */
    private void software_update() {
        if(!downloading) {
            try {
                downloading = true;
                GetChannelInfo getChannelInfo = new GetChannelInfo(getApplicationContext());
                JSONObject input = getChannelInfo.getChannelInfo();
                AsyncTaskToDownloadApk asyncTaskToDownloadApk = new AsyncTaskToDownloadApk(getApplicationContext());
                String response = asyncTaskToDownloadApk.execute(input).get();
                AsyncTaskConfirm asyncTaskToConfirm = new AsyncTaskConfirm(getApplicationContext());
                String response1 = asyncTaskToConfirm.execute(input).get();
                Log.e(LOGTAG, "Software updating, sending confirmation to server" + response + response1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    protected void setDownload (Boolean down){
        downloading = down;
    }
    private void sendStatus() {
        try {
            //int bitrate = Pojo.getInstance().getVideoBitRate();
            updateChannelInfo = new UpdateChannelInfo(getApplicationContext());

            SharedPreferences prefs = getSharedPreferences("bitrates", Context.MODE_PRIVATE);
            String bitrate1 = prefs.getString("br", "");
            Log.v(LOGTAG, "Bit rate from sendStatus " + bitrate1);

            boolean netStatus = PingIP.isConnectingToInternet(getApplicationContext());
            GetChannelInfo getChannelInfo = new GetChannelInfo(getApplicationContext());
            Log.v(LOGTAG, "Player status from db " + getChannelInfo.getStatus());
            if (getChannelInfo.getStatus().equalsIgnoreCase("404")) {
                Log.v(LOGTAG, "Player Stopped");
                Utils.sendStatus(getApplicationContext(), 404);


                if (netStatus)
                    postDeviceStatus(new InputDeviceStatus().sendStatus(getApplicationContext()));
                //no stream case

            }else if (getChannelInfo.getStatus().equalsIgnoreCase("401")) {
                Log.v(LOGTAG, "inactivated");
                // Utils.sendStatus(getApplicationContext(), 401);
                if (netStatus)
                    postDeviceStatus(new InputDeviceStatus().sendStatus(getApplicationContext()));
            }
            else if (getChannelInfo.getStatus().equalsIgnoreCase("400")) {
                Log.v(LOGTAG, "Buffering");
                if (netStatus)
                    postDeviceStatus(new InputDeviceStatus().sendStatus(getApplicationContext()));
            }else {
                Log.v(LOGTAG, "Player running");
                Utils.sendStatus(getApplicationContext(), 200);
                if (netStatus)
                    postDeviceStatus(new InputDeviceStatus().sendStatus(getApplicationContext()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is to check service running status
     * @param serviceClass
     * @return true or false
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
