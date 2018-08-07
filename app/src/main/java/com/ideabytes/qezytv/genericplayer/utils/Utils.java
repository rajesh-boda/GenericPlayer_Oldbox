package com.ideabytes.qezytv.genericplayer.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;

import com.ideabytes.qezytv.genericplayer.R;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.ideabytes.qezytv.genericplayer.constants.DBConstants;
import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.constants.FolderAndURLConstants;
import com.ideabytes.qezytv.genericplayer.database.GetChannelInfo;
import com.ideabytes.qezytv.genericplayer.network.PingIP;

/*******************************************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : Utils
 * author:  Suman
 * Description : To construct Utils
 * Created Date : 07-12-2015
 * Modified Date : 16-02-2016
 * Reason: get apk file name method added
 *********************************************************************************************/
public class Utils implements DeviceConstants, FolderAndURLConstants {
    private static final String LOGTAG = "Utils";
    private Context context;

    /**
     * empty constructor
     */
    public Utils() {

    }

    /**
     * parameterised constructor
     * @param context
     */
    public Utils(Context context) {
        this.context = context;
    }

    public static void CopyStream(InputStream is, OutputStream os) {

        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception e) {
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer();
            sendExceptionsToServer.sendMail(LOGTAG, Utils.convertExceptionToString(e), "Exception");
        }
    }

    public static void sendStatus(final Context context, final int status) {
        Intent intnet = new Intent(SEND_STATUS_ACTION);
        intnet.putExtra("status",status);
        context.sendBroadcast(intnet);
    }
    /**
     * This method creates "GenericFolder" folder in sd card to store updated apk
     * downloaded from server
     *
     * author suman
     * @param folderPath
     * @return folder creation status
     */
    public  boolean createGenericFolderIfRequired(String folderPath) {
        try {
            File directory = new File(folderPath);
            // Log.v(TAG, "Folder path=>"+directory.getAbsolutePath());
            if (!directory.isDirectory()) {
                if (!directory.mkdirs()) {
                    return false;
                }
            } else {
                directory.delete();
                directory.mkdir();
            }
        } catch (Exception e) {
           e.printStackTrace();
        }// catch
        return true;
    }// createGenericFolderIfRequired()

    /**
     * This method creates "GenericFolder" folder in sd card under app package name to store db files
     *
     * author suman
     * @return folder creation status
     */
    public boolean createGenericFoldetToStoreDB() {
        try {
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File directory = context.getExternalFilesDir(extStorageDirectory);
            Log.v("Utils", "debug Folder path=>" + directory.getAbsolutePath());
            if (!directory.isDirectory()) {
                if (!directory.mkdirs()) {
                    return false;
                }
            }// try
        } catch (Exception e) {
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer();
            sendExceptionsToServer.sendMail(LOGTAG, Utils.convertExceptionToString(e), "Exception");
        }// catch
        return true;
    }// createGenericFoldetToStoreDB()

    /***
     * This method stores debug text in specified location if any exception
     * caught of File (Bug report)
     *
     * @author Suman
     * @param fileName
     * @param body
     * @since 5.2.0
     */
    public void generateNoteOnSD(final String fileName, final String body) {
        try {
            File root = new File(FOLDER_OCTO_LOGS);
            if (!root.exists()) {
                root.mkdirs();
            }
            FileOutputStream fOut = new FileOutputStream(root + "/" + fileName + ".txt");
            OutputStreamWriter myOutWriter =
                    new OutputStreamWriter(fOut);
            // System.out.println(body);
            myOutWriter.append(body);
            myOutWriter.close();
            fOut.close();
            // Toast.makeText(MainActivity.getContext(), "Bug Report generated",
            // Toast.LENGTH_SHORT).show();
            // if(new
            // ConnectionDetector(MainActivity.getContext()).isConnectingToInternet())
            // {
            // new SendBugReportAsyncTask(context).execute();
            // }
        } catch (Exception e) {
           e.printStackTrace();
        }
    }// generateDebugFile()

    /**
     * This method is to read text file content
     *
     * @param path
     * @param fileName
     * @return file content
     */
    public String readTextFile(final String path, final String fileName) {
        String aBuffer = "";
        try {
            File myFile = new File(path + "/" + fileName + ".txt");
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(
                    new InputStreamReader(fIn));
            String aDataRow = "";
            while ((aDataRow = myReader.readLine()) != null) {
                aBuffer += aDataRow + "\n";
            }
            //System.out.println("file content "+aBuffer);
            myReader.close();

        } catch (Exception e) {
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer();
            sendExceptionsToServer.sendMail(LOGTAG, Utils.convertExceptionToString(e), "Exception");
        }
        return aBuffer.trim();
    }

    /**
     * This method returns present Date Time in format (yyyy-MM-dd HH:mm:ss)
     *
     * author Suman
     * @return Date Time
     */
    @SuppressLint("SimpleDateFormat")
    public String getPresentDateTime(final Context context) {
        String presentDate = "";
       // boolean netStatus = PingIP.pingForInternet(context);
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            presentDate = sdf.format(c.getTime());
        } catch (Exception e1) {
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer();
            sendExceptionsToServer.sendMail(LOGTAG, Utils.convertExceptionToString(e1), "Exception");
        }
