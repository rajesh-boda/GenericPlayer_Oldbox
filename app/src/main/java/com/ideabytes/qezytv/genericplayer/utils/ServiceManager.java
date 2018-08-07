package com.ideabytes.qezytv.genericplayer.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : ServiceManager
 * author:  Suman
 * Created Date : 01-02-2016
 * Description : This activity is to check net connectivity
 * Modified Date : 29-02-2016
 * Reason: Checking network change and also internet
 *************************************************************/
public class ServiceManager extends ContextWrapper {

    public ServiceManager(Context base) {
        super(base);
    }

    /**
     * This method returns true if connected to working internet connection else returns network status as false
     * @return connectivity status
     */
    public boolean isNetworkAvailable() {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                status = true;
            } else {
                    status = false;
            }
        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }
        return status;
    }

    public boolean checkInterNet() {
        try {
            if (isNetworkAvailable()) {
                // Google.com
                if (InetAddress.getByAddress("183.82.9.60".getBytes()).isReachable(5000) == true) {
                    //Boolean variable named network
                    return true; //Ping works
                } else {
                    return false; //Ping doesnt work
                }
            } else {
                return false;
            }
        } catch (MalformedURLException e1) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }
}
