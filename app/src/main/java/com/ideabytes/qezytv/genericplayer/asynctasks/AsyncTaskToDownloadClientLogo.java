package com.ideabytes.qezytv.genericplayer.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.constants.FolderAndURLConstants;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : AsyncTaskToDownloadClientLogo
 * author:  Suman
 * Created Date : 08-12-2015
 * Description : Async Task to download customer dialog_to_show_client_logo from server
 * Modified Date : 27-04-2016
 * Reason: Checking connection succces with code 200
 *************************************************************/
public class AsyncTaskToDownloadClientLogo extends AsyncTask<String,Void,Bitmap> implements FolderAndURLConstants {
    private static final String LOGTAG = "AsyncTaskToDownloadClientLogo";
    private Context context;
    private ProgressDialog progressDialog = null;//to display loading status
    public AsyncTaskToDownloadClientLogo(Context context) {
        this.context = context;
    }
    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap myBitmap = null;
        try {
            Log.v(LOGTAG, "" + params[0]);
            URL url = new URL(params[0]);
            String response = null;
            try
            {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.setConnectTimeout(DeviceConstants.TIME_OUT);
                urlConnection.connect();
                if (urlConnection.getResponseCode() == 200) {
                    String filename = "logo.jpg";
                    String dirPath = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "GenericPlayer/Logo";
                    Log.v(LOGTAG, "dirPath:" + dirPath);
                    new Utils().createGenericFolderIfRequired(dirPath);
                    Log.v(LOGTAG, "Local filename:" + filename);
                    File file = new File(dirPath, filename);
                    //when there is no image file then only download new image file
                    FileOutputStream fileOutput = new FileOutputStream(file);
                    InputStream inputStream = urlConnection.getInputStream();
                    int totalSize = urlConnection.getContentLength();
                    Log.i(LOGTAG, " logo totalSize: " + totalSize);
                    int downloadedSize = 0;
                    byte[] buffer = new byte[1024];
                    int bufferLength = 0;
                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fileOutput.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;
                    }
                    Log.i(LOGTAG, " logo downloadedSize: " + downloadedSize + "  logo totalSize: " + totalSize);
                    fileOutput.close();
                } else {
                    Log.e(LOGTAG, "Problem in connection " + urlConnection.getResponseCode());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            StringBuilder sb = new StringBuilder();
            response = sb.toString();//response from service
           // Log.v(LOGTAG," downloading default_logo service call response "+response);
        } catch (IOException e) {
            //sending exception to email
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(context);
            sendExceptionsToServer.sendMail(AsyncTaskToDownloadClientLogo.this.getClass().getName(), Utils.convertExceptionToString(e),"Exception");
            return null;
        }
        return myBitmap;
    }
//    protected void onPostExecute(Bitmap result) {
//        // execution of result of Long time consuming operation
//        super.onPostExecute(result);
//        if (progressDialog != null)
//            progressDialog.cancel();
//    }
//
//    @Override
//    protected void onPreExecute() {
//        progressDialog = new  ProgressDialog(context);
//        progressDialog.setTitle("Downloading Logo");
//        progressDialog.setMessage("Please wait...");
//        progressDialog.show();
//    }
}