//        if (netStatus) {
//            try {
//                String TIME_SERVER = "time-a.nist.gov";
//                NTPUDPClient timeClient = new NTPUDPClient();
//                InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
//                TimeInfo timeInfo = timeClient.getTime(inetAddress);
//                long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
//                Date time = new Date(returnTime);
//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                presentDate = formatter.format(time);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            try {
//                Calendar c = Calendar.getInstance();
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                presentDate = sdf.format(c.getTime());
//            } catch (Exception e1) {
//                SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer();
//                sendExceptionsToServer.sendMail(LOGTAG, Utils.convertExceptionToString(e1), "Exception");
//            }
//        }
       // Log.e(LOGTAG, " dateString " + presentDate);
        return presentDate;
    }// getPresentDateTime()
    /**
     * This message is to oncvert exception to string
     * @param e
     * @return converted string
     */
    public static String convertExceptionToString(final Exception e) {
        String exceptionAsString = null;
        try {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            exceptionAsString = sw.toString();
            Log.e(LOGTAG, exceptionAsString);
        } catch (Exception e1) {
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer();
            sendExceptionsToServer.sendMail(LOGTAG, Utils.convertExceptionToString(e1), "Exception");
        }
        return  exceptionAsString;
    }

    /**
     * To delete application cache
     * @param context
     */
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        }
        else if(dir!= null && dir.isFile())
            return dir.delete();
        else {
            return false;
        }
    }

    /**
     * This method is used to return apk file name that is downloaded from service to update
     * @return apk file name with extension
     */
    public String getApkFileName() {
        String apkPath = FolderAndURLConstants.FOLDER_PATH_UPDATED_APK;
       File file = new File(apkPath) ;
        Log.v(LOGTAG,"apk file name "+file.getName());
        return file.getName();
    }

    public String getApkVersion() {
        Resources resources = context.getResources();
       // Log.v(LOGTAG,"apk version "+resources.getString(R.string.version));
        return resources.getString(R.string.version);
    }
    public String getApkVersionFromDB() {
        String version = "";
        try {
            GetChannelInfo getChannelInfo = new GetChannelInfo(context);
            version = getChannelInfo.getChannelInfo().getString(DeviceConstants.APK_VERSION);
            //Log.v(LOGTAG, "apk version " + version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    public JSONObject getInputToService() {
        JSONObject response = null;
        try {
            GetChannelInfo getChannelInfo = new GetChannelInfo(context);
            response = getChannelInfo.getChannelInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
       return response;
    }

    /**
     * To change license check status
     * @param context,setup
     */
    public void setActivation(final Context context,final int setup) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("setup", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("setup",setup);
        editor.commit();
    }
}