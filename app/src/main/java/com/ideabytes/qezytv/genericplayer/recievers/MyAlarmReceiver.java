package com.ideabytes.qezytv.genericplayer.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import org.json.JSONObject;

import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.services.WatchDogToUpdateStatus;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

public class MyAlarmReceiver extends BroadcastReceiver implements DeviceConstants {
	private static final String LOGTAG = "MyAlarmReceiver";
    public static final int REQUEST_CODE = 12345;
	  // Triggered by the Alarm periodically (starts the service to run task)
	  @Override
	  public void onReceive(Context context, Intent intent) {
          try {
              Log.v(LOGTAG, "status = ");
              CharSequence Status = intent.getCharSequenceExtra("status");
              JSONObject statusObject = new JSONObject(Status.toString());
              Log.v(LOGTAG, "status = " + statusObject.toString());
              Intent i = new Intent(context, WatchDogToUpdateStatus.class);
              i.putExtra("status", statusObject.toString());
             // context.startService(i);
//              Log.d(LOGTAG, "Stream Status " + statusObject.getString(STREAM_STATUS));
//              Log.d(LOGTAG, "active Status " + statusObject.getString(ACTIVITY_STATUS));
//              Log.d(LOGTAG, "InternetSpeed " + statusObject.getString(INTERNET_SPEED));
//              Log.d(LOGTAG, "DEVICE ID " + statusObject.getString(DEVICE_ID));
//              Log.d(LOGTAG, "CHANNEL_ID " + statusObject.getString(CHANNEL_ID));
//              Log.d(LOGTAG, "LOCATION " + statusObject.getString(LOCATION));
//              Log.d(LOGTAG, "VIDEO_TYPE " + statusObject.getString(VIDEO_TYPE));
//              Log.d(LOGTAG, "REASON " + statusObject.getString(REASON));
//              Log.d(LOGTAG, "IP_ADDRESS " + statusObject.getString(IP_ADDRESS));
//              Log.d(LOGTAG, "GEO_LANGITUDE " + statusObject.getString(GEO_LANGITUDE));
//              Log.d(LOGTAG, "GEO_LATITUDE " + statusObject.getString(GEO_LATITUDE));
//              Log.d(LOGTAG, "CONSUMED_BIT_RATE " + statusObject.getString(CONSUMED_BIT_RATE));
//              Log.d(LOGTAG, "VIEWING_DEVICE_ID " + statusObject.getString(VIEWING_DEVICE_ID));
//              Log.d(LOGTAG, "ACCESS_TOKEN " + statusObject.getString(ACCESS_TOKEN));
              context.startService(i);
     } catch (Exception e) {
              //sending exception to email
              SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer();
              sendExceptionsToServer.sendMail(MyAlarmReceiver.this.getClass().getName(), Utils.convertExceptionToString(e),"Exception");
          }
      }
	}
