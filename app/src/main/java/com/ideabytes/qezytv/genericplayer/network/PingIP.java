package com.ideabytes.qezytv.genericplayer.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import java.io.IOException;
import java.net.InetAddress;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : PingIP
 * author:  Suman
 * Created Date : 22-04-2016
 * Description : This class is to check internet connectivity
 * Modified Date : 25-04-2016
 * Reason: checking provider first then internet
 *************************************************************/
public class PingIP {
    private final static String TAG = "PingIP";
    /**
     * Checking for all possible internet providers
     * **/
    public static boolean isConnectingToInternet(final Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    /**
     * This method is to check internet
     * @param context
     * @return net connectivity status
     */
   // public static boolean pingForInternet(final Context context){
//        //System.out.println("pingForInternet");
//        boolean reachable = false;
//        if (isConnectingToInternet(context)) {
//            try{
//                InetAddress address = InetAddress.getByName("https://www.google.co.in");
//                reachable = address.isReachable(5*1000);
//                System.out.println("Is host reachable? " + reachable);
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//        } else {
//            return reachable;
//        }
//        return reachable;
//    }
}
