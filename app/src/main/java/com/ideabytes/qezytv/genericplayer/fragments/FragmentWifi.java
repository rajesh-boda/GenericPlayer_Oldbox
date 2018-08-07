package com.ideabytes.qezytv.genericplayer.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ideabytes.qezytv.genericplayer.R;

import com.ideabytes.qezytv.genericplayer.wifi.WifiScanner;

/**
 * Created by suman on 23/12/15.
 */
public class FragmentWifi extends Fragment {
    private static final String LOGTAG = "FragmentWifi";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi, container, false);
//        Intent intent=new Intent();
//        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings"));
//        // intent.setComponent(new ComponentName("android.net.ethernet", "android.net.ethernet.IEthernetManager"));
//        startActivity(intent);

        Intent intent = new Intent(getActivity(), WifiScanner.class);
        startActivity(intent);
        Log.d(LOGTAG, "FragmentWifi ");

        return view;
    }
}
