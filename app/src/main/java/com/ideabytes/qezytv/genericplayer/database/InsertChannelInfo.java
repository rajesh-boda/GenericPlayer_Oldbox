package com.ideabytes.qezytv.genericplayer.database;

import android.content.ContentValues;
import android.content.Context;

import com.ideabytes.qezytv.genericplayer.constants.DBConstants;
import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

import org.json.JSONObject;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : InsertChannelInfo
 * author:  Suman
 * Created Date : 08-12-2015
 * Description : This class is used to insert channel information from database table
 * Modified Date : 16-02-2016
 * Reason:app version insterted
 *************************************************************/
public class InsertChannelInfo extends DatabaseHelper implements DBConstants {
    private final String LOGTAG = "InsertChannelInfo";
    private Context context;//used in creating database connection
    public InsertChannelInfo(Context context) {
        super(context);
        this.context = context;
    }
    /**
     * This method is to insert complete channel information  in to database table
     *
     * @param  channelInfo <Json Object channel information></Json>
     * @return rows <no of rows effected>
     */
    public long insertChannelInfo(final JSONObject channelInfo) {
        long rows  = 0;
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        try {
            //get database connection
            contentValues.put(CHANNEL_ID, channelInfo.getString(CHANNEL_ID));
            contentValues.put(CHANNEL_NAME, channelInfo.getString(CHANNEL_NAME));
            contentValues.put(CHANNEL_LINK, channelInfo.getString(CHANNEL_LINK));
            contentValues.put(CHANNEL_LOGO, channelInfo.getString(CHANNEL_LOGO));
            contentValues.put(ACCESS_TOKEN, channelInfo.getString(ACCESS_TOKEN));
            contentValues.put(VIEWING_DEVICE_ID, channelInfo.getString(VIEWING_DEVICE_ID));
            contentValues.put(DEVICE_ID, channelInfo.getString(DEVICE_ID));
            contentValues.put(VIDEO_TYPE, channelInfo.getString(VIDEO_TYPE));
            contentValues.put(DeviceConstants.APK_VERSION, channelInfo.getString(DeviceConstants.APK_VERSION));

            rows =  db.insert(TABLE_STREAM_DETAILS, null, contentValues);
        } catch (Exception e) {
            //sending exception to email
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(context);
            sendExceptionsToServer.sendMail(InsertChannelInfo.this.getClass().getName(), Utils.convertExceptionToString(e),"Exception");
        } finally {
            //close database connection
            //closeDatabaseConnection();

            db.close();
        }
        return rows;
    }
    public void insertPlayerStatus(final String status) {
        try {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            db = databaseHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_STATUS, status);
            db.insert(TABLE_PLAYER_STATUS, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //closeDatabaseConnection();
            db.close();
        }
    }
}
