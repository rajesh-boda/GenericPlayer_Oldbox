package com.ideabytes.qezytv.genericplayer.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.ideabytes.qezytv.genericplayer.utils.ServiceManager;

public class ConnectivitySpeed {
    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public static boolean isConnected(Context context){
        NetworkInfo info = ConnectivitySpeed.getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    public static boolean isConnectedWifi(Context context){
        NetworkInfo info = ConnectivitySpeed.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public static boolean isConnectedMobile(Context context){
        NetworkInfo info = ConnectivitySpeed.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    // Definition of Very fast > 2Mbps
    public static boolean isConnectedVeryFast(Context context){
        NetworkInfo info = ConnectivitySpeed.getNetworkInfo(context);
        return (info != null && info.isConnected() && 
        		ConnectivitySpeed.isConnectionVeryFast(info.getType(),info.getSubtype()));
    }

    // Definition of fast > 1Mbps
    public static boolean isConnectedFast(Context context){
        NetworkInfo info = ConnectivitySpeed.getNetworkInfo(context);
        return (info != null && info.isConnected() && 
        		ConnectivitySpeed.isConnectionFast(info.getType(), info.getSubtype()));
    }

    // Definition of medium speed > 500kbps
    public static boolean isConnectedMediumSpeed(Context context){
        NetworkInfo info = ConnectivitySpeed.getNetworkInfo(context);
        return (info != null && info.isConnected() && 
        		ConnectivitySpeed.isConnectionMediumSpeed(info.getType(), info.getSubtype()));
    }
    
    public static boolean isConnectedSlow(Context context){
        NetworkInfo info = ConnectivitySpeed.getNetworkInfo(context);
        return (info != null && info.isConnected() && 
        		ConnectivitySpeed.isConnectionSlow(info.getType(), info.getSubtype()));
    }
    
    public static boolean isConnectedVerySlow(Context context){
        NetworkInfo info = ConnectivitySpeed.getNetworkInfo(context);
        return (info != null && info.isConnected() && 
        		ConnectivitySpeed.isConnectionVerySlow(info.getType(), info.getSubtype()));
    }

    
    public static String getNetworkTypeAsString(Context context) {
        NetworkInfo info = ConnectivitySpeed.getNetworkInfo(context);
        if (!info.isConnected()) {
        	return "NOT CONNECTED";
        }
        return info.getTypeName();
    }
    
    public static String getNetworkSubTypeAsString(Context context) {
        NetworkInfo info = ConnectivitySpeed.getNetworkInfo(context);
        if (!info.isConnected()) {
        	return "NOT CONNECTED";
        }
        return info.getSubtypeName();
    }
    
    
    // Definition of Very fast > 2Mbps
    private static boolean isConnectionVeryFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch(subType){
            case TelephonyManager.NETWORK_TYPE_EVDO_B:  // ~ 5 Mbps   // API level 9
            case TelephonyManager.NETWORK_TYPE_LTE:     // ~ 10+ Mbps // API level 11
            case TelephonyManager.NETWORK_TYPE_HSPAP:   // ~ 10-20 Mbps // API level 13
            case TelephonyManager.NETWORK_TYPE_HSDPA:   // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:   // ~ 1-23 Mbps
                return true; 
                
            case TelephonyManager.NETWORK_TYPE_1xRTT:  // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:   // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:   // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0: // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A: // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:   // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSPA:   // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_UMTS:   // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:  // ~ 1-2 Mbps  // API level 11 
            case TelephonyManager.NETWORK_TYPE_IDEN:   // ~25 kbps    // API level 8
                return false;  
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            default:
            	return false;
            }
        }
        else {
            return false;
        }
    }

    // Definition of Fast > 1Mbps
    private static boolean isConnectionFast(int type, int subType){
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch(subType){
            case TelephonyManager.NETWORK_TYPE_EVDO_B:  // ~ 5 Mbps   // API level 9
            case TelephonyManager.NETWORK_TYPE_LTE:     // ~ 10+ Mbps // API level 11
            case TelephonyManager.NETWORK_TYPE_HSPAP:   // ~ 10-20 Mbps // API level 13
            case TelephonyManager.NETWORK_TYPE_HSDPA:   // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:   // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:  // ~ 1-2 Mbps  // API level 11 
            case TelephonyManager.NETWORK_TYPE_UMTS:   // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_HSPA:   // ~ 700-1700 kbps
                return true; 
                
            case TelephonyManager.NETWORK_TYPE_1xRTT:  // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:   // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:   // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0: // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A: // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:   // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_IDEN:   // ~25 kbps    // API level 8
                return false;  
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            default:
            	return false;
            }
        }
        else {
            return false;
        }
    }

