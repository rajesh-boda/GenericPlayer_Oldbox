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
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by sharanya on 9/8/16.
 */
public class AsyncTaskToRebootConfirm extends AsyncTask<JSONObject,Void,String> implements DeviceConstants,FolderAndURLConstants {
    private static final String LOGTAG = "AsyncTaskToRebootConfirm";
    private Context context;
    private URLConnection conn;
    public AsyncTaskToRebootConfirm(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(JSONObject... params) {
        String response ;
        BufferedReader reader = null;
        try {
            Log.v(LOGTAG," inputData "+params[0]);
            //posting data to service
            String data = URLEncoder.encode(DEVICE_ID, "UTF-8") + "="
                    + URLEncoder.encode(params[0].optString(DEVICE_ID).toString(), "UTF-8");
            data += "&" + URLEncoder.encode(ACCESS_TOKEN, "UTF-8") + "="
                    + URLEncoder.encode(params[0].optString(ACCESS_TOKEN).toString(), "UTF-8");

            // Defined URL where to send data
            URL url = new URL(SERVER_URL_TO_REBOOT_CONFIRM);
            // Send POST data request
            conn = url.openConnection();
            conn.setConnectTimeout(DeviceConstants.TIME_OUT);
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(
                    conn.getOutputStream());
            wr.write(data);
            wr.flush();
            Log.v(LOGTAG,"data is:::"+data);
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
            Log.v(LOGTAG,"confirmation call response "+response);
        } catch (Exception e) {
            response = "Exception";
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(context);
            sendExceptionsToServer.sendMail(AsyncTaskToRebootConfirm.this.getClass().getName(), Utils.convertExceptionToString(e),"Exception");
        }
        return response;
    }
}

