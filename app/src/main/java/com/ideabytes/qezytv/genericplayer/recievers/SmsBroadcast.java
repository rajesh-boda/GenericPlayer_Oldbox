package com.ideabytes.qezytv.genericplayer.recievers;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.ideabytes.qezytv.genericplayer.VideoActivity;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

public class SmsBroadcast extends BroadcastReceiver {
    
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
     
    public void onReceive(Context context, Intent intent) {
     
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
 
        try {
             
            if (bundle != null) {
                 
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                 
                for (int i = 0; i < pdusObj.length; i++) {
                     
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                     Log.d("current message", ""+currentMessage);
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    Log.d(" message1", ""+message);
                  
                    
                    Log.i("SmsReceiver", "senderName: "+ senderNum + "; number: " + message);
                     
                    if(message.contains("IB_start"))
                    {
                    	Log.d("TEST Yes", "yes Message equal to ="+message);
                    	Log.d("TEST Navigated ", "Navigated to MiniFlashPlayer");
   		             
                    	 // Show Alert
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context,
                                     "senderName: "+ senderNum + ", Number: " + message, duration);
                        toast.show();
   		        	Intent i1 = new Intent(context,VideoActivity.class);
   		        	 // i.setClassName("com.test", "com.octoshape.android.octodemoplayer.DemoPlayer");
   		        	  i1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
   		        	  i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
   		        	i1.putExtra("channel", "bjphindi");
   		        	context.startActivity(i1);
   		        	
//   		        	ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//   		        	activityManager.restartPackage("com.ideabytes.MobileReporter"); TODO
                    }
                    else
                    {
                    	Log.d("TEST Else", "No Message is not IB_start ="+message);
                    }
                  
                     
                } // end for loop
              } // bundle is null
 
        } catch (Exception e) {
            //sending exception to email
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer();
            sendExceptionsToServer.sendMail(SmsBroadcast.this.getClass().getName(), Utils.convertExceptionToString(e),"Exception");
             
        }


    }   
}