package com.ideabytes.qezytv.genericplayer.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by suman on 26/2/16.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private final String LOGTAG = "AlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(LOGTAG, "alarm recived");
    }
}
