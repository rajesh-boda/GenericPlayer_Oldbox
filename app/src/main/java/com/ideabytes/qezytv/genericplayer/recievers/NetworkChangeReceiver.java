package com.ideabytes.qezytv.genericplayer.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ideabytes.qezytv.genericplayer.VideoActivity;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;
/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : NetworkChangeReceiver
 * author:  Suman
 * Created Date : 01-02-2016
 * Description : This BroadcastReceiver is to check network change status
 * Modified Date : 01-02-2016
 * Reason: Exception mail added
 *************************************************************/
public class NetworkChangeReceiver extends BroadcastReceiver {
    private final String LOGTAG = "NetworkChangeReceiver";
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.i(LOGTAG, "on network change received");
            try {
                Intent myIntent = new Intent(context, VideoActivity.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(myIntent);
            } catch (Exception e) {
                //sending exception to email
                SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer();
                sendExceptionsToServer.sendMail(NetworkChangeReceiver.this.getClass().getName(), Utils.convertExceptionToString(e),"Exception");
            }

    }
}
