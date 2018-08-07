package com.ideabytes.qezytv.genericplayer.logs;

import android.util.Log;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : MainActivity
 * author:  Suman
 * Created Date : 16-12-2015
 * Description : This class is to show log messages (if we dont want logs just come here and comment)
 * Modified Date : 16-12-2015
 * Reason: Exception mail added
 *************************************************************/
public class Logger  {
   public static void logVerbose(final String className,final String method,final String message) {
      Log.v(className,method+"()::"+message);
   }
    public static void logError(final String className,final String method,final String message) {
        Log.e(className, method + "()::" + message);
    }
    public static void logInfo(final String className,final String method,final String message) {
        Log.i(className, method + "()::" + message);
    }
    public static void logWarning(final String className,final String method,final String message) {
        Log.w(className, method + "()::" + message);
    }
    public static void logDebug(final String className,final String method,final String message) {
        Log.d(className, method + "()::" + message);
    }
}
