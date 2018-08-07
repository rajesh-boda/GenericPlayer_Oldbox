package com.ideabytes.qezytv.genericplayer;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ideabytes.qezytv.genericplayer.autoupdate.AsyncTaskToGetApkVersion;
import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.constants.FolderAndURLConstants;
import com.ideabytes.qezytv.genericplayer.database.GetChannelInfo;
import com.ideabytes.qezytv.genericplayer.fragments.FragmentExit;
import com.ideabytes.qezytv.genericplayer.fragments.FragmentToUpdateApp;
import com.ideabytes.qezytv.genericplayer.logs.Logger;
import com.ideabytes.qezytv.genericplayer.model.Pojo;
import com.ideabytes.qezytv.genericplayer.network.PingIP;
import com.ideabytes.qezytv.genericplayer.services.WatchDogToCheckPlayer;
import com.ideabytes.qezytv.genericplayer.utils.SendExceptionsToServer;
import com.ideabytes.qezytv.genericplayer.utils.Utils;
import com.ideabytes.qezytv.genericplayer.volley.SendPayerStatusToServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : Settings
 * author:  Suman
 * Created Date : 23-12-2015
 * Description : This activity is to show settings on the page
 * Modified Date : 25-04-2016
 * Reason: Removed wifi settings from page
 *************************************************************/
public class Settings extends Activity implements DeviceConstants,FolderAndURLConstants {
   private static final String LOGTAG = "Settings";
 //   public static TextView tvReason;
    private TextView IPAddress;

    private Switch switch1;
    private String TAG = "Settings";
    private SharedPreferences prefs;
    //InternetSpeedTest speedTest;

    //public static ProgressBar progressBar1;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
      //  setContentView(R.layout.settings_page);
        setContentView(R.layout.settings_page2);
     //   progressBar1 = (ProgressBar)findViewById(R.id.settingProgress);
        boolean netStatus = false;
        //check apk update on every time when clicked back button on the player, when n/w enabled

        /////////////////////////////////////////////////////////////
        if (isMyServiceRunning(WatchDogToCheckPlayer.class)) {
            try {
                Intent statusService = new Intent(Settings.this, WatchDogToCheckPlayer.class);
                stopService(statusService);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /////////////////////////////////////////////////////////
        try {
            netStatus = PingIP.isConnectingToInternet(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //to launch player
      //  Button btnPlayer = (Button) findViewById(R.id.btnPlayer);
        final RelativeLayout player_layout =(RelativeLayout)findViewById(R.id.player_layout);
      //  final TouchImageView tv=(TouchImageView)findViewById(R.id.player);


        //to set wifi
       // Button btnWifi = (Button) findViewById(R.id.btnWifi); not required for now
        //to set ethernet
       // Button btnEthernet = (Button) findViewById(R.id.btnEthernet);
        final RelativeLayout ethernet_layout =(RelativeLayout)findViewById(R.id.ethernet_layout);

        //to Check internet Bandwidth
      //  Button btnCkBand = (Button) findViewById(R.id.btnCheckBand);
        final RelativeLayout bandwidth_layout =(RelativeLayout)findViewById(R.id.bandwidth_layout);

        //to quit from application
       // Button btnExit = (Button) findViewById(R.id.btnExit);
        final RelativeLayout exit_layout =(RelativeLayout)findViewById(R.id.exit_layout);
        final Drawable highlight = getResources().getDrawable( R.drawable.highlight);
        final Drawable unhighlight = getResources().getDrawable( R.drawable.unhighlight);


//        IPAddress = (TextView)findViewById(R.id.ipaddress);
//        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
//        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
//        IPAddress.setText(ip);
//        switch1 = (Switch)findViewById(R.id.switch1);
//
//        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                // do something, the isChecked will be
//                // true if the switch is in the On position
//                if (isChecked) {
//                    String name = "toStart";
//                    prefs = getSharedPreferences("toStartActivity", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = prefs.edit();
//                    editor.putString(name, "no");
//                    editor.commit();
//                } else {
//                    String name = "toStart";
//                    prefs = getSharedPreferences("toStartActivity", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = prefs.edit();
//                    editor.putString(name, "yes");
//                    editor.commit();
//                }
//            }
//        });
  //      tvReason = (TextView) findViewById(R.id.tvReason);
       // Intent intent = getIntent();
    //    tvReason.setText(intent.getStringExtra(MESSAGE));
     //   tvReason.setTextSize(20);
       // blinkText(tvReason);
    //    tvReason.setTextColor(Color.RED);

        /*
        player_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Drawable highlight = getResources().getDrawable( R.drawable.highlight);
                player_layout.setBackground(highlight);

                return false;
            }
        });
*/
        player_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                player_layout.setBackground(highlight);

                bandwidth_layout.setBackground(unhighlight);

                ethernet_layout.setBackground(unhighlight);

                exit_layout.setBackground(unhighlight);



                //tv.setImageResource(R.drawable.player);
                GetChannelInfo getChannelInfo = new GetChannelInfo(getApplicationContext());
                if (getChannelInfo.getChannelInfo().length() > 0) {
                    Intent intent = new Intent(Settings.this, VideoActivity.class);
                    startActivity(intent);
                    finish();
                    //start service which restarts activity when video activity not on top
                    Intent serviceIntent = new Intent(Settings.this, WatchDogToCheckPlayer.class);
                    Pojo.getInstance().setIsUserStopped(false);
                    Pojo.getInstance().setServiceCheckPlayer(serviceIntent);
                    startService(serviceIntent);
                } else {
                    Intent intent = new Intent(Settings.this, ValidatorActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
//       // btnWifi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                displayView(v);
//            }
//        });
        bandwidth_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bandwidth_layout.setBackground(highlight);
                ethernet_layout.setBackground(unhighlight);
                player_layout.setBackground(unhighlight);
                 exit_layout.setBackground(unhighlight);
                displayView(v);
            }
        });

        ethernet_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ethernet_layout.setBackground(highlight);
               // player_layout.setPadding(0, 0, 0, 0);
               player_layout.setBackground(unhighlight);
              //  bandwidth_layout.setPadding(0, 0, 0, 0);
               // exit_layout.setPadding(0, 0, 0, 0);
                bandwidth_layout.setBackground(unhighlight);
                exit_layout.setBackground(unhighlight);
                displayView(v);
            }
        });
        exit_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit_layout.setBackground(highlight);

