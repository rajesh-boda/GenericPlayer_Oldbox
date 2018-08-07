package com.ideabytes.qezytv.genericplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.database.Database_for_Inactive;
import com.ideabytes.qezytv.genericplayer.database.GetChannelInfo;
import com.ideabytes.qezytv.genericplayer.database.Inactive_status;
import com.ideabytes.qezytv.genericplayer.model.Pojo;
import com.ideabytes.qezytv.genericplayer.network.ConnectivitySpeed;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : InputDeviceStatus
 * author:  Suman
 * Created Date : 02-03-2016
 * Description : This class is to construct input to post device status
 * Modified Date : 11-03-2016
 * Reason: --
 *************************************************************/
public class InputDeviceStatus implements DeviceConstants {
    private final String TAG = "InputDeviceStatus";
    private Database_for_Inactive db;

    /**
     * This method is construct input to post device status to dashboard on time interval
     * @param context
     * @return playing status as json object
     */
    public JSONObject sendStatus(final Context context) {
        JSONObject postData = new JSONObject();
        db = new Database_for_Inactive(context);
        GetChannelInfo getChannelInfo = new GetChannelInfo(context);
        DeviceInfo deviceInfo = new DeviceInfo(context);
        JSONObject channelInfo = getChannelInfo.getChannelInfo();
        try {
            postData.put(CHANNEL_ID, channelInfo.getString(CHANNEL_ID));
            postData.put(DATE_TIME, new Utils().getPresentDateTime(context));
            postData.put(DEVICE_ID, channelInfo.getString(DEVICE_ID));
            if (getChannelInfo.getStatus().equalsIgnoreCase("404")) {
                postData.put(ACTIVITY_STATUS, ACTIVITY_STATUS_FALSE);
                postData.put(STREAM_STATUS, CODE_STREAM_NOT_AVAILABLE);
            }
           else if (getChannelInfo.getStatus().equalsIgnoreCase("401")) {
                postData.put(ACTIVITY_STATUS, ACTIVITY_STATUS_FALSE);
                postData.put(STREAM_STATUS, CODE_DEVICE_DEACTIVATED);
            }
            else if (getChannelInfo.getStatus().equalsIgnoreCase("400")) {
                postData.put(ACTIVITY_STATUS, ACTIVITY_STATUS_FALSE);
                postData.put(STREAM_STATUS, CODE_DEVICE_BUFFERING);
            }
            else {
                postData.put(ACTIVITY_STATUS, ACTIVITY_STATUS_TRUE);
                postData.put(STREAM_STATUS, CODE_STREAM_OK);
            }
            postData.put(INTERNET_SPEED, ConnectivitySpeed.getSpeed(context));

            postData.put(VIDEO_TYPE, channelInfo.getString(VIDEO_TYPE));
            postData.put(IP_ADDRESS, deviceInfo.getIpAddress());
            postData.put(LOCATION, "Hyd");
            postData.put(GEO_LATITUDE, "17.123");
            postData.put(GEO_LANGITUDE, "72.124");
            postData.put(ACCESS_TOKEN, channelInfo.getString(ACCESS_TOKEN));
            SharedPreferences prefs = context.getSharedPreferences("bitrates", Context.MODE_PRIVATE);
            int bitrate2 = Pojo.getInstance().getVideoBitRate();
            String bitrate = String.valueOf(bitrate2);
            Inactive_status is = db.getStatus(1);
            String bitrate4 = is.getInactiveStatus();
            String bitrate3 = prefs.getString("br1", "0");
            String bitrate1 = prefs.getString("br", bitrate3);

            postData.put(CONSUMED_BIT_RATE, bitrate4);
            //postData.put(CONSUMED_BIT_RATE,ConnectivitySpeed.getSpeed(context));
            //postData.put(CONSUMED_BIT_RATE, Pojo.getInstance().getVideoBitRate());

            //postData.put(CONSUMED_BIT_RATE, "815");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }
}