    // Definition of Medium Fast > 500kbps
    private static boolean isConnectionMediumSpeed(int type, int subType){
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch(subType){
            case TelephonyManager.NETWORK_TYPE_EVDO_B:  // ~ 5 Mbps   // API level 9
            case TelephonyManager.NETWORK_TYPE_LTE:     // ~ 10+ Mbps // API level 11
            case TelephonyManager.NETWORK_TYPE_HSPAP:   // ~ 10-20 Mbps // API level 13
            case TelephonyManager.NETWORK_TYPE_HSDPA:   // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:   // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:  // ~ 1-2 Mbps  // API level 11 
            case TelephonyManager.NETWORK_TYPE_UMTS:   // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_HSPA:   // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0: // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A: // ~ 600-1400 kbps
                return true; 
                
            case TelephonyManager.NETWORK_TYPE_1xRTT:  // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:   // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:   // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:   // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_IDEN:   // ~25 kbps    // API level 8
                return false;  
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            default:
            	return false;
            }
        }
        else {
            return false;
        }
    }


    // Definition of Slow < 500kbps
    private static boolean isConnectionSlow(int type, int subType){
        return !isConnectionMediumSpeed(type, subType);
    }
    
    
    // Definition of Very Slow < 75kbps
    private static boolean isConnectionVerySlow(int type, int subType){
        if (type == ConnectivityManager.TYPE_WIFI) {
            return false;
        }
        else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch(subType){
            case TelephonyManager.NETWORK_TYPE_EVDO_B:  // ~ 5 Mbps   // API level 9
            case TelephonyManager.NETWORK_TYPE_LTE:     // ~ 10+ Mbps // API level 11
            case TelephonyManager.NETWORK_TYPE_HSPAP:   // ~ 10-20 Mbps // API level 13
            case TelephonyManager.NETWORK_TYPE_HSDPA:   // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:   // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:  // ~ 1-2 Mbps  // API level 11 
            case TelephonyManager.NETWORK_TYPE_UMTS:   // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_HSPA:   // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0: // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A: // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_1xRTT:  // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:   // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:   // ~ 100 kbps
                return false; 
            case TelephonyManager.NETWORK_TYPE_CDMA:   // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_IDEN:   // ~25 kbps    // API level 8
                return true;  
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            default:
            	return false;
            }
        }
        else {
            return false;
        }
    }

    /**
     * This method is to return net connectivity speed
     * Author :Suman
     * @param context
     * @return net speed
     */
    public static String getSpeed(Context context) {
        String netSpeed = "Very Show Internet";
        ServiceManager serviceManager = new ServiceManager(context);
        if (serviceManager.isNetworkAvailable()) {
            if (isConnectedVeryFast(context)) {
                netSpeed = "Very Fast Internet";
            } else if (isConnectedFast(context)) {
                netSpeed = "Fast Internet";
            } else if (isConnectedMediumSpeed(context)) {
                netSpeed = "Medium Speed Internet";
            } else if (isConnectedSlow(context)) {
                netSpeed = "Slow Internet";
            } else if (isConnectedVerySlow(context)) {
                netSpeed = "Very Slow Internet";
            }
        }
        return netSpeed;
    }
}

