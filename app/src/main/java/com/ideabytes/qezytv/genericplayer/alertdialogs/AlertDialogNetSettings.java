package com.ideabytes.qezytv.genericplayer.alertdialogs;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ideabytes.qezytv.genericplayer.R;

import com.ideabytes.qezytv.genericplayer.Settings;
import com.ideabytes.qezytv.genericplayer.ValidatorActivity;
import com.ideabytes.qezytv.genericplayer.constants.DBConstants;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : AlertDialogNetSettings
 * author:  Suman
 * Created Date : 15-12-2015
 * Description : Customized dialog to show customer dialog_to_show_client_logo on any exception
 * Modified Date : 15-12-2015
 * Reason: Exception mail added
 *************************************************************/
public class AlertDialogNetSettings extends Dialog implements DBConstants {
    private Activity activity;

    public AlertDialogNetSettings(Activity activity) {
        super(activity);
        this.activity = activity;
    }
    public void showDialog(final String message) {
        try {
            final Dialog dialog = new Dialog(activity, R.style.PauseDialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //this is not to close alert dialog on out side touch
            dialog.setCanceledOnTouchOutside(false);
            //setting view to alert dialog
            dialog.setContentView(R.layout.custom_dialog_net_settings);
            //settings params to dialog
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView tvTitle = (TextView) dialog
                    .findViewById(R.id.Alert_Dialog_Title);//text view to display alert dialog title
            tvTitle.setText("Alert Message");
            TextView tvMessage = (TextView) dialog
                    .findViewById(R.id.Alert_Dialog_Message);//text view to display alert dialog message
            tvMessage.setText(message);
            Button btnApp = (Button) dialog
                    .findViewById(R.id.Alert_Dialog_Btn_App);//button to dismiss

            btnApp.setText("Player");
            //button click listener of dialog to launch application
            btnApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(activity, ValidatorActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                }
            });

            Button btnNetSettings = (Button) dialog
                    .findViewById(R.id.Alert_Dialog_Btn_Net_Settings);//button to dismiss

            btnNetSettings.setText("Net Settings");
            //button click listener of dialog to navigate to net settings page
            btnNetSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(activity, Settings.class);
                    activity.startActivity(intent);
                    activity.finish();

                }
            });
            //back button click listener of dialog
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
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(activity.getApplicationContext());
            sendExceptionsToServer.sendMail(activity.getClass().getName(), Utils.convertExceptionToString(e),"Exception");
        }
    }
}
