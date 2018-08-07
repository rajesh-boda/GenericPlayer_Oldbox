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
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : AsyncTaskPostDeviceStatus
 * author:  Suman
 * Created Date : 08-12-2015
 * Description : Async Task to post device status to dashboard
 * Modified Date : 27-04-2016
 * Reason: Checking connection succces with code 200
 *************************************************************/
public class AsyncTaskPostDeviceStatus extends AsyncTask<JSONObject,Void,String> implements DeviceConstants,FolderAndURLConstants {
    private static final String LOGTAG = "AsyncTaskPostDeviceStatus";
    private Context context;
    public AsyncTaskPostDeviceStatus(Context context) {
         this.context = context;
    }
    @Override
    protected String doInBackground(JSONObject... params) {
        String response = "202";
        BufferedReader reader = null;
        try {
           //  Log.v(LOGTAG, "inputData to post device status " + params[0]);
            //posting data to service
            //required input to post device status to dashboard
            String data = URLEncoder.encode(CHANNEL_ID, "UTF-8") + "="
                    + URLEncoder.encode(params[0].optString(CHANNEL_ID).toString(), "UTF-8");
            data += "&" + URLEncoder.encode(DATE_TIME, "UTF-8") + "="
                    + URLEncoder.encode(params[0].optString(DATE_TIME).toString(), "UTF-8");
            data += "&" + URLEncoder.encode(DEVICE_ID, "UTF-8") + "="
                    + URLEncoder.encode(params[0].optString(DEVICE_ID).toString(), "UTF-8");
            data += "&" + URLEncoder.encode(ACTIVITY_STATUS, "UTF-8") + "="
                    + URLEncoder.encode(params[0].optString(ACTIVITY_STATUS).toString(), "UTF-8");
            data += "&" + URLEncoder.encode(INTERNET_SPEED, "UTF-8") + "="
                    + URLEncoder.encode(params[0].optString(INTERNET_SPEED).toString(), "UTF-8");
            data += "&" + URLEncoder.encode(STREAM_STATUS, "UTF-8") + "="
                    + URLEncoder.encode(params[0].optString(STREAM_STATUS).toString(), "UTF-8");
            data += "&" + URLEncoder.encode(VIDEO_TYPE, "UTF-8") + "="
                    + URLEncoder.encode(params[0].optString(VIDEO_TYPE), "UTF-8");
            data += "&" + URLEncoder.encode(IP_ADDRESS, "UTF-8") + "="
                    + URLEncoder.encode(params[0].optString(IP_ADDRESS), "UTF-8");
            data += "&" + URLEncoder.encode(ACCESS_TOKEN, "UTF-8") + "="
                    + URLEncoder.encode(params[0].optString(ACCESS_TOKEN), "UTF-8");
            data += "&" + URLEncoder.encode(LOCATION, "UTF-8") + "="
                    + URLEncoder.encode(params[0].getString(LOCATION), "UTF-8");
            data += "&" + URLEncoder.encode(GEO_LANGITUDE, "UTF-8") + "="
                    + URLEncoder.encode(params[0].getString(GEO_LANGITUDE), "UTF-8");
            data += "&" + URLEncoder.encode(GEO_LATITUDE, "UTF-8") + "="
                    + URLEncoder.encode(params[0].getString(GEO_LATITUDE), "UTF-8");
            data += "&" + URLEncoder.encode(CONSUMED_BIT_RATE, "UTF-8") + "="
                    + URLEncoder.encode(params[0].getString(CONSUMED_BIT_RATE), "UTF-8");

            // Defined URL where to send data
           // URL url = new URL(Utils.urlPath + "updateBandwidthAndStatus");
            URL url = new URL(SERVER_URL_TO_POST_STATUS);
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
               // Log.v(LOGTAG, " update status call response " + response);
            } else {
                Log.e(LOGTAG, "Problem in connection " + conn.getResponseCode());
            }
        } catch (Exception e) {
            response = "Exception";
            e.printStackTrace();
        }
        return response;
    }
}
