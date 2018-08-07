package com.ideabytes.qezytv.genericplayer.utils;

import android.content.Context;

import com.ideabytes.qezytv.genericplayer.asynctasks.AsyncTaskToSendExceptionEmail;
import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.constants.FolderAndURLConstants;
import com.ideabytes.qezytv.genericplayer.logs.Logger;
import com.ideabytes.qezytv.genericplayer.network.PingIP;

import org.json.JSONObject;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : SendExceptionsToServer
 * author:  Suman
 * Created Date : 15-12-2015
 * Description : This class is to send exceptions to server, to send an email on exception
 * Modified Date : 15-04-2016
 * Reason: Checking net status before sending mail to server
 *************************************************************/
public class SendExceptionsToServer implements DeviceConstants,FolderAndURLConstants {
    private Context context;
    public SendExceptionsToServer() {

    }
    public SendExceptionsToServer(Context context) {
        this.context = context;
    }
    private static final String LOGTAG = "SendExceptionsToServer";
    public String deviceId = "";
    public JSONObject sendMail(final String subject,final String message,final String notification_type) {
        Logger.logDebug(LOGTAG,"sendMail" , "subject: " + subject);
        Logger.logDebug(LOGTAG, "sendMail", "message: " + message);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CODE, CODE_EXCEPTION);
            jsonObject.put(MESSAGE, message);
            jsonObject.put(DEVICE_ID,deviceId);
          //  String dateTime = new Utils().getPresentDateTime();
//            //store debug log in text file ,create file name with date and time
//            new Utils().generateNoteOnSD(FOLDER_PATH_LICENSE, dateTime, message);
//            //TODO, have to change this, replace Exception string with 202 code
//            int responseCode = 202;
//            if (notification_type.equalsIgnoreCase("Exception")) {
//                responseCode = 204;
//            }
            //jsonObject.put(NOTIFICATION_TYPE, responseCode); not required
            boolean netStatus = PingIP.isConnectingToInternet(context);
            if (netStatus) {
                AsyncTaskToSendExceptionEmail asyncTaskToSendExceptionEmail = new AsyncTaskToSendExceptionEmail(context);
                asyncTaskToSendExceptionEmail.execute(jsonObject);
            }
        } catch (Exception e) {
            Utils.convertExceptionToString(e);
//            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer();
//            sendExceptionsToServer.sendMail(SendExceptionsToServer.this.getClass().getName(), Utils.convertExceptionToString(e));
        }
        return jsonObject;
    }

    public String getDeviceId(final String device) {
        if (!device.equalsIgnoreCase("")) {
             deviceId = device;
        }
        return deviceId;
    }
}
