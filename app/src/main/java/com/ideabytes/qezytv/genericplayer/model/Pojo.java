package com.ideabytes.qezytv.genericplayer.model;

import android.content.Intent;

import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : Pojo
 * author:  Suman
 * Created Date : 06-04-2016
 * Description : Model class to share player status
 * Modified Date : 07-04-2016
 * Reason: --
 *************************************************************/
public class Pojo {
    private static Pojo pojo;
    private String activeStatus = DeviceConstants.ACTIVITY_STATUS_FALSE;
    private String streamStatus = DeviceConstants.CODE_STREAM_NOT_AVAILABLE;
    private int videoBitRate = 0;
    private boolean isInactive = false;
    private boolean isUserStopped = false ;
    private boolean isLogoShowing = false;
    private Intent serviceCheckPlayer;
    private boolean playerStoppedStatus = false;
    private boolean netStatus = false;
    public synchronized void setNetStatus(boolean netStatus) {
        this.netStatus = netStatus;
    }
    public synchronized boolean isNetStatus() {
        return netStatus;
    }

    public synchronized void setPlayerStoppedStatus(boolean playerStoppedStatus) {
        this.playerStoppedStatus = playerStoppedStatus;
    }

    public synchronized boolean isPlayerStoppedStatus() {
        return playerStoppedStatus;
    }
    public synchronized void setServiceCheckPlayer(Intent serviceCheckPlayer) {
        this.serviceCheckPlayer = serviceCheckPlayer;
    }

    public synchronized Intent getServiceCheckPlayer() {
        return serviceCheckPlayer;
    }
    public synchronized void setIsLogoShowing(boolean isLogoShowing) {
        this.isLogoShowing = isLogoShowing;
    }

    public synchronized boolean isLogoShowing() {
        return isLogoShowing;
    }
    public synchronized void setIsUserStopped(boolean isUserStopped) {
        this.isUserStopped = isUserStopped;
    }

    public synchronized boolean isUserStopped() {
        return isUserStopped;
    }
    public synchronized boolean isInactive() {
        return isInactive;
    }

    public synchronized void setIsInactive(boolean isInactive) {
        this.isInactive = isInactive;
    }

    public synchronized void setVideoBitRate(int videoBitRate) {
        this.videoBitRate = videoBitRate;
    }

    public synchronized int getVideoBitRate() {
        return videoBitRate;
    }
    public synchronized String getStreamStatus() {
        return streamStatus;
    }

    public synchronized String getActiveStatus() {
        return activeStatus;
    }

    public synchronized void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }

    public synchronized void setStreamStatus(String streamStatus) {
        this.streamStatus = streamStatus;
    }

    /* A private Constructor prevents any other
    * class from instantiating.
    */
    private Pojo() {

    }
    public synchronized static Pojo getInstance() {
        if (pojo == null) {
                pojo = new Pojo();
        }
        return pojo;
    }
}
