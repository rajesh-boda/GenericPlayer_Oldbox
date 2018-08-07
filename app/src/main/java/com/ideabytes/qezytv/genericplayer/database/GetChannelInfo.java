package com.ideabytes.qezytv.genericplayer.database;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONObject;

import com.ideabytes.qezytv.genericplayer.constants.DBConstants;
import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : GetChannelInfo
 * author:  Suman
 * Created Date : 08-12-2015
 * Description : This class is used to get channel information from database table
 * Modified Date : 16-02-2016
 * Reason:added version of the app in get channelinfo method
 *************************************************************/
public class GetChannelInfo extends DatabaseHelper implements DBConstants {
    private Context context;//used in creating database connection

    public GetChannelInfo(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * This method is to get complete channel information stored in database table
     *
     * @return <Json Object channel information></Json>
     */
    public JSONObject getChannelInfo() {
        JSONObject channelInfo = new JSONObject();
       // get database connection
        //getDatabaseConnection();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        db = databaseHelper.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from " + TABLE_STREAM_DETAILS, null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String chanelId = cursor.getString(cursor
                            .getColumnIndex(CHANNEL_ID));
                    channelInfo.put(CHANNEL_ID, chanelId);
                    String chanelName = cursor.getString(cursor
                            .getColumnIndex(CHANNEL_NAME));
                    channelInfo.put(CHANNEL_NAME, chanelName);
                    String chanelLink = cursor.getString(cursor
                            .getColumnIndex(CHANNEL_LINK));
                    channelInfo.put(CHANNEL_LINK, chanelLink);
                    String chanelLogo = cursor.getString(cursor
                            .getColumnIndex(CHANNEL_LOGO));
                    channelInfo.put(CHANNEL_LOGO, chanelLogo);
                    String accessToken = cursor.getString(cursor
                            .getColumnIndex(ACCESS_TOKEN));
                    channelInfo.put(ACCESS_TOKEN, accessToken);
                    String viewingDeviceId = cursor.getString(cursor
                            .getColumnIndex(VIEWING_DEVICE_ID));
                    channelInfo.put(VIEWING_DEVICE_ID, viewingDeviceId);
                    String deviceId = cursor.getString(cursor
                            .getColumnIndex(DEVICE_ID));
                    channelInfo.put(DEVICE_ID, deviceId);
                    String video_type = cursor.getString(cursor
                            .getColumnIndex(VIDEO_TYPE));
                    channelInfo.put(VIDEO_TYPE, video_type);
                    String version = cursor.getString(cursor
                            .getColumnIndex(DeviceConstants.APK_VERSION));
                    channelInfo.put(DeviceConstants.APK_VERSION, version);
                }
            }
        } catch (Exception e) {
            //sending exception to email
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(context);
            sendExceptionsToServer.sendMail(GetChannelInfo.this.getClass().getName(), Utils.convertExceptionToString(e),"Exception");
        } finally {
          // closeDatabaseConnection();
            cursor.close();

            db.close();
        }
        return channelInfo;
    }

    public String getStatus() {
        //getDatabaseConnection();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select status from " + TABLE_PLAYER_STATUS, null);

        try {;
            //get database connection
            while (cursor.moveToNext()) {
               return cursor.getString(cursor.getColumnIndex(COL_STATUS));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
           // closeDatabaseConnection();
            db.close();
        }
        return "404";
    }
}
