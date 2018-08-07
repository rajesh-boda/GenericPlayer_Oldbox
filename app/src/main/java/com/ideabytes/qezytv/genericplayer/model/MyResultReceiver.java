package com.ideabytes.qezytv.genericplayer.model;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.ideabytes.qezytv.genericplayer.VideoActivity;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : MyResultReceiver
 * author:  Suman
 * Created Date : 12-04-2016
 * Description : This class is to restart video player based on update from WatchDogService
 * Modified Date : 13-04-2016
 * Reason: --validating restart with result codes
 *************************************************************/
public class MyResultReceiver extends ResultReceiver {
    private String LOGTAG = "MyResultReceiver";
    private Context context;
    public MyResultReceiver(Handler handler,Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if(resultCode == 404) {
            //Log.v(LOGTAG, "resultCode "+resultData.getInt("state"));
            VideoActivity.getActivity().runOnUiThread(new UpdateUI(resultData.getInt("state")));
        } else if(resultCode == 401) {
           // Log.v(LOGTAG, "resultCode "+resultData.getInt("state"));
            VideoActivity.getActivity().runOnUiThread(new UpdateUI(resultData.getInt("state")));
        } else if(resultCode == 201) {
            //Log.v(LOGTAG, "resultCode "+resultData.getInt("state"));
            VideoActivity.getActivity().runOnUiThread(new UpdateUI(resultData.getInt("state")));
        } else {
           // Log.v(LOGTAG, "resultCode "+resultData.getInt("state"));
            VideoActivity.getActivity().runOnUiThread(new UpdateUI(resultData.getInt("state")));
        }
    }

    /**
     * This class is to update UI on the main activity based on error codes from WatchDog
     */
   private class UpdateUI implements Runnable {
        int resultCode;
        public UpdateUI(int resultCode) {
            this.resultCode = resultCode;
        }
        public void run() {
            Log.e(LOGTAG, "resultCode in update UI " + resultCode);
            if (resultCode == 404) {
                Log.e(LOGTAG, "Player Stopped, call restart");
                VideoActivity.instance.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        VideoActivity.instance.error("Player Stopped");
                    }
                });
            } else if (resultCode == 201) {
                Log.e(LOGTAG, "License Re-Activated, call restart immediately ");
                VideoActivity.instance.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        VideoActivity.instance.error("License Re-Activated, call restart immediately");
                    }
                });
            } else if (resultCode == 401) {
                Log.e(LOGTAG, "License Inactivated, stop play ");
                //if default_logo not showing, then only show default_logo
                if (!Pojo.getInstance().isLogoShowing()) {
                    VideoActivity.instance.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            VideoActivity.instance.shutdownOs();
                            VideoActivity.instance.showClientLogo(2);
                        }
                    });
                }
            }
        }
    }
}
