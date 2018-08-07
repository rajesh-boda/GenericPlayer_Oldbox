package com.ideabytes.qezytv.genericplayer.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import org.json.JSONObject;
import com.ideabytes.qezytv.genericplayer.ValidatorActivity;
import com.ideabytes.qezytv.genericplayer.asynctasks.AsyncTaskSendNotification;
import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.database.GetChannelInfo;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;
/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : BootReceiver
 * author:  Suman
 * Created Date : 12-12-2015
 * Description : Broadcast receiver to send rebooted message to server and start player on reboot
 * Modified Date : 23-03-2016
 * Reason: --MESSAGE key changed to CODE in sending inactive message to server
 *************************************************************/
public class BootReceiver extends BroadcastReceiver implements DeviceConstants {
	private final String LOGTAG = "BootReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Log.i(LOGTAG, "on boot received");
			Intent myIntent = new Intent(context, ValidatorActivity.class);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(myIntent);
			GetChannelInfo getChannelInfo = new GetChannelInfo(context);
            //when there is license data then send update to server on device rebooted to measure count
            //how many time device rebooted
			if (getChannelInfo.getChannelInfo().length() > 0) {
				//new Utils().sendNotification(context, DEVICE_REBOOTED);
				final String device_id = new GetChannelInfo(context).getChannelInfo().getString(DEVICE_ID);
				//send an email to admin on channel inactivation
					JSONObject jsonObject = new JSONObject();
					jsonObject.put(DEVICE_ID,device_id);
					jsonObject.put(CODE,CODE_DEVICE_REBOOTED);
					//this sends notification mail to server
					AsyncTaskSendNotification asyncTaskSendNotification = new AsyncTaskSendNotification(context);
					asyncTaskSendNotification.execute(jsonObject);
			}
		} catch (Exception e) {
			//sending exception to email
			SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer();
			sendExceptionsToServer.sendMail(BootReceiver.this.getClass().getName(), Utils.convertExceptionToString(e),"Exception");
		}
	}

}
