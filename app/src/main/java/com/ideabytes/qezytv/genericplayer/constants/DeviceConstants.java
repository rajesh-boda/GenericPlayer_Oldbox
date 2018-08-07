package com.ideabytes.qezytv.genericplayer.constants;


/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : DeviceConstants
 * author:  Suman
 * Created Date : 08-12-2015
 * Description : Device  constants to post data to servers and to construct json objects
 * Modified Date : 16-02-2016
 * Reason: apk file name getting dynamically
 *************************************************************/
public interface DeviceConstants {
    //Device info
    String DEVICE = "device_id";
    String DEVICE_MODEL = "mobile_type";
    String CLIENT_VERSION = "client_version";
    String OS_TYPE = "os_type";
    String LICENSE_KEY = "license_key";
    String DATE_TIME = "datetime";
    String GEO_LANGITUDE = "geo_lang";
    String GEO_LATITUDE = "geo_lat";
    String IP_ADDRESS = "ip_address";
    String LOCATION = "location";
    String GEO_ADDRESS = "geo_address";
    String COUNTRY = "country";
    String CITY = "city";
    //database table constants
    String CHANNEL_ID = "channel_id";
    String CHANNEL_LINK = "link";
    String CHANNEL_LOGO = "logo";
    String ACCESS_TOKEN = "access_token";
    String MESSAGE = "message";
    String CODE = "code";
    //admin message to be sent to admin email on inactive status received from dashboard
    String CODE_STREAM_OK = "100";//video playing fine
    String CODE_LICENSE_ACTIVATED = "101";//LICENSE ACTIVATED FOR THE FIRST TIME
    String CODE_DEVICE_REBOOTED = "102";//DEVICE REBOOTED
    String CODE_DEVICE_BUFFERING = "103";//DEVICE BUFFERING
    String CODE_DEVICE_DEACTIVATED = "104";//DEVICE DEACTIVATED FROM DASHBOARD
    String CODE_STREAM_NOT_AVAILABLE = "105";//STREAM IS NOT AVAILABLE
    String CODE_NO_INTERNET = "106";//NO NETWORK
    String CODE_M_LOCK = "108";//when admnin deactivated license from dashboard
    String ACTIVITY_STATUS_FALSE = "false";
    String ACTIVITY_STATUS_TRUE = "true";
    String CODE_USER_STOP = "107";//when user pressed back button(screen showing Settings page)
    String CODE_EXCEPTION = "204";//EXCEPTION IN CODE
    String SUBJECT = "subject";
    String VIEWING_DEVICE_ID = "viewing_device_unique_id";
    String DEVICE_ID = "device_id";
    String APK_VERSION = "version";
    String NOTIFICATION_TYPE = "notification_type";
    //Stream status
    String ACTIVITY_STATUS = "activityStatus";
    String INTERNET_SPEED = "InternetSpeed";//dont change keys, this is matching with server response key
    String STREAM_STATUS = "StreamStatus";//dont change keys, this is matching with server response key
    String VIDEO_TYPE = "video_type";
    String REASON = "reason";
    String CONSUMED_BIT_RATE = "consumed_bitrate";
    //state which is used in active inactive case
    String STATE_100 = "100";
    String STATE_ZERO = "0";
    String STATE_ONE = "1";
    String STATE_TWO = "2";
    String STATE_THREE = "3";
    String STATE_FOUR = "4";

    int TIMER_START_INTERVAL_UPDATE = 10;
    int TIMER_REPEAT_INTERVAL_UPDATE = 6400 ;//24 hours 86400*1000
    //timer start and repeat interval which is used to send device status to dashboard
    int TIMER_POST_STATUS_INTERVAL = 30;
    //network change permission
    String BROADCAST_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    //sleep time before restarting Octoshape system on any error
    int SLEEP_TIME = 60*1000*10;//10 minutes
    int TIME_OUT =  3*1000;

    String ALERT_MESSAGE = "Alert Message";
    String UPDATE_APK_MESSAGE = "Update Found, Do You want to Update?";
    //get apk file name from sd card that downloaded from service on an apk update
    String APK_FILE_NAME = "GenericPlayer.apk";
    String TECH_ERROR_MSG = "Technical Error,Please Try again";
    String WELCOME_MSG  = "WelCome to Generic Player";

    String STATUS = "status";
    String DATA = "data";
    String BANDWIDTHURLKEY="link";
    String RESPONSE_CODE_200 = "200";
    String RESPONSE_CODE_202 = "202";

    String INTERNET_ACTION = "com.ideabytes.qezytv.netcheck";
    String USER_STOPPED_PLAY = "User Stopped Player";
    String CHOOSE_OPTIONS = "Please Choose any of the Options";

    String SEND_STATUS_ACTION = "com.ideabytes.qezytv.genericplayer.POST.STATUS";
    String ACTION_NETWORK = "action.network.changed";
    String LICENSE = "license";
    String EXCEPTION = "Exception";
    String VALUE_202 = "202";
    String BANDWIDTHSPEED="BandwidthSpeed";
    int BANDWIDTHCHECKTIMEOUT=90000;//90secs
   // String FILEDOWNLOADURL="https://s3.amazonaws.com/ideabytes-digitalboards-prod/c54ebee133dea5c74ab3027238893ec5.mp4";
    String FILEDOWNLOADURL="https://www.airtel.in/business/assets/images/globalbusiness.mp4";
    //double fileSize=9.74;
    double fileSize=10.4;
    //int BANDWIDTHCHECKTIMEOUT=10000;//90secs
}
