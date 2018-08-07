package com.ideabytes.qezytv.genericplayer;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ideabytes.qezytv.genericplayer.channel.ImageStorage;
import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.constants.FolderAndURLConstants;
import com.ideabytes.qezytv.genericplayer.database.Database_for_Inactive;
import com.ideabytes.qezytv.genericplayer.database.GetChannelInfo;
import com.ideabytes.qezytv.genericplayer.database.Inactive_status;
import com.ideabytes.qezytv.genericplayer.database.UpdateChannelInfo;
import com.ideabytes.qezytv.genericplayer.model.Pojo;
import com.ideabytes.qezytv.genericplayer.network.PingIP;
import com.ideabytes.qezytv.genericplayer.services.WatchDogToCheckPlayer;
import com.ideabytes.qezytv.genericplayer.services.WatchDogToUpdateStatus;
import com.ideabytes.qezytv.genericplayer.utils.Utils;
import com.octoshape.android.client.OctoStatic;
import com.octoshape.android.client.OctoshapePortListener;

import java.util.Timer;
import java.util.TimerTask;

import octoshape.osa2.Problem;
import octoshape.osa2.android.OctoshapeSystem;
import octoshape.osa2.android.StreamPlayer;
import octoshape.osa2.android.listeners.MediaPlayerListener;
import octoshape.osa2.android.listeners.StreamPlayerListener;
import octoshape.osa2.listeners.MultiStreamInfoListener;
import octoshape.osa2.listeners.OctoshapeSystemListener;
import octoshape.osa2.listeners.ProblemListener;
import octoshape.osa2.listeners.StreamInfoListener;
import octoshape.osa2.listeners.StreamSignalListener;
import octoshape.util.xml.XmlNodeView;

;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : VideoActivity
 * author:  Viplov
 * Created Date : 05-01-2016
 * Description : This activity is to play stream and to handle exceptional cases while playing video
 * Modified Date : 06-06-2017
 * Reason: Stream Status sending as codes to server and changing status by setters and getters methods
 *************************************************************/
public class VideoActivity extends Activity implements DeviceConstants,FolderAndURLConstants,SurfaceHolder.Callback {

    private final String LOGTAG = "VideoActivity";
    private Dialog dialog;
    private  VideoView vid;
    private SurfaceView mSurface;
    private SurfaceHolder mHolder;
    private OctoshapeSystem os;
    private MediaPlayer mMediaPlayerStream;
    private StreamPlayer mStreamPlayer;
    private boolean over = false;
    private boolean logotime = false;
    private  int time_count = 0;
    private boolean dialog_showing = false;
    private int octoStatic = 0;
    private boolean back_button_pressed = false;
    public static VideoActivity instance;
    private UpdateChannelInfo updateChannelInfo;
    private boolean isAlreadyInit = false;
    private  int buffering_status = 0;
    private TextView crashTV;
    SharedPreferences prefs;
    //boolean buffer=true;

