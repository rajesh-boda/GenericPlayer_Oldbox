package com.ideabytes.qezytv.genericplayer.fragments;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ideabytes.qezytv.genericplayer.R;

/**
 * Created by suman on 23/12/15.
 */
public class FragmentEtherNet  extends Fragment {
    private static final String LOGTAG = "FragmentEtherNet";
    private EditText IPAddress;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ethernet, container, false);

       // IPAddress = (EditText)view.findViewById(R.id.ipaddress);
       // WifiManager wm = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);
       // String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        //IPAddress.setText(ip);
        Intent intent=new Intent();
        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings"));
        // intent.setComponent(new ComponentName("android.net.ethernet", "android.net.ethernet.IEthernetManager"));
        startActivity(intent);
//        Intent intent = new Intent(getActivity(), Ethernet.class);
//        startActivity(intent);
        Log.d(LOGTAG, "FragmentEtherNet ");

        return view;
    }
}
