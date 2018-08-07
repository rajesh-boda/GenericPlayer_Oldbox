package com.ideabytes.qezytv.genericplayer.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.constants.FolderAndURLConstants;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by suman on 27/4/16.
 */
public class AsyncTaskToGetActiveStatus extends AsyncTask<JSONObject,Void,String> implements DeviceConstants,FolderAndURLConstants {
    private static final String LOGTAG = "AsyncTaskToGetActiveStatus";
    private Context context;
    public AsyncTaskToGetActiveStatus(Context context) {
        this.context = context;
    }
    @Override
    protected String doInBackground(JSONObject... params) {
        String response = "202";
        BufferedReader reader = null;
        try {
            //Log.v(LOGTAG, " inputData " + params[0]);
            //posting data to service
            String data = URLEncoder.encode(CHANNEL_ID, "UTF-8") + "="
                    + URLEncoder.encode(params[0].optString(CHANNEL_ID).toString(), "UTF-8");
            data += "&" + URLEncoder.encode(DEVICE_ID, "UTF-8") + "="
                    + URLEncoder.encode(params[0].optString(DEVICE_ID).toString(), "UTF-8");
            data += "&" + URLEncoder.encode(ACCESS_TOKEN, "UTF-8") + "="
                    + URLEncoder.encode(params[0].optString(ACCESS_TOKEN).toString(), "UTF-8");

            // Defined URL where to send data
            // URL url = new URL(Utils.urlPath + "updateBandwidthAndStatus");
            URL url = new URL(SERVER_URL_TO_CONFIRM);
            // Send POST data request
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(DeviceConstants.TIME_OUT);
            conn.setDoOutput(true);
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

                response = sb.toString();//reponse from service
                Log.v(LOGTAG, "get active status call response " + response);
            }  else {
                Log.e(LOGTAG, "Problem in connection " + conn.getResponseCode());
            }
        } catch (Exception e) {
            response = "Exception";
            //sending exception to email
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(context);
            sendExceptionsToServer.sendMail(AsyncTaskToGetActiveStatus.this.getClass().getName(), Utils.convertExceptionToString(e),"Exception");
        }
        return response;
    }
}