    private Database_for_Inactive db;
    private boolean isplaying=false;
    /**
     * Called when the activity is first created. Creating views, GUI, setting
     * listeners. Here the OSA is initialized.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //set the new Content of your activity
        setContentView(R.layout.video);
        updateChannelInfo = new UpdateChannelInfo(getApplicationContext());
        db = new Database_for_Inactive(getApplicationContext());

//        logoStatus(0);

        int count = db.getContactsCount();
        if(count > 0){

        }else{
            db.addStatus(new Inactive_status("static","0"));
        }
        crashTV = (TextView)findViewById(R.id.crashText);
        crashTV.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                throw new RuntimeException("This is a crash");
            }
        });

        instance = this;
        Log.v(LOGTAG, "service running " + isMyServiceRunning(WatchDogToUpdateStatus.class));
        //start service to update video player status to server

        // Setup views and holder to be used by MediaPlayer
        mSurface = (SurfaceView) findViewById(R.id.surface);
        mHolder = mSurface.getHolder();
        mHolder.addCallback(this);
        //mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //show dialog until video getting ready to play

       // showClientLogo(0);
        //added by rajesh 01June18
        boolean netStatus = PingIP.isConnectingToInternet(getApplicationContext());
        if(netStatus)
        {
            showClientLogo(0);
        }
        else
        {
            showClientLogo(2);
        }

        // setPlayerState(ACTIVITY_STATUS_FALSE, CODE_STREAM_NOT_AVAILABLE);
    }

    /**
     * Creating OctoshapeSystem, adding ProblemListener for which we set to the
     * language of the OS and setting OctoshapePortListener triggering a
     * callback once the Octoshape service/client has started successfully.
     */
    public void initOctoshapeSystem() {
        Log.v(LOGTAG, "OS instantiated ");
        // OctoStatic.enableLog(true, FOLDER_PATH_LICENSE);if enabled this, change service in manifest to StreamServiceDebug
        final String streamUrl;
        try {



            streamUrl = new GetChannelInfo(getApplicationContext()).getChannelInfo().getString(CHANNEL_LINK);
            //streamUrl = "octoshape://streams.octoshape.net/ideabytes/vod/QP/dreamwaver6.mp4";
            // Create OctoshapeSystem

            if (isAlreadyInit) {
            //if(octoStatic == 1){
                if (mStreamPlayer != null) {
                    shutdownOs();
                    //  mStreamPlayer.requestPlayAbort();
                    mStreamPlayer.requestPlayAbort();
                    // mStreamPlayer = setupStream("octoshape://streams.octoshape.net/ideabytes/live/ib-ch60/auto");
//                    mStreamPlayer = setupStream(streamUrl);
//                    mStreamPlayer.requestPlay();
//                    isAlreadyInit = true;
                    Log.v(LOGTAG, "OS already instantiated ");
                }
            } else {
               // shutdownOs();
                Log.v(LOGTAG, "OS instantiated first time");
                os = OctoStatic.create(this, problemListener, new OctoshapePortListener() {
                    @Override
                    public void onPortBound(String s, int i) {
                        Log.v(LOGTAG, "onPortBound() "+s);
                        octoStatic = 1;
                    }
                });
            }


            os.setOctoshapeSystemListener(new OctoshapeSystemListener() {
                // called once the Octoshape service/client has started.
                @Override
                public void onConnect(String authId) {
                    Log.v(LOGTAG, "onConnect " + authId);

                }

            });
            mStreamPlayer = setupStream(streamUrl);
            mStreamPlayer.requestPlay();

            Log.v(LOGTAG, "Added Stream Url" + streamUrl);

            // Adding AndroidMediaPlayer
            os.addPlayerNameAndVersion(OctoshapeSystem.MEDIA_PLAYER_NATIVE, OctoshapeSystem.MEDIA_PLAYER_NATIVE, "" + Build.VERSION.RELEASE);
            os.open();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Adding a stream to the OctoMediaPlayer. Creates an StreamPlayer the
     * StreamPlayer instance is initiated with it's own UrlListener and
     * ProblemListener.
     *
     * @param stream
     *            link (e.g., octoshape://ond.octoshape.com/demo/ios/bbb.mp4)
     * @return StreamPlayer on which we can request playback.
     */
    public StreamPlayer setupStream(final String stream) {
        Log.d(LOGTAG, "Setting up stream: " + stream);
        final StreamPlayer sp = os.createStreamPlayer(stream);
        sp.setStreamInfoListener(new StreamInfoListener() {
            @Override
            public void gotStreamInfo(String s, String s1) {
                Log.d(LOGTAG, "gotStreamInfo: s " + s+" s1 "+s1);

            }
        });
        sp.setMultiStreamInfoListener(new MultiStreamInfoListener() {
            @Override
            public void gotMultiStreamInfo(String[] strings, int[] ints) {

            }

            @Override
            public void gotBaseStreamInfo(String s, int i) {
                Log.v(LOGTAG, s + " bit rate " + i / 1000);

                try{
                Pojo pojo = Pojo.getInstance();
                pojo.setVideoBitRate(i / 1000);
                String name = "br";
                String value = String.valueOf(i / 1000);
                Inactive_status inactive_status = db.getStatus(1);
                String DorS = inactive_status.getName();
                String db_value2 = inactive_status.getInactiveStatus();

                db.updateContact(new Inactive_status(1,DorS, value));

                prefs = getSharedPreferences("bitrates", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(name, value);
                editor.commit();
                }
                catch(Exception e)

                {
                    e.printStackTrace();
                }

            }
        });
        sp.setListener (new StreamPlayerListener() {

            /**
             * Receiving new URL from the streamplayer object either due to
             * requesting playback, seeking or experiencing a bitrate/resolution
             * changes requiring the re-initialization of the Player.
             *
             * @param url
             *            to be passed to the media player
             * @param seekOffset
             *            offset we have seek to in milliseconds
             */
            @Override
            public void gotUrl(String url, long seekOffset, MediaPlayerListener mpl) {
                try {
                    Log.v(LOGTAG, "url " + url + " seekOffset " + seekOffset);
//                    if(stream_status == false) {
//                        Log.v(LOGTAG, "Stream not available to start the playStream");
//                    }else {
//                        Log.v(LOGTAG, "starting the playStream");
                    playStream(Uri.parse(url), mpl);
                    //}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /**
             * Resets an on-demand file duration previously reported in
             * resolvedOsaSeek(..) method
             */
            @Override
            public void gotNewOnDemandStreamDuration(long duration) {
                Log.v(LOGTAG, "gotNewOnDemandStreamDuration " + duration);
            }

            /**
             * Called if the stream is seekable using the media player's own
             * native seeking functionality (e.g., the Android MediaPlayer does
             * the seeking for us).
             */
            @Override
            public void resolvedNativeSeek(boolean isLive, String playerId) {
                Log.v(LOGTAG, "resolvedNativeSeek");
            }

            /**
             * Called if it is not possible to seek in the stream.
             */
            @Override
            public void resolvedNoSeek(boolean isLive, String playerId) {
                Log.v(LOGTAG, "resolvedNoSeek isLive " + isLive + " playerId " + playerId);
            }

            @Override
            /**
             * Called when stream support OsaSeek / DVR
             */
            public void resolvedOsaSeek(boolean isLive, long duration, String playerId) {
                Log.v(LOGTAG, "resolvedOsaSeek isLive" + isLive);
            }
        });
        sp.setStreamSignalListener(new StreamSignalListener() {
            @Override
            public void gotStreamSignal(String s, XmlNodeView xmlNodeView) {
                Log.e(LOGTAG, "signal " + s);
            }
        });
        sp.setProblemListener(problemListener);
        return sp;
    }
    /**
     * Setting up and playing a received media URL
     *
     * @param mediaUrl URL which needs to be passed to a media player
     * @param mpl MediaPlayerListen used for reporting stream start and end
     */
    protected void playStream(final Uri mediaUrl, final MediaPlayerListener mpl) {

        Log.d(LOGTAG, "playStream: Now playing: " + mediaUrl);
        mMediaPlayerStream = new MediaPlayer();

        mMediaPlayerStream.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayerStream.reset();
        try {
            mMediaPlayerStream.setDisplay(mHolder);
            mMediaPlayerStream.setDataSource(this, mediaUrl);
            mMediaPlayerStream.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mpl.onMediaPlaybackCompleted();
                    Log.v(LOGTAG, "onCompletion");
                    //when on completion is called , just make bit rate as 0, this will
                    // restart system in demon process
                }
            });
            mMediaPlayerStream.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    Log.d(LOGTAG, "MediaPlayer.OnInfoListener: " + what);
                    if (MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START == what) {
                        Log.v(LOGTAG, "START RENDERING ");
                        //dismissClientLogo();
                        buffering_status = 0;
                        mpl.onMediaPlaybackStarted();
                        over = false;
                    }
                    else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START && over) {
                        Log.e(LOGTAG, "MEDIA_INFO_BUFFERING_START");
                        // restart function

                        logoStatus(0);
                        logotime = true;
                        updateChannelInfo.updateStatus("400");

/////////////////////////////////////////////////////////////////////////////////////////////
/*
                        if (!dialog.isShowing()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e(LOGTAG, ""+MediaPlayer.MEDIA_INFO_BUFFERING_START);


                                        ////////////////////////////by rajesh
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                //Do something after 10 seconds
                                              //  showClientLogo();
                                            }
                                        }, 10000);

                                    ////////////////////by rajesh

                                 //   showClientLogo(2);
                                }
                            });
                        }

*/
                     ////////////////////////////////////////////////////////////////////////////////

                        // buffering_status is raised when the buffering is started. it will be set 1 and will be set to 0 if the buffering ends
                        // system will check for this buffering status every 10 seconds to show logo or not
                        buffering_status = 1;

                    }
                    else if (what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
                        Log.e(LOGTAG, "Stream no available, video restarting");
                        // restart function

                      //  if (!dialog.isShowing()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e(LOGTAG, "Stream no available, video restarting439");
                                    showClientLogo(2);
                                    logotime = false;
                                }
                            });
                       // }

                        if (!Pojo.getInstance().isInactive()) {
                            updateChannelInfo.updateStatus("404");
                            // Utils.sendStatus(getApplicationContext(), 404);
                        }
                    }
                    else if (what == MediaPlayer.MEDIA_INFO_METADATA_UPDATE) {

                        Log.e(LOGTAG, "Stream no available, video restarting");
                        // restart function

                      //  if (!dialog.isShowing()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e(LOGTAG, "Stream no available, video restarting460");
                                    showClientLogo(2);
                                    logotime = false;
                                }
                            });
                      //  }

                        if (!Pojo.getInstance().isInactive()) {
                            updateChannelInfo.updateStatus("404");
                            // Utils.sendStatus(getApplicationContext(), 404);
                        }


                    } else if (what == MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING) {

                        Log.e(LOGTAG, "Stream no available, video restarting");
                        // restart function

                       // if (!dialog.isShowing()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e(LOGTAG, "Stream no available, video restarting479");
                                    showClientLogo(2);
                                    logotime = false;
                                }
                            });
                       // }

                        if (!Pojo.getInstance().isInactive()) {
                            updateChannelInfo.updateStatus("404");
                            // Utils.sendStatus(getApplicationContext(), 404);
                        }

                    }

                    else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END)
                    {
                        Log.i(LOGTAG, "Video buffering completed, started playing");

                        if (!Pojo.getInstance().isInactive())
                        {
                            buffering_status = 0;
                            updateChannelInfo.updateStatus("200");

                            logotime = false;
                            time_count = 0;

                            dismissClientLogo();
                            Log.e(LOGTAG, "dismissClientLogo, dismissClientLogo509");
                            Utils.sendStatus(getApplicationContext(), 200);
                            over = true;
                        }

                    }
                    else {
                        Log.v(LOGTAG, "Starting, first time ");
                        if (!Pojo.getInstance().isInactive()) {
                            buffering_status = 0;
                            over = true;
                        }
                    }
                    return false;
                }
            });
