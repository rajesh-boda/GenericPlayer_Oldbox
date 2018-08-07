package com.ideabytes.qezytv.genericplayer.wifi;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Toast;

import com.ideabytes.qezytv.genericplayer.R;


/**
 * Created by suman on 4/1/16.
 */
public class WifiConnector extends Floating {

    public static final String EXTRA_HOTSPOT = "com.ideabytes.genericplayer.connecter.wifi.HOTSPOT";

    private ScanResult mScanResult;
    private Floating.Content mContent;
    private WifiManager mWifiManager;

    @Override
    protected void onNewIntent (Intent intent) {
        setIntent(intent);
        // This activity has "singleInstance" launch mode.
        // Update content to reflect the newest intent.
        doNewIntent(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWifiManager = (WifiManager)getSystemService(WIFI_SERVICE);

        doNewIntent(getIntent());
    }

    private boolean isAdHoc(final ScanResult scanResule) {
        return scanResule.capabilities.indexOf("IBSS") != -1;
    }

    private void doNewIntent(final Intent intent) {
        mScanResult = intent.getParcelableExtra(EXTRA_HOTSPOT);

        if(mScanResult == null) {
            Toast.makeText(this, "No data in Intent!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if(isAdHoc(mScanResult)) {
            Toast.makeText(this, R.string.adhoc_not_supported_yet, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        final String security = Wifi.ConfigSec.getScanResultSecurity(mScanResult);
        final WifiConfiguration config = Wifi.getWifiConfiguration(mWifiManager, mScanResult, security);

        if(config == null) {
            mContent = new NewNetworkContent(this, mWifiManager, mScanResult);
        } else {
            final boolean isCurrentNetwork_ConfigurationStatus = config.status == WifiConfiguration.Status.CURRENT;
            final WifiInfo info = mWifiManager.getConnectionInfo();
            final boolean isCurrentNetwork_WifiInfo = info != null
                    && android.text.TextUtils.equals(info.getSSID(), mScanResult.SSID)
                    && android.text.TextUtils.equals(info.getBSSID(), mScanResult.BSSID);
            if(isCurrentNetwork_ConfigurationStatus || isCurrentNetwork_WifiInfo) {
                mContent = new CurrentNetworkContent(this, mWifiManager, mScanResult);
            } else {
                mContent = new ConfiguredNetworkContent(this, mWifiManager, mScanResult);
            }
        }
        setContent(mContent);
    }
}
