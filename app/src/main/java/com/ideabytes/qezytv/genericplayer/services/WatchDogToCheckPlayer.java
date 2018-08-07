package com.ideabytes.qezytv.genericplayer.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.ideabytes.qezytv.genericplayer.VideoActivity;
import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : WatchDogToCheckPlayer
 * author:  Suman
 * Created Date : 07-03-2016
 * Description : Service to check main activity state in the stack, to call video player when it showing home screen of STB
 * Modified Date : 07-04-2016
 * Reason: -- getting top activity logic changed
 *************************************************************/
public class WatchDogToCheckPlayer extends Service implements DeviceConstants {
    // Constant
    public static String LOGTAG = "WatchDogToCheckPlayer";
    private Timer mTimer = null;

    public String state = STATE_100;

    //changed by rajesh 04June18
    private long TIME = 30*1000;
    //changed time viplov 25May2018
   // private long TIME = 180*1000;
    private Utils utils = new Utils();
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

        @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(LOGTAG, LOGTAG + " service started ");
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new CheckForConnection(), TIME, TIME);

        return START_STICKY;
    }

    /**
     * Timer task to send status to server
     */
   private class CheckForConnection extends TimerTask {
        @Override
        public void run() {
                Log.v(LOGTAG, "activity running status from service " + isActivityRunning(VideoActivity.class.getName()));
                try {
                    // boolean netStatus = new AsyncTaskPing(getApplicationContext()).execute().get();
                    //restart the activity if it is not on top, means when it goes to home screen of the box
                    if (!isActivityRunning(VideoActivity.class.getName())) {
                        Log.v(LOGTAG, "Shall i start " + isActivityRunning(VideoActivity.class.getName()));

//
                            callPlayer("Boss , restarting player because I am in home screen of the STB");
//
                        } else {
                        Log.e(LOGTAG, " Activity running");
                    }
                        //start service to update video player status to server
                        if (!isMyServiceRunning(WatchDogToUpdateStatus.class)) {
                            Log.e(LOGTAG, " Update service not working, restarting ");
                            try {
                                Intent statusService = new Intent(WatchDogToCheckPlayer.this, WatchDogToUpdateStatus.class);
                                startService(statusService);
                            } catch (Exception e) {
                                e.printStackTrace();
                                utils.generateNoteOnSD(utils.getPresentDateTime(getApplicationContext()),e.getMessage());
                            }
                        }
                        else{
                            Log.e(LOGTAG, " Update service working");
                        }

                } catch (Exception e) {
                    e.printStackTrace();
                }
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        Log.v(LOGTAG, " service Destroyed ");
    }

    /**
     * This method is to get activity which is on top in the stack
     * @param activityClass
     * @return activity on top status
     */
    protected Boolean isActivityRunning(String activityClass) {
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
     //   Log.d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
        if (taskInfo.get(0).topActivity.getClassName().equals(activityClass)) {
            return true;
        }

        return false;
    }

    /**
     * This method to call video activity
     * @param reason <>reason why it is stopped playing video</>
     */
    private void callPlayer(final String reason) {
        Log.e(LOGTAG, reason);
        Intent dialogIntent = new Intent(WatchDogToCheckPlayer.this, VideoActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);
    }

}
