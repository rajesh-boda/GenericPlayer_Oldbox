package com.ideabytes.qezytv.genericplayer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;

import com.ideabytes.qezytv.genericplayer.constants.DBConstants;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : UpdateChannelInfo
 * author:  Suman
 * Created Date : 08-12-2015
 * Description : This class is used to update channel information from database table
 * Modified Date : 15-12-2015
 * Reason: Exception mail added
 *************************************************************/
public class UpdateChannelInfo extends DatabaseHelper implements DBConstants {
    private Context context;//used in creating database connection

    public UpdateChannelInfo(Context context) {
        super(context);
        this.context = context;
    }
    /**
     * This method is to update complete channel information  in to database table
     *
     * @param  channelInfo <Json Object channel information></Json>
     * @return rows <no of rows effected>
     */
    public int updateChannelInfo(final JSONObject channelInfo) {
        int rows = 0;
        SQLiteDatabase db = null;
        ContentValues contentValues = new ContentValues();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        db = databaseHelper.getWritableDatabase();
        try {
            //get database connection

            contentValues.put(CHANNEL_ID, channelInfo.getString(CHANNEL_ID));
            contentValues.put(CHANNEL_NAME, channelInfo.getString(CHANNEL_NAME));
            contentValues.put(CHANNEL_LINK, channelInfo.getString(CHANNEL_LINK));
            contentValues.put(CHANNEL_LOGO, channelInfo.getString(CHANNEL_LOGO));
            contentValues.put(ACCESS_TOKEN, channelInfo.getString(ACCESS_TOKEN));
            contentValues.put(VIEWING_DEVICE_ID, channelInfo.getString(VIEWING_DEVICE_ID));
            contentValues.put(DEVICE_ID, channelInfo.getString(DEVICE_ID));

            rows = db.update(TABLE_STREAM_DETAILS, contentValues, DEVICE_ID + " = ?",
                    new String[]{String.valueOf(channelInfo.getString(DEVICE_ID))});
        } catch (Exception e) {
            //sending exception to email
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer();
            sendExceptionsToServer.sendMail(UpdateChannelInfo.this.getClass().getName(), Utils.convertExceptionToString(e),"Exception");
        } finally {
            //close database connection
           // closeDatabaseConnection();\
            db.close();
        }
        return rows;
    }

    public void updateStatus(final String status) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();

            contentValues.put(COL_STATUS, status);
            db.execSQL("update "+TABLE_PLAYER_STATUS+" set "+COL_STATUS+" = "+status);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //closeDatabaseConnection();
            db.close();
        }
    }
}
