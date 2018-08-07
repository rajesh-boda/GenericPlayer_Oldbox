package com.ideabytes.qezytv.genericplayer.constants;

import android.os.Environment;

import java.io.File;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : FolderAndURLConstants
 * author:  Suman
 * Created Date : 08-12-2015
 * Description : UL  constants to post data to servers or get data from servers
 * Modified Date : 02-12-2016
 * Reason: Error codes for stream status added
 *************************************************************/
public interface FolderAndURLConstants {
    //Server ip
    //dev http://192.168.1.48
    //http://www.autotestscript.com/
    //String SERVER = "http://52.66.139.53/qezymedia/tvstreaminganalytics";
   String SERVER = "http://qezymedia.com/tvstreaminganalytics";
    String SERVER_PATH =SERVER+"/api/";
    String SERVER_URL_TO_GET_CHANNEL_INFO = SERVER_PATH+"getChannelId";
    String SERVER_URL_TO_POST_STATUS = SERVER_PATH+"updateBandwidthAndStatus";
    String SERVER_URL_TO_GET_LICENSE_KEY = "http://qmdev.qezy.tv/tvstreaminganalytics/api/getDeviceLicense";


    String SERVER_URL_TO_UPDATE_CHANNEL_INFO = SERVER_PATH+"getChannelByToken";
    String SERVER_URL_TO_SEND_MAIL = SERVER_PATH+"sendMail";
    String SERVER_URL_TO_REBOOT_CONFIRM = SERVER_PATH+"updateRebootFlag";
 String SERVER_URL_TO_RESTART_CONFIRM = SERVER_PATH+"updateRestartFlag";

    String SERVER_URL_TO_GET_BANDWIDTHCHECK_URL=SERVER_PATH+"getBandwidthLink";


    String SERVER_URL_TO_SEND_NOTIFICATION = SERVER_PATH+"sendNotification";
    String SERVER_URL_TO_CONFIRM = SERVER_PATH+"updateChanneUpdateFlag";
    String SERVER_URL_TO_GET_ACTIVE = SERVER_PATH+"getStatus";
    String SERVER_URL_TO_GET_UPDATED_APK = SERVER_PATH+"getUpgradedApp";
    String SERVER_URL_TO_GET_APK_STATUS = SERVER_PATH+"getAppUpgradeStatus";
    String SERVER_URL_TO_POST_APK_UPDATE_STATUS = SERVER_PATH+"updateAppUpgradedStatus";
    //folder path constants
    String FOLDER_PATH_UPDATED_APK =  Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "GenericPlayer/Apk";
    String FOLDER_PATH_LICENSE =  Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "Android";
    String FOLDER_OCTO_LOGS =  Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "GenericPlayer/Octoshape";
    String TEXT_FILE_NAME = "Debug.txt";
    String VERSION_FILE_NAME = "Version.txt";
    String FOLDER_TO_STORE_LOGO = Environment.getExternalStorageDirectory().getAbsoluteFile()+ File.separator + "GenericPlayer/Logo";
    String FOLDER_TO_STORE_VIDEO = Environment.getExternalStorageDirectory().getAbsoluteFile()+ File.separator + "Android";
}
