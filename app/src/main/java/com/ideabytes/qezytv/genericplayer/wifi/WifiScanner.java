package com.ideabytes.qezytv.genericplayer.wifi;

/**
 * Created by suman on 4/1/16.
 */
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.ideabytes.qezytv.genericplayer.R;

public class WifiScanner extends ListActivity {

    private WifiManager mWifiManager;
    private List<ScanResult> mScanResults;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mWifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //if wifi is off, enable it
        if (!mWifi.isConnected()) {
            // Do whatever
            mWifiManager.setWifiEnabled(true);
        }

        setListAdapter(mListAdapter);

        getListView().setOnItemClickListener(mItemOnClick);
    }

    @Override
    public void onResume() {
        super.onResume();
        final IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mReceiver, filter);
        mWifiManager.startScan();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                mScanResults = mWifiManager.getScanResults();
                mListAdapter.notifyDataSetChanged();

                mWifiManager.startScan();
            }

        }
    };

    private BaseAdapter mListAdapter = new BaseAdapter() {
        private LayoutInflater inflater=null;
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            inflater =(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            if(convertView == null || !(convertView instanceof TwoLineListItem)) {
//                convertView = View.inflate(getApplicationContext(),
//                        android.R.layout.simple_list_item_2, null);
//            }
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.wifi_list,null);
            }
            TextView tvSSID = (TextView) convertView.findViewById(R.id.tvSsid);
            TextView tvBSSID = (TextView) convertView.findViewById(R.id.tvBSSID);
            final ScanResult result = mScanResults.get(position);
//            ((TwoLineListItem)convertView).getText1().setText(result.SSID);
//            ((TwoLineListItem)convertView).getText2().setText(
//                    String.format("%s  %d", result.BSSID, result.level)
//            );
            tvSSID.setText(result.SSID);
            tvBSSID.setText(result.BSSID);
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return mScanResults == null ? 0 : mScanResults.size();
        }
    };

    private OnItemClickListener mItemOnClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            final ScanResult result = mScanResults.get(position);
            launchWifiConnector(WifiScanner.this, result);
        }
    };

    /**
     * Try to launch Wifi Connecter with  Prompt user to download if Wifi Connecter is not installed.
     * @param activity
     * @param hotspot
     */
    private static void launchWifiConnector(final Activity activity, final ScanResult hotspot) {
        final Intent intent = new Intent("com.ideabytes.genericplayer.connecter.action.CONNECT_OR_EDIT");
        intent.putExtra("com.ideabytes.genericplayer.connecter.wifi.HOTSPOT", hotspot);
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Wifi Connecter Library is not installed.
            Toast.makeText(activity, "Wifi Connecter is not installed.", Toast.LENGTH_LONG).show();
            // downloadWifiConnecter(activity);
        }
    }
}
