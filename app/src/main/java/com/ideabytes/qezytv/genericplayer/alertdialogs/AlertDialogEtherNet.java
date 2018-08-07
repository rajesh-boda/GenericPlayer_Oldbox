package com.ideabytes.qezytv.genericplayer.alertdialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.ideabytes.qezytv.genericplayer.R;

import com.ideabytes.qezytv.genericplayer.constants.DBConstants;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : AlertDialogExit
 * author:  Suman
 * Created Date : 19-12-2015
 * Description : Customized dialog to exit from application
 * Modified Date : 19-12-2015
 * Reason: Exception mail added
 *************************************************************/

public class AlertDialogEtherNet extends Dialog implements DBConstants {
    private Activity activity;
    private static final String LOGTAG = "AlertDialogEtherNet";
    public AlertDialogEtherNet(Activity activity) {
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
            dialog.setContentView(R.layout.custom_dialog_ethernet);
            //settings params to dialog
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            Button btnOn = (Button) dialog
                    .findViewById(R.id.Alert_Dialog_Btn_On);//button to dismiss

            //back button click listener of dialog to close dialog
            btnOn.setText("On");
            btnOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Log.e(LOGTAG,"on");
                    dialog.dismiss();
                    turnEthOnOrOff();
                }
            });
            Button btnOff = (Button) dialog
                    .findViewById(R.id.Alert_Dialog_Btn_Off);//button to dismiss
            //back button click listener of dialog to close dialog
            btnOff.setText("Off");
            btnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(LOGTAG,"off");
                    dialog.dismiss();
                    turnEthOnOrOff();

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
                    return false;
                }
            });
            dialog.show();
        } catch (Exception e) {
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(activity);
            sendExceptionsToServer.sendMail(activity.getClass().getName(), Utils.convertExceptionToString(e),"Exception");
        }
    }
    public static boolean doesEthExist() {
        List<String> list = getListOfNetworkInterfaces();

        return list.contains("eth0");
    }

    public static List<String> getListOfNetworkInterfaces() {

        List<String> list = new ArrayList<String>();

        Enumeration<NetworkInterface> nets;

        try {
            nets = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {

            e.printStackTrace();
            return null;
        }

        for (NetworkInterface netint : Collections.list(nets)) {

            list.add(netint.getName());
        }

        return list;

    }

    public static boolean isEthOn() {

        try {

            String line;
            boolean r = false;

            Process p = Runtime.getRuntime().exec("netcfg");

            BufferedReader input = new BufferedReader (new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {

                if(line.contains("eth0")){
                    if(line.contains("UP")){
                        r=true;
                    }
                    else{
                        r=false;
                    }
                }
            }
            input.close();

            Log.e(LOGTAG, "isEthOn: " + r);
            return r;

        } catch (IOException e) {
            Log.e("OLE","Runtime Error: "+e.getMessage());
            e.printStackTrace();
            return false;
        }

    }

    public static void turnEthOnOrOff() {

        try {

            if(isEthOn()){
                Runtime.getRuntime().exec("ifconfig eth0 down");
                Log.e(LOGTAG+" turnEthOnOrOff", "EthOff: " );
            }
            else{
                Runtime.getRuntime().exec("ifconfig eth0 up");
                Log.e(LOGTAG+" turnEthOnOrOff", "EthOn: " );
            }

        } catch (IOException e) {
            Log.e("OLE","Runtime Error: "+e.getMessage());
            e.printStackTrace();
        }
    }
}

