package com.ideabytes.qezytv.genericplayer.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by suman on 25/2/16.
 */
public class ConnectionService extends Service {

    // Constant
    public static int TAG_INTERVAL = 60*1000;
    public static String TAG_URL_PING = "https://www.google.co.in/";
    public static String LOGTAG = "ConnectionService";

    private Timer mTimer = null;

    ConnectionServiceCallback mConnectionServiceCallback;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public interface ConnectionServiceCallback {
        void hasInternetConnection();
        void hasNoInternetConnection();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(LOGTAG,"on start");

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new CheckForConnection(), 0, TAG_INTERVAL);

        return super.onStartCommand(intent, flags, startId);
    }

    class CheckForConnection extends TimerTask {
        @Override
        public void run() {
            //Log.v(LOGTAG, "net status from service " + isNetworkAvailable());
        }
    }

    @Override
    public void onDestroy() {
        mTimer.cancel();
        super.onDestroy();
    }

    private boolean isNetworkAvailable(){
        HttpGet httpGet = new HttpGet(TAG_URL_PING);
        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 5000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

        int timeoutSocket = 7000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
        try{
            httpClient.execute(httpGet);
            return true;
        }
        catch(ClientProtocolException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return false;
    }

}
