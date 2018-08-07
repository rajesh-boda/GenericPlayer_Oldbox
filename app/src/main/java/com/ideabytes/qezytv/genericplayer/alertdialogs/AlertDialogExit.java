package com.ideabytes.qezytv.genericplayer.alertdialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ideabytes.qezytv.genericplayer.R;

import com.ideabytes.qezytv.genericplayer.constants.DBConstants;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

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

public class AlertDialogExit extends Dialog implements DBConstants {
    private Activity activity;

    public AlertDialogExit(Activity activity) {
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
            dialog.setContentView(R.layout.custom_dialog_exit);
            //settings params to dialog
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            final EditText etExitPassword = (EditText) dialog.findViewById(R.id.Alert_Dialog_etPwd);
            Button inunClose = (Button) dialog
                    .findViewById(R.id.Alert_Dialog_Btn_Ok);//button to dismiss

            //back button click listener of dialog to close dialog
            inunClose.setText("Exit");
            inunClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userValuePwd = etExitPassword.getText().toString();
                    String adminPwd = activity.getResources().getString(R.string.exitpassword);
                    if (userValuePwd.equalsIgnoreCase(adminPwd)) {
                        dialog.dismiss();
                        activity.finish();
                    } else {
                        //make empty after clicking on Exit button
                        etExitPassword.setText("");
                        Toast.makeText(activity.getApplicationContext(),"Invalid Password",Toast.LENGTH_LONG).show();
                    }

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
}
