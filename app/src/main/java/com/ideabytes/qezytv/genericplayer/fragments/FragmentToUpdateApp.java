package com.ideabytes.qezytv.genericplayer.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.ideabytes.qezytv.genericplayer.R;

import java.io.File;
import com.ideabytes.qezytv.genericplayer.autoupdate.AsyncTaskToDownloadApk;
import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.database.GetChannelInfo;
import com.ideabytes.qezytv.genericplayer.utils.Utils;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : FragmentToUpdateApp
 * author:  Suman
 * Created Date : 04-02-2016
 * Description : This fragment is to show dialog on update apk file
 * Modified Date : 04-02-2016
 * Reason: --
 *************************************************************/
public class FragmentToUpdateApp extends DialogFragment implements DeviceConstants {
    private String LOGTAG = "FragmentToUpdateApp";
    private Activity activity;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        activity  = getActivity();
        final String dirPath = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "GenericPlayer/Apk";
        return new AlertDialog.Builder(activity)
                .setTitle(ALERT_MESSAGE)
                .setMessage(UPDATE_APK_MESSAGE)
                .setNegativeButton(R.string.btnCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing (will close dialog)
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.btnUpdate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do something
                        try {
                            dialog.dismiss();
                            AsyncTaskToDownloadApk asyncTaskToDownloadApk = new AsyncTaskToDownloadApk(activity);
                            asyncTaskToDownloadApk.execute();
//                            if (response.startsWith("Apk file not found")) {
//                                //show dialog to intimate, there is a problem in downloading apk file
//                                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                                // Create and show the dialog.
//                                FragmentToShowAlert fragmentToShowAlert = new FragmentToShowAlert();
//                                fragmentToShowAlert.show(ft, "dialog");
//                            } else {
//                                //apk downloaded, so send confirmation to server
//                                AsyncTaskToPostApkUpgrade asyncTaskToPostApkUpgrade = new AsyncTaskToPostApkUpgrade(getActivity().getApplicationContext());
//                                asyncTaskToPostApkUpgrade.execute();
//                                installApplication(dirPath);
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .create();
    }
    /**
     * This method is to install apk file which is downloaded to sd card
     * on clicking "Update" button from alert dialog
     * @param dirPath
     */
    public void installApplication(final String dirPath) {
        try {
            Log.v(LOGTAG, "installApplication " + dirPath);
            String appVersion = new Utils(getActivity().getApplicationContext()).getApkVersion();
            GetChannelInfo getChannelInfo = new GetChannelInfo(getActivity().getApplicationContext());
            String versionFromDb = getChannelInfo.getChannelInfo().getString(DeviceConstants.APK_VERSION);
            File apkFile = new File(dirPath, APK_FILE_NAME);
            Log.v(LOGTAG, "installApplication file path " + apkFile.getAbsolutePath());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(
                    Uri.fromFile(apkFile),
                    "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().startActivity(intent);

            //after installation delete file
//        File file = new File(dirPath);
//        file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}