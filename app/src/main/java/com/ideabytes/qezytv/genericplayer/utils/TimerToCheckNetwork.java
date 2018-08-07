package com.ideabytes.qezytv.genericplayer.utils;

import android.content.Context;
import android.content.Intent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.network.PingIP;

/**
 * Created by suman on 18/2/16.
 */
public class TimerToCheckNetwork {
    private final String LOGTAG  = "TimerToCheckNetwork";
    private Context context;
    public TimerToCheckNetwork() {

    }
    public TimerToCheckNetwork(Context context,int startTime,int repeatInterval) {
        this.context = context;
        checkApkUpdate(startTime,repeatInterval);
    }
    public boolean checkApkUpdate(final int startTime,final int repeatInterval) {
        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(startTime);
        //Schedule a task to run every 5 seconds (or however long you want)
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!PingIP.isConnectingToInternet(context)) {
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    intent.setAction(DeviceConstants.INTERNET_ACTION);
                    context.sendBroadcast(intent);
                }
            }
        }, startTime, repeatInterval, TimeUnit.SECONDS); // or .MINUTES, .HOURS etc.
        return PingIP.isConnectingToInternet(context);
    }
}