               player_layout.setBackground(unhighlight);
              bandwidth_layout.setBackground(unhighlight);
                ethernet_layout.setBackground(unhighlight);
               // System
            displayView(v);
            }
        });
        if (netStatus) {
           // check();
        }
    }

    /**
     * Displaying fragment view for selected button from list in the settings page
     * */
    private void displayView(View view) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //if any of the options chosen, then reason must be empty
   //     tvReason.setText("");
        // update the main content by replacing fragments
        switch (view.getId()) {
           // case R.id.btnExit:
            case R.id.exit_layout:
                Log.v(LOGTAG, "btnExit");
                FragmentExit fragmentExit = new FragmentExit();
                fragmentTransaction.replace(R.id.frame_container, fragmentExit).commit();
                break;
                /*
           // case R.id.btnWifi:
            case R.id.ethernet_layout:
                Log.v(LOGTAG, "btnWifi");
                FragmentWifi fragmentWifi = new FragmentWifi();
                fragmentTransaction.replace(R.id.frame_container, fragmentWifi).commit();
                break;
                */
           // case R.id.btnCheckBand:
            case R.id.bandwidth_layout:
                Log.v(LOGTAG, "btnWifi");
                Log.v(LOGTAG, "progress bar visible");
            //   progressBar1.setVisibility(View.VISIBLE);

                /*
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Progress bar", Toast.LENGTH_SHORT).show();
                        progressBar1.setVisibility(View.VISIBLE);

                    }
                });
                */

                //////////////////////////////////////////////////////
                /////////////////////////////////////////////////////////

                //BandwidthCheck ss = new BandwidthCheck();
             //   ss.getBandWidthURL();

                boolean netStatus = PingIP.isConnectingToInternet(getApplicationContext());


                //initialize Octoshape System
                if(netStatus) {
                    getBandWidthURL();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    /*
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.bandwidthplace.com/speedtest"));
                    Intent chooser = Intent.createChooser(sendIntent, "Choose Your Browser");
                    if (sendIntent.resolveActivity(getPackageManager()) != null) {
                        // startActivityForResult(chooser);
                        startActivity(chooser);
                    }
                    */
                }


                /*
                Intent intent1 = new Intent(Settings.this, BandwidthCheck.class);
                startActivity(intent1);
                finish();
                */
//
//
//                 speedTest = new InternetSpeedTest(Settings.this);
//
//                try {
//                    speedTest.execute();
//                   // String result = speedTest.execute().get();
//                    /*
//                    if (result != "")
//                    {
//                      //  progressBar1.setVisibility(View.INVISIBLE);
//                        Toast.makeText(getApplicationContext(), "Progress bar"+result, Toast.LENGTH_SHORT).show();
//
//                    }
//                    */
//                   // progressBar1.setVisibility(View.INVISIBLE);
//                   // Log.d(TAG, "onPostExecute: " + "speedTest2"+InternetSpeedTest.speedTest2);
//                    /*
//                    if(!InternetSpeedTest.speedTest2.equals(""))
//                    {
//                        AlertDialog.Builder builder1 = new AlertDialog.Builder(Settings.this);
//                        builder1.setTitle("Intenet Speed !");
//                        builder1.setMessage("Speed is"+InternetSpeedTest.speedTest2 + "mb/second");
//                        builder1.setCancelable(true);
//                        builder1.setPositiveButton(
//                                "Ok",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        dialog.cancel();
//                                    }
//                                });
//                        AlertDialog alert11 = builder1.create();
//                        alert11.show();
//
//                    }
//                    */
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//



                /*
                FragmentBandCheck fragmentBandCheck = new FragmentBandCheck();
                fragmentTransaction.replace(R.id.frame_container, fragmentBandCheck).commit();
                */
            /*commented below--------------
                Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.bandwidthplace.com/speedtest/"));
                Intent chooser = Intent.createChooser(sendIntent, "Choose Your Browser");
                if (sendIntent.resolveActivity(getPackageManager()) != null) {
                    // startActivityForResult(chooser);
                    startActivity(chooser);
                    // startActivityForResult(sendIntent, 1);
                }

            */
                break;
            //case R.id.btnEthernet:
            case R.id.ethernet_layout:

                Log.v(LOGTAG, "btnEthernet");
//                FragmentEtherNet fragmentEtherNet = new FragmentEtherNet();
//                fragmentTransaction.replace(R.id.frame_container, fragmentEtherNet).commit();
                Intent intent = new Intent(Settings.this, com.ideabytes.qezytv.genericplayer.ethernet.Ethernet.class);
                intent.putExtra(MESSAGE, USER_STOPPED_PLAY);
                startActivity(intent);
                finish();
//                Log.v(LOGTAG, "btnUpdate");
//                FragmentToUpdateApp fragmentToUpdateApp = new FragmentToUpdateApp();
//                fragmentTransaction.replace(R.id.frame_container, fragmentToUpdateApp).commit();
                break;
        }
        }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Log.v(LOGTAG, "speedTest cancel2");
       // System.err.println("speedTest cancel");

      //  speedTest.cancel(true);
        finish();
    }




    //back button listener
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
//                AlertDialogNetSettings alertDialogNetSettings = new AlertDialogNetSettings(VideoActivity.this);
//                alertDialogNetSettings.showDialog("Choose Your option");
                Log.v(LOGTAG, "speedTest cancel2");
                //Do Code Here
                // If want to block just return false

                Toast.makeText(getApplicationContext(),"Click any of the Options above",Toast.LENGTH_SHORT).show();
                return true;
            }
        } catch (Exception e) {
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(getApplicationContext());
            sendExceptionsToServer.sendMail(Settings.this.getClass().getName(), Utils.convertExceptionToString(e), "Exception");
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * This method is to check apk update from server
     * when there is a update (status 1) then show a alert dialog to install apk file or
     * if current version of apk is not matching with web version that is stored in text file then also show dialog
     * to install apk
     */

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public void showAlert(String speed)
    {
        Log.d(TAG, "onPostExecute: " + "show alert called" );
        AlertDialog.Builder builder1 = new AlertDialog.Builder(Settings.this);
        builder1.setTitle("Intenet Speed !");
        builder1.setMessage("Speed is"+speed+ "mb/second");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();

    }
    /////////////////////////////////////////////////////////////////
    public String getBandWidthURL() {
        final String[] bandwidthurl = {""};

        try {
            // GetChannelInfo getChannelInfo = new GetChannelInfo(getApplicationContext());
            //JSONObject inputToService = getChannelInfo.getChannelInfo();

            SendPayerStatusToServer sendPayerStatusToServer = new SendPayerStatusToServer(getApplicationContext());
            sendPayerStatusToServer.getBandWidthURL(new SendPayerStatusToServer.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    Log.e(LOGTAG,"Updating channel done, sending confirmation to server");
                    // Logger.logDebug(LOGTAG, "postConfirmationOnUpdate()", " confirm " + inputToService);
                    String responseCode = response.optString("responseCode").toString().trim();

                    if (responseCode.equalsIgnoreCase(RESPONSE_CODE_200)) {
                        try {
                            // final String serviceData = response.optString(DATA).toString().trim();
                            // JSONArray jArray = response.getJSONArray(DATA);
                            JSONObject image=response.getJSONObject("data");
                            bandwidthurl[0]=image.getString(BANDWIDTHURLKEY);
                            Log.v(LOGTAG, "bandwidthURL--"+bandwidthurl[0]);

                            //Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.bandwidthplace.com/speedtest/"));

                            Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(bandwidthurl[0]));
                            Intent chooser = Intent.createChooser(sendIntent, "Choose Your Browser");
                            if (sendIntent.resolveActivity(getPackageManager()) != null) {
                                // startActivityForResult(chooser);
                                startActivity(chooser);
                            }


                            /*
                            JSONArray imageattribute = response.getJSONArray("data");
                            for (int jsi = 0; jsi < imageattribute.length(); jsi++) {
                                JSONObject elementObj = new JSONObject(imageattribute.get(jsi).toString());
                                final String itemId = elementObj.getString(BANDWIDTHURLKEY).toString();
                                bandwidthurl[0] =itemId;
                            }
                            */
                            // String status = response.getJSONArray("data").getJSONObject(0).getString(BANDWIDTHURLKEY);
                            // System.err.println("status--"+status);
                            // JSONObject json_data = response.getJSONObject(0);
                            // bandwidthurl[0] = serviceData.getString(BANDWIDTHURLKEY).toString();

                            // return bandwidthurl;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    Logger.logDebug(LOGTAG, "postConfirmationOnUpdate()", "responseCode " + responseCode);
                    // final String serviceData = response.optString(DATA).toString().trim();
//            if (responseCode.equalsIgnoreCase(RESPONSE_CODE_200)) {
//                String status = jsonObject.getJSONArray("data").getJSONObject(0).getString(STATUS);
//            }
                }

                @Override
                public void onFailure(JSONObject response) {
                    Log.e(LOGTAG,"RETRIVING BANDWIDTH URL failed");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            // utils.generateNoteOnSD(utils.getPresentDateTime(getApplicationContext()), e.getMessage());
        }
        return bandwidthurl[0];
    }




    /////////////////////////////////////////////////////////////////
    private boolean check() {
        boolean status = false;
        try {
            AsyncTaskToGetApkVersion asyncTaskToGetApkVersion = new AsyncTaskToGetApkVersion(getApplicationContext());
            String apkUpgradeStatus = asyncTaskToGetApkVersion.execute().get();
            if (!apkUpgradeStatus.trim().equalsIgnoreCase("Exception")) {
                Log.v(LOGTAG, "apkUpgradeStatus " + apkUpgradeStatus);
                String array[] = apkUpgradeStatus.split(":");
                String apkStatus = array[0];
                Log.v(LOGTAG, "apkStatus " + apkStatus);
                String version = array[1];
                Log.v(LOGTAG, "web version " + version);
                Log.v(LOGTAG, "apk update apkUpgradeStatus " + apkUpgradeStatus);
                String versionPath = getFilesDir().getAbsolutePath() + File.separator + "GenericPlayer/Version";
                //once apk update status "1" then download apk file to folder
                if (apkStatus.equalsIgnoreCase(STATE_ONE)) {
                    //Write to text file to store web version to check updated apk installed or not
                    new Utils().generateNoteOnSD( VERSION_FILE_NAME, version);
                    //show dialog to install apk file
                    showAlertDialogOnUpdate();
                } else {
                    String currentVersion = new Utils(getApplicationContext()).getApkVersion();
                    Log.v(LOGTAG, "currentVersion " + currentVersion);
                    String textFileVersion = new Utils().readTextFile(versionPath, VERSION_FILE_NAME);
                    Log.v(LOGTAG, "text file version " + version);
                    if (!currentVersion.equalsIgnoreCase(textFileVersion)) {
                        //show dialog to install apk file
                        showAlertDialogOnUpdate();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    /**
     * This method is to show alert dialog to install updated apk
     */
    private void showAlertDialogOnUpdate() {
        //show dialog to install apk file
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        // Create and show the dialog.
        FragmentToUpdateApp newFragment = new FragmentToUpdateApp();
        newFragment.show(ft, "dialog");
    }

    /**
     * This is to blink text view
     * @param textView
     */
    private void blinkText(final TextView textView) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        textView.startAnimation(anim);
        textView.setTextSize(20);
    }
}
