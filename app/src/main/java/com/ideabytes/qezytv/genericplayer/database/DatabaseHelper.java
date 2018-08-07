package com.ideabytes.qezytv.genericplayer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.ideabytes.qezytv.genericplayer.constants.DBConstants;
import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : DatabaseHelper
 * author:  Suman
 * Created Date : 08-12-2015
 * Description : This class is used in creating local sqlite database for the application
 * Modified Date : 16-02-2016
 * Reason: version fied added in table
 *************************************************************/
public class DatabaseHelper extends SQLiteOpenHelper implements DBConstants {
	private final String TAG = "DatabaseHelper";
	public SQLiteDatabase db;
	private Context context;

	// Constructor to create database in Phone memory
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	// Constructor to create database in SdCard memory
	public DatabaseHelper(Context context, String path) {
		super(context, Environment.getExternalStorageDirectory()
						+ "/GenericPlayer/Database/" + "/" + DATABASE_NAME, null,
				DATABASE_VERSION);
		this.context = context;
	}
	//create table stream_details to store channel info
	private static final String CREATE_TABLE_CHANNEL_INFO = "CREATE TABLE "+TABLE_STREAM_DETAILS
			+"("+CHANNEL_ID+" TEXT , "
			+ CHANNEL_NAME+" TEXT , "
			+ CHANNEL_LINK+" TEXT , "
			+ CHANNEL_LOGO+" TEXT , "
			+ ACCESS_TOKEN+" TEXT , "
			+ DEVICE_ID +" TEXT , "
			+ VIDEO_TYPE +" TEXT , "
			+ DeviceConstants.APK_VERSION +" TEXT , "
			+ VIEWING_DEVICE_ID+" TEXT "+")";
	private static final String CREATE_TABLE_PLAYER_STATUS = "CREATE TABLE "+TABLE_PLAYER_STATUS+"("+
			COL_STATUS+" TEXT )";
	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL(CREATE_TABLE_CHANNEL_INFO);
		sqLiteDatabase.execSQL(CREATE_TABLE_PLAYER_STATUS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_CHANNEL_INFO);
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_PLAYER_STATUS);
		onCreate(sqLiteDatabase);
	}

	/**
	 * This method is to create database connection
	 */
    public void getDatabaseConnection(){
		try {
			db = getWritableDatabase();
		} catch (Exception e){
			//sending exception to email
			SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(context);
			sendExceptionsToServer.sendMail(DatabaseHelper.this.getClass().getName(), Utils.convertExceptionToString(e),"Exception");
		}
    }
	/**
	 * This method is to close database connection
	 */

	public void closeDatabaseConnection(){
		try {
            if (db != null) {
                db.close();
            }
		}  catch (Exception e){
			//sending exception to email
			SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(context);
			sendExceptionsToServer.sendMail(DatabaseHelper.this.getClass().getName(), Utils.convertExceptionToString(e),"Exception");
		}
    }

}
