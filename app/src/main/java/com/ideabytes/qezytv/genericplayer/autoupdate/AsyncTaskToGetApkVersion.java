package com.ideabytes.qezytv.genericplayer.autoupdate;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
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

/**
 * Created by suman on 8/1/16.
 */
public class AsyncTaskToGetApkVersion extends AsyncTask<Void,Void,String> implements DeviceConstants,FolderAndURLConstants {
    private static final String LOGTAG = "AsyncTaskToGetApkVersion";
    private Context context;
    private URLConnection conn;
    public AsyncTaskToGetApkVersion(Context context) {
        this.context = context;
    }
    @Override
    protected String doInBackground(Void... params) {
        String response = "202";
        BufferedReader reader = null;
        try {
            JSONObject inputData = new Utils(context).getInputToService();
           // Log.v(LOGTAG, "input to service " +inputData);
            //posting data to service
            String data = URLEncoder.encode(DEVICE_ID, "UTF-8") + "="
                    + URLEncoder.encode(inputData.optString(DEVICE_ID).toString(), "UTF-8");
            data += "&" + URLEncoder.encode(ACCESS_TOKEN, "UTF-8") + "="
                    + URLEncoder.encode(inputData.optString(ACCESS_TOKEN).toString(), "UTF-8");

            // Defined URL where to send data
            // URL url = new URL(Utils.urlPath + "updateBandwidthAndStatus");
            URL url = new URL(SERVER_URL_TO_GET_APK_STATUS);
            // Send POST data request
            conn = url.openConnection();
            conn.setConnectTimeout(2*1000);
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
            //make josn reponse as required string
            JSONObject jsonObject = new JSONObject(response);
            Log.v(LOGTAG,"apk version call resp 1 "+response);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            response = jsonArray.getJSONObject(0).getString("status");
            String version = jsonArray.getJSONObject(0).getString("version");
            response = response+":"+version;
           // Log.v(LOGTAG,"apk version call response "+response);
        } catch (Exception e) {
            response = "Exception";
            //sending exception to email
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(context);
            sendExceptionsToServer.sendMail(AsyncTaskToGetApkVersion.this.getClass().getName(), Utils.convertExceptionToString(e),"Exception");
        }
        return response;
    }
}
