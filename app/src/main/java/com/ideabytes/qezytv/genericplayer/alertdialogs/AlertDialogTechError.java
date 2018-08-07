package com.ideabytes.qezytv.genericplayer.alertdialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.ideabytes.qezytv.genericplayer.R;

import com.ideabytes.qezytv.genericplayer.constants.DBConstants;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : AlertDialogTechError
 * author:  Suman
 * Created Date : 09-12-2015
 * Description : Customized dialog to show customer dialog_to_show_client_logo on any exception
 * Modified Date : 15-12-2015
 * Reason: Exception mail added
 *************************************************************/
public class AlertDialogTechError extends Dialog implements DBConstants {
    private Context context;
    public static Dialog dialogTexhError;
    public AlertDialogTechError(Context context) {
        super(context);
        this.context = context;
    }
    public void showDialog() {
        try {
            dialogTexhError = new Dialog(context, R.style.PauseDialog);
            dialogTexhError.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogTexhError.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            //this is not to close alert dialog on out side touch
            dialogTexhError.setCanceledOnTouchOutside(false);
            //setting view to alert dialog
            dialogTexhError.setContentView(R.layout.alert_dialog_tech_error);
            //settings params to dialog
            dialogTexhError.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            //back button click listener of dialog closed, not to respond on back button click
            dialogTexhError.setOnKeyListener(new Dialog.OnKeyListener() {

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
            //show dialog
            dialogTexhError.show();
        } catch (Exception e) {
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(context);
            sendExceptionsToServer.sendMail(AlertDialogTechError.this.getClass().getName(), Utils.convertExceptionToString(e),"Exception");
        }
    }

    public void dismissDialog() {
        dialogTexhError.cancel();
    }
}
