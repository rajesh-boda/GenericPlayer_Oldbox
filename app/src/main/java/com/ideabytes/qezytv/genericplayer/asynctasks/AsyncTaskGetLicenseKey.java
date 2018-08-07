package com.ideabytes.qezytv.genericplayer.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.constants.FolderAndURLConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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
public class AsyncTaskGetLicenseKey extends AsyncTask<String,Void,String> implements DeviceConstants,FolderAndURLConstants {
    private static final String LOGTAG = "AsyncTaskGetLicenseKey";
    private Context context;
    public AsyncTaskGetLicenseKey(Context context) {
         this.context = context;
    }
    @Override
    protected String doInBackground(String... params) {
        String response = DeviceConstants.VALUE_202;

        BufferedReader reader = null;
        Log.v(LOGTAG,"Get License Begenning ");
        try {
           //  Log.v(LOGTAG, "inputData to post device status " + params[0]);
            //posting data to service
            //required input to post device status to dashboard
            String data = "&" + URLEncoder.encode(DEVICE_ID, "UTF-8") + "="
                    + URLEncoder.encode(params[0], "UTF-8");


            // Defined URL where to send data
           // URL url = new URL(Utils.urlPath + "updateBandwidthAndStatus");
            URL url = new URL(SERVER_URL_TO_GET_LICENSE_KEY);
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
                Log.v(LOGTAG, " Get License response: " + response);
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray(DeviceConstants.DATA);
                String jString = jsonArray.getString(0);
                JSONObject jObj = new JSONObject(jString);
                response = jObj.getString(DeviceConstants.LICENSE);
                Log.v(LOGTAG, " Get License key: " + response);
                Log.v(LOGTAG,"Get License End");
            } else {

            }
        } catch (Exception e) {
            response = DeviceConstants.EXCEPTION;
            e.printStackTrace();
        }
        return response;
    }

}
