package com.ideabytes.qezytv.genericplayer.autoupdate;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.constants.FolderAndURLConstants;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : AsyncTaskToPostApkUpgrade
 * author:  Suman
 * Created Date : 16-02-2016
 * Description : Async Task to post confirmation to server on apk upgrade
 * Modified Date : 16-02-2016
 * Reason: Exception mail added
 *************************************************************/
public class AsyncTaskToPostApkUpgrade  extends AsyncTask<Void,Void,String> implements DeviceConstants,FolderAndURLConstants {
    private static final String LOGTAG = "AsyncTaskToPostApkUpgrade";
    private Context context;
    private URLConnection conn;
    public AsyncTaskToPostApkUpgrade(Context context) {
        this.context = context;
    }
    @Override
    protected String doInBackground(Void... params) {
        String response = "202";
        BufferedReader reader = null;
        try {
            JSONObject inputData = new Utils(context).getInputToService();
            //Log.v(LOGTAG, "input " + inputData);
            //posting data to service
            String version = new Utils(context).getApkVersion();
           // Log.v(LOGTAG,"version "+version);
            String data = URLEncoder.encode(DEVICE_ID, "UTF-8") + "="
                    + URLEncoder.encode(inputData.optString(DEVICE_ID).toString(), "UTF-8");
            data += "&" + URLEncoder.encode(APK_VERSION, "UTF-8") + "="
                    + URLEncoder.encode(version, "UTF-8");
            data += "&" + URLEncoder.encode(ACCESS_TOKEN, "UTF-8") + "="
                    + URLEncoder.encode(inputData.optString(ACCESS_TOKEN).toString(), "UTF-8");

            // Defined URL where to send data
            // URL url = new URL(Utils.urlPath + "updateBandwidthAndStatus");
            URL url = new URL(SERVER_URL_TO_POST_APK_UPDATE_STATUS);
            // Send POST data request
            conn = url.openConnection();
            conn.setConnectTimeout(DeviceConstants.TIME_OUT);
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(
                    conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the server response
            reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line + "\n");
            }

            response = sb.toString();//reponse from service
            Log.v(LOGTAG,"apk upgrade confirmation call response "+response);
        } catch (Exception e) {
            response = "Exception";
            //sending exception to email
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(context);
            sendExceptionsToServer.sendMail(AsyncTaskToPostApkUpgrade.this.getClass().getName(), Utils.convertExceptionToString(e),"Exception");
        }
        return response;
    }
}
