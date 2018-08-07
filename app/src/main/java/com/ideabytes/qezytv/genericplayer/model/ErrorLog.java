package com.ideabytes.qezytv.genericplayer.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by suman on 11/4/16.
 */
public class ErrorLog {
    private Context context;
    public ErrorLog(Context context) {
        this.context = context;
    }
    public void saveErrorLog(final String reason) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("error",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("code",reason);
        editor.commit();
    }
    public String getErrorLogs() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("error",Context.MODE_PRIVATE);
        String errorLog = sharedPreferences.getString("code","No Error Saved Yet");
        return errorLog;
    }
}
