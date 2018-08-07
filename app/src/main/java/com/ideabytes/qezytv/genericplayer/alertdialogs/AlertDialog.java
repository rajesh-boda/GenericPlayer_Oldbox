package com.ideabytes.qezytv.genericplayer.alertdialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.ideabytes.qezytv.genericplayer.R;

import com.ideabytes.qezytv.genericplayer.ValidatorActivity;
import com.ideabytes.qezytv.genericplayer.constants.DBConstants;
import com.ideabytes.qezytv.genericplayer.database.DeleteData;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : AlertDialog
 * author:  Suman
 * Created Date : 09-12-2015
 * Description : Customized dialog to show customer dialog_to_show_client_logo on any exception
 * Modified Date : 25-04-2016
 * Reason: On ok button click delete channel info from db and navigate to validator
 *************************************************************/
public class AlertDialog extends Dialog implements DBConstants {
    private Context context;

    public AlertDialog(Context context) {
        super(context);
        this.context = context;
    }

    public void showDialog(final int code,final String message) {
        try {
            final Dialog dialog = new Dialog(context, R.style.PauseDialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //this is not to close alert dialog on out side touch
            dialog.setCanceledOnTouchOutside(false);
            //setting view to alert dialog
            dialog.setContentView(R.layout.alert_dialog);
            //settings params to dialog
            getWindow().setFlags(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            TextView tvTitle = (TextView) dialog
                    .findViewById(R.id.Alert_Dialog_Title);//text view to display alert dialog title
            tvTitle.setText("Alert Message");
            TextView tvMessage = (TextView) dialog
                    .findViewById(R.id.Alert_Dialog_Message);//text view to display alert dialog message
            tvMessage.setText(message);
            Button inunClose = (Button) dialog
                    .findViewById(R.id.Alert_Dialog_Btn_Ok);//button to dismiss

            //back button click listener of dialog to close dialog
            inunClose.setText("OK");
            inunClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (code == 202) {
                        //delete complete channel info if stored
                        DeleteData deleteData = new DeleteData(context.getApplicationContext());
                        deleteData.deleteData();
                        //activity.finish(); do not quit, navigate to validator activity when license wrong
                        Intent intent = new Intent(context, ValidatorActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                        //dismiss dialog
                        dialog.dismiss();
                        //quit
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
                    return true;
                }
            });
            dialog.show();
        } catch (Exception e) {
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(context);
            sendExceptionsToServer.sendMail(context.getClass().getName(), Utils.convertExceptionToString(e),"Exception");
        }
    }
}
