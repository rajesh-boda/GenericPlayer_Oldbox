package com.ideabytes.qezytv.genericplayer.autoupdate;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.ideabytes.qezytv.genericplayer.VideoActivity;
import com.ideabytes.qezytv.genericplayer.asynctasks.AsyncTaskToRebootConfirm;
import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.constants.FolderAndURLConstants;
import com.ideabytes.qezytv.genericplayer.database.GetChannelInfo;
import com.ideabytes.qezytv.genericplayer.ethernet.Ethernet;
import com.ideabytes.qezytv.genericplayer.services.WatchDogToUpdateStatus;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : AsyncTaskToDownloadApk
 * author:  Suman
 * Created Date : 08-12-2015
 * Description : Async Task to download updated apk
 * Modified Date : 15-12-2015
 * Reason: Exception mail added
 *************************************************************/
public class AsyncTaskToDownloadApk extends AsyncTask<JSONObject,Void,String> implements FolderAndURLConstants,DeviceConstants {
    private static final String LOGTAG = "AsyncTaskToDownloadApk";
    private Context context;
    private ProgressDialog progressDialog = null;//to display loading status
    public AsyncTaskToDownloadApk(Context context) {
        this.context = context;
    }
    @Override
    protected String doInBackground(JSONObject... params) {
        String response = null;
        try {
          //  Log.v(LOGTAG, "apk url path" + SERVER_URL_TO_GET_UPDATED_APK);
            JSONObject inputData = new Utils(context).getInputToService();
           // Log.v(LOGTAG, "input to apk download service " +inputData);
            //posting data to service
            Log.v(LOGTAG," inputData "+params[0]);
            //posting data to service
            String data = URLEncoder.encode(DEVICE_ID, "UTF-8") + "="
                    + URLEncoder.encode(params[0].optString(DEVICE_ID).toString(), "UTF-8");
            data += "&" + URLEncoder.encode(ACCESS_TOKEN, "UTF-8") + "="
                    + URLEncoder.encode(params[0].optString(ACCESS_TOKEN).toString(), "UTF-8");

            Log.v(LOGTAG," sending data "+data);

              //  URL url = new URL(SERVER_URL_TO_GET_UPDATED_APK);
            URL url = new URL("http://qezymedia.com/tvstreaminganalytics/apk/GenericPlayer.apk");

                Log.v(LOGTAG,"url  "+SERVER_URL_TO_GET_UPDATED_APK);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(
                        urlConnection.getOutputStream());
                wr.write(data);
                wr.flush();
                //urlConnection.setConnectTimeout(3 * 1000);
                urlConnection.connect();
               String filename = APK_FILE_NAME;
            //String filename = "genericplayer.apk";
                //String dirPath = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "GenericPlayer/Apk";
               // Log.v(LOGTAG, "dirPath:" + FOLDER_PATH_UPDATED_APK);
                new Utils().createGenericFolderIfRequired(FOLDER_PATH_UPDATED_APK);
               // Log.v(LOGTAG,"apk filename:" + filename);
                File file = new File(FOLDER_PATH_UPDATED_APK,filename);
                //delete older file if any before downloading
                //if nothing there nothing will be deleted
                if (file.exists()) {
                    file.delete();
                    } else {
                        file.createNewFile();
                    }
                FileOutputStream fileOutput = new FileOutputStream(file);
               InputStream inputStream = urlConnection.getInputStream();
//            InputStream inputStream = new BufferedInputStream(url.openStream(),
//                    8192);

            int totalSize = urlConnection.getContentLength();
                double apkSize = totalSize;
                Log.v(LOGTAG," apk file totalSize "+apkSize);
                double downloadedSize = 0.0;
                byte[] buffer = new byte[1024];
                int bufferLength = 0;

                while ((bufferLength = inputStream.read(buffer)) != -1 )
                {
                    fileOutput.write(buffer, 0, bufferLength);
                    // Log.v(LOGTAG, "end time " + new Utils().getPresentDateTime());
                    downloadedSize += bufferLength;
                }
                Log.i(LOGTAG, " apk file downloadedSize: " + downloadedSize / (1024 * 1024) + " MB apk file totalSize: " + apkSize + " MB");
                    fileOutput.flush();
                    fileOutput.close();
               // Log.v(LOGTAG," downloading apk service call response "+ response);
        } catch (ConnectTimeoutException cte) {
             Log.v(LOGTAG, "Connection timed out");
            return null;
        }catch (Exception e) {
            //sending exception to email
            Log.v(LOGTAG, "exception" + e.toString());
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(context);
            sendExceptionsToServer.sendMail(AsyncTaskToDownloadApk.this.getClass().getName(), Utils.convertExceptionToString(e),"Exception");
            return null;
        }
        return response;
    }
    protected void onPostExecute(String result) {
        // execution of result of Long time consuming operation
        super.onPostExecute(result);
        Log.v(LOGTAG, "onPostExecute");
//        if (progressDialog != null)
//            progressDialog.cancel();
        //apk downloaded, so send confirmation to server
        AsyncTaskToPostApkUpgrade asyncTaskToPostApkUpgrade = new AsyncTaskToPostApkUpgrade(context);
        asyncTaskToPostApkUpgrade.execute();
       // installApplication(FOLDER_PATH_UPDATED_APK);

        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.ib.viplov.gp_installer");
        if (launchIntent != null) {
            launchIntent.putExtra("message", "install");

            context.startActivity(launchIntent);//null pointer check in case package name was not found
            //VideoActivity.getActivity().finish();
            //context.stopService(new Intent(context.getApplicationContext(), WatchDogToUpdateStatus.class));


        }

        String nameOfProcess = "com.ideabytes.qezytv.genericplayer";
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> listOfProcesses = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : listOfProcesses)
        {
            if (process.processName.contains(nameOfProcess))
            {
                Log.e("Proccess" , process.processName + " : " + process.pid);
                android.os.Process.killProcess(process.pid);
                android.os.Process.sendSignal(process.pid, android.os.Process.SIGNAL_KILL);
                manager.killBackgroundProcesses(process.processName);
                break;
            }
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //show progress dialog on connecting to server
//        progressDialog = ProgressDialog.show(context, "Downloading Apk ","\nPlease wait...");
       // Log.v(LOGTAG, "onPreExecute");
    }
    /**
     * This method is to install apk file which is downloaded to sd card
     * on clicking "Update" button from alert dialog
     * @param dirPath
     */
    public void installApplication(final String dirPath) {
        try {
            Log.v(LOGTAG, "installApplication " + dirPath);
//            String appVersion = new Utils(getActivity().getApplicationContext()).getApkVersion();
//            GetChannelInfo getChannelInfo = new GetChannelInfo(getActivity().getApplicationContext());
//            String versionFromDb = getChannelInfo.getChannelInfo().getString(DeviceConstants.APK_VERSION);
            File apkFile = new File(dirPath, APK_FILE_NAME);
            Log.v(LOGTAG, "installApplication file path " + apkFile.getAbsolutePath());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(
                    Uri.fromFile(apkFile),
                    "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            String command;
            command = "pm install -r " + apkFile;
            Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
            proc.waitFor();


            //after installation delete file
        File file = new File(dirPath);
        file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
