package com.ideabytes.qezytv.genericplayer.alertdialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ideabytes.qezytv.genericplayer.R;

import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

/**
 * Created by suman on 19/12/15.
 */
public class AlertDialogWifi extends Dialog {
    private static final String LOGTAG = "AlertDialogWifi";
    private Activity activity;
    public AlertDialogWifi(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public void showDialog() {
        try {
            final Dialog dialog = new Dialog(activity, R.style.PauseDialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //this is not to close alert dialog on out side touch
            dialog.setCanceledOnTouchOutside(false);
            //setting view to alert dialog
            dialog.setContentView(R.layout.custom_dialog_wifi);
            //settings params to dialog
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            TextView tvTitle = (TextView) dialog
                    .findViewById(R.id.Alert_Dialog_Title);//text view to display alert dialog title
            tvTitle.setText("Alert Message");
            final EditText etPwd = (EditText) dialog
                    .findViewById(R.id.Alert_Dialog_Wifi_Pwd);//text view to display alert dialog message

            Button inunClose = (Button) dialog
                    .findViewById(R.id.Alert_Dialog_Btn_Ok);//button to dismiss

            //back button click listener of dialog to close dialog
            inunClose.setText("Connect");
            inunClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    String password = etPwd.getText().toString().trim();
                    connectToWifi(password);
                }
            });
            dialog.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    // TODO Auto-generated method stub
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        //disable back press
                    }
                    return true;
                }
            });
            dialog.show();
        } catch (Exception e) {
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(activity);
            sendExceptionsToServer.sendMail(activity.getClass().getName(), Utils.convertExceptionToString(e),"Exception");
        }
    }

    private void connectToWifi(final String pwd) {
        WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        // setup a wifi configuration
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\""+getSSID()+"\"";
        Log.d(LOGTAG,"connectToWifi ssid  "+ getSSID());
        Log.d(LOGTAG,"connectToWifi pwd  "+ pwd);
        wc.preSharedKey = pwd;
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        // connect to and enable the connection
        int netId = wifiManager.addNetwork(wc);
        wifiManager.enableNetwork(netId, true);
        boolean changeHappen = wifiManager.saveConfiguration();

        Log.d(LOGTAG, "*** changeHappen "+changeHappen);
        if(netId != -1 && changeHappen){
            Log.d(LOGTAG, "### Change happen");
        }else{
            Log.d(LOGTAG, "*** Change NOT happen");
        }
        //wifiManager.setWifiEnabled(true);
    }

    private String getSSID() {
        WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.d(LOGTAG,"wifiInfo "+ wifiInfo.toString());
        Log.d(LOGTAG,"SSID "+wifiInfo.getSSID());
        return wifiInfo.getSSID();
    }
}
