package com.ideabytes.qezytv.genericplayer.fragments;

/**
 * Created by suman on 17/12/15.
 */
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ideabytes.qezytv.genericplayer.R;

import com.ideabytes.qezytv.genericplayer.VideoActivity;
import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;

public class FragError  extends Fragment {
    private static final String LOGTAG = "FragError";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_fragment, container, false);

        Log.d(LOGTAG, "FragError :");
        method();
        return view;
    }
    private void method() {
        Thread background = new Thread() {
            public void run() {

                try {
                    Log.i(LOGTAG, "background thread block");
                    // Thread will sleep for 5 seconds
                    sleep(DeviceConstants.SLEEP_TIME);
                    // After 5 seconds redirect to another intent
                    Intent i=new Intent(getActivity(),VideoActivity.class);
                    startActivity(i);
                   } catch (Exception e) {

                }
            }
        };
        background.start();
    }
}//class
