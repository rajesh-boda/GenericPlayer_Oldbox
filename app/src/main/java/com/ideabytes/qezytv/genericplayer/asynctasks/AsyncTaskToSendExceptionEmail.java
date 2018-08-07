package com.ideabytes.qezytv.genericplayer.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.constants.FolderAndURLConstants;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : AsyncTaskToSendExceptionEmail
 * author:  Suman
 * Created Date : 15-12-2015
 * Description : Async Task to send exceptions to service which will send an email to registered developer
 * Modified Date : 27-04-2016
 * Reason: Checking connection succces with code 200
 *************************************************************/
public class AsyncTaskToSendExceptionEmail extends AsyncTask<JSONObject,Void,String> implements DeviceConstants,FolderAndURLConstants {
    private static final String LOGTAG = "AsyncTaskToSendExceptionEmail";
    private Context context;
    public AsyncTaskToSendExceptionEmail(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(JSONObject... params) {
        String response = "202";
        BufferedReader reader = null;
        try {
            //JSONObject inputData = new Utils(context).getInputToService();
           // Log.v(LOGTAG, "inputData " + params[0]);
            //posting data to service
            String data = URLEncoder.encode(SUBJECT, "UTF-8") + "="
                    + URLEncoder.encode(params[0].optString(SUBJECT).toString(), "UTF-8");
            data += "&" + URLEncoder.encode(MESSAGE, "UTF-8") + "="
                    + URLEncoder.encode(params[0].optString(MESSAGE).toString(), "UTF-8");
            data += "&" + URLEncoder.encode(DEVICE_ID, "UTF-8") + "="
                    + URLEncoder.encode(params[0].optString(DEVICE_ID), "UTF-8");
            data += "&" + URLEncoder.encode(NOTIFICATION_TYPE, "UTF-8") + "="
                    + URLEncoder.encode(params[0].optString(NOTIFICATION_TYPE), "UTF-8");

            // Defined URL where to send data
            // URL url = new URL(Utils.urlPath + "updateBandwidthAndStatus");
            URL url = new URL(SERVER_URL_TO_SEND_MAIL);
            // Send POST data request
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setConnectTimeout(DeviceConstants.TIME_OUT);
            OutputStreamWriter wr = new OutputStreamWriter(
                    conn.getOutputStream());
            wr.write(data);
            wr.flush();
            if (conn.getResponseCode() == 200) {
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

                response = sb.toString();//response from service
                //  Log.v(LOGTAG, "send email response " + response);
            }  else {
                Log.e(LOGTAG, "Problem in connection " + conn.getResponseCode());
            }
        } catch (Exception e) {
            response = "Exception";
            //sending exception to email
        }
        return response;
    }
}