//                mMediaPlayerStream.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        Log.v(LOGTAG, "PLAYERBACK STARTED");
//                        mpl.onMediaPlaybackStarted();
//                    }
//                });
            mMediaPlayerStream.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d("MediaPlayer", "PLAYER READY");
                    mp.start();
                    System.err.println("MP is playing...");
                    isplaying=true;
                    // dismissClientLogo();
                }
            });
//            mMediaPlayerStream.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener(){
//               public void onBufferingUpdate (MediaPlayer mp,
//                                        int percent){
//                   Log.d(LOGTAG, "Buffer is :" +percent);
//               }
//            });
            mMediaPlayerStream.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.d(LOGTAG, "MediaPlayer error:" + what + ":" + extra);
                    //when on error is called , just make but rate as 0, this will
                    // restart system in demon process
                    GetChannelInfo getChannelInfo = new GetChannelInfo(getApplicationContext());
                    if (!getChannelInfo.getStatus().equalsIgnoreCase("401")) {
                        updateChannelInfo.updateStatus("404");
                        //Utils.sendStatus(getApplicationContext(), 404);
                    }
                    return false;
                }
            });
            mMediaPlayerStream.prepareAsync();
            //mMediaPlayerStream.start();

        } catch (final Exception e) {
            //when on completion is called , just make but rate as 0, this will
            e.printStackTrace();
        }
    }
    //back button listener
    @Override
    public void onBackPressed() {

        back_button_pressed = true;
        super.onBackPressed();
        /*
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
*/
        try {
            Log.v(LOGTAG, " back button pressed ");
            //before restart shutdown os first
            back_button_pressed = true;
            shutdownOs();
            // clear_player();

            Intent intent = new Intent(VideoActivity.this, com.ideabytes.qezytv.genericplayer.Settings.class);
            intent.putExtra(MESSAGE, USER_STOPPED_PLAY);
            startActivity(intent);
            finish();
            //stop service which restarts player, when video activity not on top
            if (Pojo.getInstance().getServiceCheckPlayer() != null) {
                stopService(Pojo.getInstance().getServiceCheckPlayer());
            }
            //update state to model class
            updateChannelInfo.updateStatus("404");
            //call Settings page on back button press on video
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * This method called in error cases which restarts system to catche next state of the player
     * @param error <>reason for error to stop player</>
     */
    public void error(final String error) {
        try {
            Log.e(LOGTAG, " error() reason for stop, " + error);
            //updateChannelInfo.updateStatus("404");
            //if dialog is not shown, show dialog and restart os to catch next state of the player
            //first shutdown os on error
            shutdownOs();

            boolean netStatus = PingIP.isConnectingToInternet(getApplicationContext());


            //initialize Octoshape System
            if(netStatus) {
                initOctoshapeSystem();
            }

        } catch (Exception e) {
            //store error log in text file ,create file name with date and time
            //  new Utils().generateNoteOnSD(FOLDER_PATH_LICENSE, dateTime, dateTime + "::" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This method is to shutdown OctoShape System on any error
     */
    public void shutdownOs() {
        if (mMediaPlayerStream != null) {
            mMediaPlayerStream.stop();
            mMediaPlayerStream.reset();
            mMediaPlayerStream.release();
            mMediaPlayerStream = null;
        }
        if(octoStatic == 1) {

            OctoStatic.terminate(new Runnable() {
                @Override
                public void run() {
                    //Show dialog if not showing
                    Log.v(LOGTAG, "Octoshape System shutdown ");
                }
            });
            octoStatic = 0;
            isAlreadyInit = false;
        }
    }

    public void clear_player(){
        if(mMediaPlayerStream != null){
            mMediaPlayerStream.release();
            mMediaPlayerStream = null;
        }
        if(mStreamPlayer != null){
            mStreamPlayer.close(null);
        }
    }



    /**
     * This method is to show dialog with client dialog_to_show_client_logo up to player is ready to start video, once video started this
     * dialog will be dismissed.
     */
    public void showClientLogo(int i) {
        Bitmap imageLogo = null;

        try {
            //dialog display
            if(!dialog_showing) {
                Log.v(LOGTAG, " show dialog called ");
                dialog = new Dialog(VideoActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //to show full screen dialog
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                LayoutInflater factory = LayoutInflater.from(VideoActivity.this);
                final View view = factory.inflate(R.layout.dialog_to_show_client_logo2, null);
                dialog.setContentView(view);
                dialog_showing = true;
            }
            TextView status = (TextView) dialog.findViewById(R.id.textView);
           // Thread.sleep(2000); //1000 milliseconds is one second.
            //Thread.
            //wait(200);
         //   Log.e(LOGTAG, " Buffering testing..>"+buffer);
           // if(buffer)
            {
                Log.e(LOGTAG, " Buffering testing..1");
              //  buffer=false;
               // Thread.sleep(1000);
               // wait(500);
                /*
                Handler handler = new Handler();
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        // do something
                    }
                }, 3000);
                */
            }

            switch (i) {
                case 0:
                    status.setBackgroundColor(getResources().getColor(R.color.green));


                    status.setText("Buffering");
                    break;
                case 1:
                    status.setBackgroundColor(getResources().getColor(R.color.orange));
                    status.setText("PI701");
                    break;
                case 2:
                    status.setBackgroundColor(getResources().getColor(R.color.red));
                    status.setText("NS801");
                    break;
                default:
                    status.setBackgroundColor(getResources().getColor(R.color.green));
                    break;
            }
            ImageView logo = (ImageView) dialog.findViewById(R.id.imageView);
            //get logo of client from sd card is downloaded after license active to show on dialog
            imageLogo = ImageStorage.loadFromFile(FOLDER_TO_STORE_LOGO + "/logo.jpg");
            if (imageLogo == null) {
                logo.setBackgroundResource(R.drawable.default_logo);
            }
            else {
                logo.setImageBitmap(imageLogo);
            }
            //back button press listener on dialog
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    Intent intent = new Intent(VideoActivity.this, com.ideabytes.qezytv.genericplayer.Settings.class);
                    startActivity(intent);
                    finish();
                    //shutdown OS
                    // shutdownOs();
                    //dismiss client default_logo
                    dismissClientLogo();
                    Log.e(LOGTAG, "dismissClientLogo, dismissClientLogo773");
                    //call Settings page on back button press on client default_logo
                    // Log.v(LOGTAG,"back dialog");
                    return false;
                }
            });
            dialog.show();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
//
//    public void showClientLogo1(int i) {
//        Bitmap imageLogo = null;
//
//        try {
//            //dialog display
//            if(!dialog_showing) {
//                Log.v(LOGTAG, " show dialog called ");
//                dialog = new Dialog(VideoActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                //to show full screen dialog
//                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//                LayoutInflater factory = LayoutInflater.from(VideoActivity.this);
//                final View view = factory.inflate(R.layout.dialog_to_show_client_video, null);
//                dialog.setContentView(view);
//                dialog_showing = true;
//            }
//            TextView status = (TextView) dialog.findViewById(R.id.textView);
//            status.setVisibility(View.INVISIBLE);
//            switch (i) {
//                case 0:
//                    status.setBackgroundColor(getResources().getColor(R.color.green));
//                    status.setText("Buffering");
//                    break;
//                case 1:
//                    status.setBackgroundColor(getResources().getColor(R.color.orange));
//                    status.setText("PI701");
//                    break;
//                case 2:
//                    status.setBackgroundColor(getResources().getColor(R.color.red));
//                    status.setText("NS801");
//                    break;
//                default:
//                    status.setBackgroundColor(getResources().getColor(R.color.green));
//                    break;
//            }
//            vid = (VideoView) dialog.findViewById(R.id.videoView);
//            //get logo of client from sd card is downloaded after license active to show on dialog
//            imageLogo = ImageStorage.loadFromFile(FOLDER_TO_STORE_VIDEO + "/video.mp4");
//          //  if (imageLogo == null) {
//                //logo.setBackgroundResource(R.drawable.default_logo);
//            //String uriPath = new Utils().readTextFile(FolderAndURLConstants.FOLDER_PATH_LICENSE, "video.mp4");
//            String uriPath = FolderAndURLConstants.FOLDER_TO_STORE_VIDEO+"/video.mp4";
//                vid.setVideoURI(Uri.parse(uriPath));
//                vid.start();
//           // } else {
//                //logo.setImageBitmap(imageLogo);
//           // }
//            //back button press listener on dialog
//            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//                @Override
//                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                    Intent intent = new Intent(VideoActivity.this, com.ideabytes.qezytv.genericplayer.Settings.class);
//                    startActivity(intent);
//                    finish();
//                    //shutdown OS
//                    // shutdownOs();
//                    //dismiss client default_logo
//                    dismissClientLogo();
//                    Log.e(LOGTAG, "dismissClientLogo, dismissClientLogo845");
//                    //call Settings page on back button press on client default_logo
//                    // Log.v(LOGTAG,"back dialog");
//                    return false;
//                }
//            });
//            dialog.show();
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    private void logoStatus(final int startTime) {
        final Timer timer = new Timer();

        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                if(logotime) {

                    Log.d(LOGTAG, "Logo time counting "+ time_count);

                    // if (time_count > 1 && time_count < 2) {
                    if (time_count > 24) {
                        GetChannelInfo getChannelInfo = new GetChannelInfo(getApplicationContext());
                        if (!getChannelInfo.getStatus().equalsIgnoreCase("404")) {
                            updateChannelInfo.updateStatus("404");
                            Log.d(LOGTAG, "Updated to 404");
                        }
                        if (!dialog.isShowing()) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e(LOGTAG, "Stream no available, video restarting877");
                                    showClientLogo(2);
                                    logotime = false;
                                    Log.d(LOGTAG, "showing logo ");
                                }
                            });
                        }

                        time_count = 0;
                        timer.cancel();
                        timer.purge();

                    }
//                    else if(time_count > 20){
//                        GetChannelInfo getChannelInfo = new GetChannelInfo(getApplicationContext());
//                        if (!getChannelInfo.getStatus().equalsIgnoreCase("404")) {
//                            updateChannelInfo.updateStatus("404");
//                            Log.d(LOGTAG, "Updated to 404");
//                        }
//                        runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    showClientLogo(2);
//                                    logotime = false;
//                                    Log.d(LOGTAG, "showing logo ");
//                                }
//                            });
//                        time_count = 0;
//                        timer.cancel();
//                        timer.purge();
//                   }
                    else{
                        time_count++;
                    }
                }else{
                    timer.cancel();
                    timer.purge();
//                    GetChannelInfo getChannelInfo = new GetChannelInfo(getApplicationContext());
//                    if (getChannelInfo.getStatus().equalsIgnoreCase("200")) {
//                        if (dialog.isShowing()) {
//                            dismissClientLogo();
//                        }
//                    }


                }
            }
        };
        // here we can set the timer to reset watchdog time
        timer.schedule(timerTask, 1 * 1000, 3 * 1000);
    }

    /**
     * This method is to dismiss dialog if it is showing
     */
    private void dismissClientLogo1() {
        try {
            if (dialog.isShowing()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(LOGTAG, " dismissClientLogo block called ");
                        vid.stopPlayback();
                       // vid.setVisibility(View.INVISIBLE);
                        dialog_showing = false;
                        dialog.cancel();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroyThis(){
        //android.os.Process.killProcess(android.os.Process.myPid());
        Log.i(LOGTAG, " destroyThis called ");
        shutdownOs();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void dismissClientLogo() {
        try {
            if (dialog.isShowing()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(LOGTAG, " dismissClientLogo block called ");
                        dialog_showing = false;
                        dialog.cancel();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




        @Override
    protected void onResume() {
        // this.registerReceiver(receiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        super.onResume();
        /*
        Log.v(LOGTAG, "VideoonResume1");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.v(LOGTAG, "VideoonResume2");
        */
        // buffer=true;

//        GetChannelInfo getChannelInfo = new GetChannelInfo(getApplicationContext());
//        if (!getChannelInfo.getStatus().equalsIgnoreCase("401")){
//            //initialize Octoshape System
//            initOctoshapeSystem();
//        }
        if (!isMyServiceRunning(WatchDogToUpdateStatus.class)) {
            try {
                Intent statusService = new Intent(this, WatchDogToUpdateStatus.class);
                startService(statusService);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!isMyServiceRunning(WatchDogToCheckPlayer.class)) {
            try {
                Intent statusService = new Intent(this, WatchDogToCheckPlayer.class);
                startService(statusService);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.registerReceiver(networkChangeReceiver,new IntentFilter(ACTION_NETWORK));
        this.registerReceiver(receiver,new IntentFilter(SEND_STATUS_ACTION));
    }
    //
    @Override
    protected void onPause() {
        //un registering network check broadcast receiver before leaving video activity
        // unregisterReceiver(receiver);
        Log.v(LOGTAG, "onPause");
        super.onPause();
        //commented on 25May18 by viplov
        //uncommented on 04June18 by rajesh
        /////////////////////////////////////////////
        shutdownOs();
        if(back_button_pressed){
            if (isMyServiceRunning(WatchDogToCheckPlayer.class)) {
                try {
                    Intent statusService = new Intent(VideoActivity.this, WatchDogToCheckPlayer.class);
                    stopService(statusService);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /////////////////////////////////////////////////////////////
        unregisterReceiver(networkChangeReceiver);
        unregisterReceiver(receiver);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.v(LOGTAG, "onStop");
        //shutdown Octoshape System before leaving video activity
        //shutdownOs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(LOGTAG, "onDestroy"+back_button_pressed);
        //shutdownOs() by rajesh 070618
        if(!back_button_pressed){

    }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(LOGTAG, "surfaceCreated ");
//        if (!Pojo.getInstance().isInactive()) {
//            Log.e(LOGTAG, "License Inactive status " + Pojo.getInstance().isInactive());
//            //initialize Octoshape System
//            initOctoshapeSystem();
//        }

        GetChannelInfo getChannelInfo = new GetChannelInfo(getApplicationContext());
        if (!getChannelInfo.getStatus().equalsIgnoreCase("401")){
            //initialize Octoshape System
            initOctoshapeSystem();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(LOGTAG, "surfaceChanged ");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(LOGTAG, "surfaceDestroyed ");
    }
    public String getActivityName() {
        return VideoActivity.this.getClass().getName();
    }
    public static Activity getActivity() {
        return instance;
    }
    /**
     * This method is to check service running status
     * @param serviceClass
     * @return true or false
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
    ProblemListener problemListener = new ProblemListener() {
        @Override
        public void gotProblem(Problem problem) {
            // restart system in demon process
            Log.e(LOGTAG, "got problem message " + problem.getMessage());
            Log.e(LOGTAG, "got problem error code " + problem.getErrorCode());

//            if(mMediaPlayerStream!=null) {
//                Log.e(LOGTAG, "got problem error code -->" );
//                mMediaPlayerStream.pause();
//                System.err.println("isplaying-->-->"+isplaying);
//                if(isplaying) {
//                    System.err.println("isplaying-->"+isplaying);
//                    SystemClock.sleep(60 * 1000);
//                    isplaying=false;
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        showClientLogo(2);
//                    }
//                });
//            }

            try {
                GetChannelInfo getChannelInfo = new GetChannelInfo(getApplicationContext());
                if (!getChannelInfo.getStatus().equalsIgnoreCase("401")){
                    shutdownOs();
                    //clear_player();
                    //update state to model class
                    updateChannelInfo.updateStatus("404");
                    // Utils.sendStatus(getApplicationContext(), 404);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new NullPointerException("problem is null");
            }
        }
    };
    /*
        * Receiver will recieve input an sets the database accordingly
        * 404 if stream not available
        * 401 if license is inactive
        * 200 if everything is OK
        * pager just call addPagerControl() on the horzizontal pager.
        */
    private  BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.v(LOGTAG, "intent status " + intent.getIntExtra("status", 100));
            int resultCode = intent.getIntExtra("status",100);
            if (resultCode == 404) {
//                updateChannelInfo.updateStatus("404");
                Pojo.getInstance().setIsInactive(false);
                Log.e(LOGTAG, "Player Stopped, call restart");
                try {
                    //  Thread.sleep(60*1000);
                    error("Player Stopped");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (resultCode == 201) {
                Log.e(LOGTAG, "License Re-Activated, call restart immediately ");
                error("License Re-Activated, call restart immediately");
                Pojo.getInstance().setIsInactive(false);
            } else if (resultCode == 401) {
                Log.e(LOGTAG, "License Inactivated, stop play ");
                // if default_logo not showing, then only show default_logo
                Pojo.getInstance().setIsInactive(true);

                if (!Pojo.getInstance().isLogoShowing()) {
                    try {


                       // if (isAlreadyInit == true) {
                            if (mMediaPlayerStream != null) {
                                mMediaPlayerStream.stop();
                            }
                       // }
                    }catch (Exception e) {
                        // This will catch any exception, because they are all descended from Exception
                        System.out.println("Error " + e.getMessage());

                    }
                }

                updateChannelInfo.updateStatus("401");

              //  if (!dialog.isShowing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(LOGTAG, "Stream no available, video restarting1147");
                            showClientLogo(2);
                            logotime = false;
                        }
                    });
               // }
                // isInactive = true;



            } else {

//                if (dialog.isShowing()) {
//                    Log.v(LOGTAG, "image is still there so restarting");
//                        updateChannelInfo.updateStatus("404");
//                   // error("Image is still present");
//                }

                GetChannelInfo getChannelInfo = new GetChannelInfo(getApplicationContext());
                if (getChannelInfo.getStatus().equalsIgnoreCase("401")){
                    updateChannelInfo.updateStatus("404");
                    error("re-activate");
                    Pojo.getInstance().setIsInactive(false);
                }
//                else if (getChannelInfo.getStatus().equalsIgnoreCase("404")) {
//                    error("re-activate");
//                }


            //    SharedPreferences prefs = getSharedPreferences("bitrates", Context.MODE_PRIVATE);
          //      String bitrate1 = prefs.getString("br", "");
             //   int bit = Pojo.getInstance().getVideoBitRate();
            //    String bitrate2 = String.valueOf(bit);
                //   if(db.getContactsCount()>0) {
              // Inactive_status inactive_status = db.getStatus(1);
             //  String bitrate3 = inactive_status.getInactiveStatus();
                // }

                //  Log.v(LOGTAG, " BIT RATE " + bitrate1 + " " + bitrate2+" "+bitrate3);

                Log.v(LOGTAG, "Player running good");
            }
        }
    };

    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context,  "Network change receiver", Toast.LENGTH_SHORT).show();
            if (PingIP.isConnectingToInternet(getApplicationContext())) {
                Log.v(LOGTAG, "network changed and is connected to network");

                GetChannelInfo getChannelInfo = new GetChannelInfo(getApplicationContext());
                if (!getChannelInfo.getStatus().equalsIgnoreCase("401")){
                    //initialize Octoshape System
                    initOctoshapeSystem();
                }


            }
            //when network lost, not to handle, error block automatically calls,
            //just when there is network changes and connected then start system so that we can avoid repeating
        }
    } ;
}

