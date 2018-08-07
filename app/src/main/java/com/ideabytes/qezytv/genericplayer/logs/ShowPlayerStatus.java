package com.ideabytes.qezytv.genericplayer.logs;

import android.util.Log;

import com.ideabytes.qezytv.genericplayer.model.Pojo;

/**
 * Created by suman on 14/4/16.
 */
public class ShowPlayerStatus {
    public void showPlayerStatus(final String activityName) {
        Log.w(activityName,"==================================");
        Log.w(activityName,"Player Status");
        Log.w(activityName,"==================================");
        Log.w(activityName,"Player Stopped status "+Pojo.getInstance().isPlayerStoppedStatus());
        Log.w(activityName,"Player Video Bit rate "+Pojo.getInstance().getVideoBitRate());
        Log.w(activityName,"Player Inactive Status "+Pojo.getInstance().isInactive());
        Log.w(activityName,"Player Activity Status "+Pojo.getInstance().getActiveStatus());
        Log.w(activityName,"Player Stream Status "+Pojo.getInstance().getStreamStatus());
        Log.w(activityName,"Logo Showing Status "+Pojo.getInstance().isLogoShowing());
        Log.w(activityName,"User Stopped Status "+Pojo.getInstance().isUserStopped());
        Log.w(activityName,"==================================");}

}
