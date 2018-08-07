package com.ideabytes.qezytv.genericplayer.alertdialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.ideabytes.qezytv.genericplayer.R;

import java.io.File;
import com.ideabytes.qezytv.genericplayer.constants.DBConstants;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : AlertDialogInstallApk
 * author:  Suman
 * Created Date : 08-01-2016
 * Description : Customized dialog to install updated apk
 * Modified Date : 08-01-2016
 * Reason: Exception mail added
 *************************************************************/
public class AlertDialogInstallApk extends Dialog implements DBConstants {
    private String LOGTAG = "AlertDialogInstallApk";
    private Activity activity;

    public AlertDialogInstallApk(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public void showDialog(final String message,final String dirPath) {
        try {
            final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //this is not to close alert dialog on out side touch
            dialog.setCanceledOnTouchOutside(false);
            //setting view to alert dialog
            dialog.setContentView(R.layout.alert_dialog_install_apk);
            //settings params to dialog
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            TextView tvTitle = (TextView) dialog
                    .findViewById(R.id.Alert_Dialog_Title);//text view to display alert dialog title
            tvTitle.setText("Alert Message");
            TextView tvMessage = (TextView) dialog
                    .findViewById(R.id.Alert_Dialog_Message);//text view to display alert dialog message
            tvMessage.setText(message);
            Button btnInstall = (Button) dialog
                    .findViewById(R.id.Alert_Dialog_Btn_Install);//button to dismiss

            //back button click listener of dialog to close dialog
            btnInstall.setText("Update");
            btnInstall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    installApplication(dirPath);
                    dialog.dismiss();
                }
            });

            Button btnCancel = (Button) dialog
                    .findViewById(R.id.Alert_Dialog_Btn_Cancel);//button to dismiss
            btnCancel.setText("Cancel");
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
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

    /**
     * This method is to install apk file which is downloaded to sd card
     * on clicking "Update" button from alert dialog
     * @param dirPath
     */
    public void installApplication(final String dirPath) {
        Log.v(LOGTAG,"installApplication "+dirPath);
        File apkFile = new File(dirPath,"GenericPlayer.apk");
        Log.v(LOGTAG,"installApplication file path "+apkFile.getAbsolutePath());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(
                Uri.fromFile(apkFile),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);

        //after installation delete file
//        File file = new File(dirPath);
//        file.delete();
    }
}
