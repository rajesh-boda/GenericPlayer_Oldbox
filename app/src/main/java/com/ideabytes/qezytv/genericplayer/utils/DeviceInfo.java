package com.ideabytes.qezytv.genericplayer.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.format.Formatter;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.network.PingIP;
import com.ideabytes.qezytv.genericplayer.services.AppLocationService;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 * Name : DeviceInfo
 * author:  Suman
 * Created Date : 18-12-2015
 * Description : This activity is to return device info and channel info from database
 * Modified Date : 11-03-2016
 * Reason:
 *************************************************************/
public class DeviceInfo implements DeviceConstants {
    private static final String LOGTAG = "DeviceInfo";
    private Context context;

    public DeviceInfo(Context context) {
        this.context = context;
    }

    /**
     * Method to get device information which is needed to send status of the player to server dashboard
     * @param deviceId
     * @param licenseKey
     * @return device info as json object
     */
    public JSONObject getDeviceInfo(final String deviceId, final String licenseKey) {
        JSONObject deviceInfo = new JSONObject();
        try {
            deviceInfo.put(DATE_TIME, getDateTime());
            deviceInfo.put(DEVICE_MODEL, Build.MODEL);
            deviceInfo.put(OS_TYPE, "Android");
            deviceInfo.put(CLIENT_VERSION, Build.VERSION.RELEASE);
            deviceInfo.put(LICENSE_KEY, licenseKey);
            deviceInfo.put(DEVICE, deviceId);
            deviceInfo.put(GEO_LANGITUDE, "14.123");
            deviceInfo.put(GEO_LATITUDE, "72.143");
            deviceInfo.put(IP_ADDRESS, getIpAddress());
            deviceInfo.put(LOCATION, "Hyderabad");
            deviceInfo.put(GEO_ADDRESS, "Hyd");
            deviceInfo.put(COUNTRY, "India");//TODO
            deviceInfo.put(CITY, "Hyderabad");
        } catch (Exception e) {
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer(context);
            sendExceptionsToServer.sendMail(DeviceInfo.this.getClass().getName(), Utils.convertExceptionToString(e), "Exception");
        }
        return deviceInfo;
    }

    private String getDateTime() {
        String presentDate = "";
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            presentDate = sdf.format(c.getTime());
        } catch (Exception e1) {
            SendExceptionsToServer sendExceptionsToServer = new SendExceptionsToServer();
            sendExceptionsToServer.sendMail(LOGTAG, Utils.convertExceptionToString(e1), "Exception");
        }
        return presentDate;
    }
    /**
     * This method is to return ip address of the device based on connection type
     * @return ip address of the device
     */
    public String getIpAddress() {
        String ipAddress = "";
        try {
            boolean netStatus = PingIP.isConnectingToInternet(context);
            if (netStatus) {
                if (getNetworkType() == ConnectivityManager.TYPE_WIFI) {
                    WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                    int ip = wifiInfo.getIpAddress();

                    ipAddress = Formatter.formatIpAddress(ip);
                } else {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();

                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {

                            InetAddress inetAddress = enumIpAddr.nextElement();
                            Log.v("DeviceInfo Inet details",inetAddress.toString());
                            if (!inetAddress.isLoopbackAddress()&& inetAddress instanceof Inet4Address) {
                                ipAddress = inetAddress.getHostAddress().toString();
                                // ipAddress = Formatter.formatIpAddress(inetAddress.hashCode());

                                Log.v("DeviceInfo IP address", "ip " + inetAddress.getHostAddress().toString());
                                }
                          //  InetAddress broadcast = interfaceAddress.getBroadcast();



                        }
                    }




                }
            } else {
                Log.e("IP Address", "Due to no internet ip is 0.0.0.0");
            }
        } catch (Exception ex) {
            Log.e("IP Address", "0.0.0.0");
            ex.printStackTrace();
        }
        //Log.v(LOGTAG, ipAddress+"type "+getNetworkType());
        return ipAddress;
    }

    /**
     * This method is to return subnet mask of the device based on connection type
     * @return ip address of the device
     */
    public String getSubnetMask() {
        String subnet = "";
        try {
            boolean netStatus = PingIP.isConnectingToInternet(context);
            if (netStatus) {
                if (getNetworkType() == ConnectivityManager.TYPE_WIFI) {
                    WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                    int ip = wifiInfo.getIpAddress();

                    subnet = Formatter.formatIpAddress(ip);
                } else {
                    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

                    while (interfaces.hasMoreElements())
                    {
                        NetworkInterface networkInterface = interfaces.nextElement();

                        if (networkInterface.isLoopback())
                            continue; // Don't want to broadcast to the loopback interface

                        for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
                        {
                            InetAddress broadcast = interfaceAddress.getBroadcast();

                            // InetAddress ip = interfaceAddress.getAddress();

                                int i = interfaceAddress.getNetworkPrefixLength(); //is another way to express subnet mask

                                // Android seems smart enough to set to null broadcast to
                                //  the external mobile network. It makes sense since Android
                                //  silently drop UDP broadcasts involving external mobile network.
                                Log.v("DeviceInfo sub net", "what getHostAddress" + i);



//                            if(i == 8){
//                                subnet = "255.0.0.0";
//                            }
//                            else if(i == 16){
//                                subnet = "255.255.0.0";
//                            }
//                            else if(i == 24){
//                                subnet = "255.255.255.0";
//                            }
//                            else if(i == 64){
//                                subnet = "255.255.255.0";
//                            }
//                            else{
//                                subnet = "0.0.0.0";
//                            }

                          int  netmaskNumeric = 0xffffffff;
                            netmaskNumeric = netmaskNumeric << (32 - i);

                            subnet = getNetmask(netmaskNumeric);

                            if (broadcast == null)
                                continue;


                        }
                    }


                }
            } else {
                Log.e("subnet", "Due to no internet subnet is 0.0.0.0");
            }
        } catch (Exception ex) {
            Log.e("subnet", "0.0.0.0");
            ex.printStackTrace();
        }
        //Log.v(LOGTAG, ipAddress+"type "+getNetworkType());
        return subnet;
    }

    public String getNetmask(int netmaskNumeric) {
        StringBuffer sb = new StringBuffer(15);

        for (int shift = 24; shift > 0; shift -= 8) {

            // process 3 bytes, from high order byte down.
            sb.append(Integer.toString((netmaskNumeric >>> shift) & 0xff));

            sb.append('.');
        }
        sb.append(Integer.toString(netmaskNumeric & 0xff));

        return sb.toString();
    }



    /**
     * This method is to return Subnet mask from getpropof the device based on connection type
     * @return ip address of the device
     */
    public String getSubnetMask1() {
        try {

            String line;
            String r = null;

            Process p = Runtime.getRuntime().exec("getprop");

            BufferedReader input = new BufferedReader (new InputStreamReader(p.getInputStream()));

            while ((line = input.readLine()) != null) {
                Log.v(LOGTAG,"subnet mask line: "+line);
                r = line;


            }
            input.close();
            return r;


        } catch (IOException e) {
            Log.e(LOGTAG,"Runtime Error: "+e.getMessage());
            e.printStackTrace();
            return "0.0.0.0";
        }

    }

    /**
     * This method is to return DNS1 of the device based on connection type
     * @return ip address of the device
     */
    public String getDNS1() {
        try {

            String line;
            String r = null;

            Process p = Runtime.getRuntime().exec("getprop net.dns1");

            BufferedReader input = new BufferedReader (new InputStreamReader(p.getInputStream()));

            while ((line = input.readLine()) != null) {
                Log.v(LOGTAG,"DNS1 line: "+line);
                r = line;


            }
            input.close();
            return r;


        } catch (IOException e) {
            Log.e(LOGTAG,"Runtime Error: "+e.getMessage());
            e.printStackTrace();
            return "0.0.0.0";
        }

    }

    /**
     * This method is to return Gateway of the device based on connection type
     * @return ip address of the device
     */
    public String getGateway() {
        try {

            String line;
            String r = null;

            Process p = Runtime.getRuntime().exec("getprop ubootenv.var.gatewayip");

            BufferedReader input = new BufferedReader (new InputStreamReader(p.getInputStream()));

            while ((line = input.readLine()) != null) {
                Log.v(LOGTAG,"gateway line: "+line);
                r = line;


            }
            input.close();
            return r;


        } catch (IOException e) {
            Log.e(LOGTAG,"Runtime Error: "+e.getMessage());
            e.printStackTrace();
            return "0.0.0.0";
        }

    }

    /**
     * This method is to return DHCP Gateway of the device based on connection type
     * @return ip address of the device
     */
    public String getDHCPGateway() {
        try {

            String line;
            String r = null;

            Process p = Runtime.getRuntime().exec("getprop dhcp.eth0.gateway");

            BufferedReader input = new BufferedReader (new InputStreamReader(p.getInputStream()));

            while ((line = input.readLine()) != null) {
                Log.v(LOGTAG,"gateway line: "+line);
                r = line;


            }
            input.close();
            return r;


        } catch (IOException e) {
            Log.e(LOGTAG,"Runtime Error: "+e.getMessage());
            e.printStackTrace();
            return "0.0.0.0";
        }

    }

    /**
     * This method is to return Gateway of the device based on connection type
     * @return ip address of the device
     */
    public String getDHCPmask() {
        try {

            String line;
            String r = null;

            Process p = Runtime.getRuntime().exec("getprop dhcp.eth0.mask");

            BufferedReader input = new BufferedReader (new InputStreamReader(p.getInputStream()));

            while ((line = input.readLine()) != null) {
                Log.v(LOGTAG,"subnet mask line: "+line);
                r = line;


            }
            input.close();
            return r;


        } catch (IOException e) {
            Log.e(LOGTAG,"Runtime Error: "+e.getMessage());
            e.printStackTrace();
            return "0.0.0.0";
        }

    }

    /**
     * This method is to return Gateway of the device based on connection type
     * @return ip address of the device
     */
    public String getDHCPorSTATIC() {
        try {

            String line;
            String r = null;

            Process p = Runtime.getRuntime().exec("getprop");

            BufferedReader input = new BufferedReader (new InputStreamReader(p.getInputStream()));

            while ((line = input.readLine()) != null) {
                Log.v(LOGTAG,"DHCPorSTATIC: "+line);
                r = line;


            }
            input.close();
            return r;


        } catch (IOException e) {
            Log.e(LOGTAG,"Runtime Error: "+e.getMessage());
            e.printStackTrace();
            return "0.0.0.0";
        }

    }

    /**
     * This method is to return network connection type
     * @return network connection type
     */
    private int getNetworkType() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo.getType();
    }
    private String getAddress() {
        String result = null;
        AppLocationService appLocationService = new AppLocationService(
                context);
        Location gpsLocation = appLocationService
                .getLocation(LocationManager.NETWORK_PROVIDER);
        if (gpsLocation != null) {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocation(
                        gpsLocation.getLatitude(), gpsLocation.getLongitude(), 1);
                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i)).append("\n");
                    }
                        sb.append(address.getLocality()).append("\n");
                        sb.append(address.getPostalCode()).append("\n");
                        sb.append(address.getCountryName());
                    result = sb.toString();
                }
            } catch (IOException e) {
                Log.e(LOGTAG, "Unable connect to Geocoder", e);
            }
        }
        Log.v(LOGTAG,"address "+result);
        return result;
    }

    /**
     * This method is to get city name using latitude and longitude of the location
     * @return city name
     */
    public String getCityName() {
        String result = "City";
        AppLocationService appLocationService = new AppLocationService(
                context);
        Location gpsLocation = appLocationService
                .getLocation(LocationManager.NETWORK_PROVIDER);
        if (gpsLocation != null) {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocation(
                        gpsLocation.getLatitude(), gpsLocation.getLongitude(), 1);
                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    result =address.getLocality();
                }
            } catch (IOException e) {
                Log.e(LOGTAG, "Unable connect to Geocoder", e);
            }
        }
      //  Log.v(LOGTAG,"city name "+result);
        return result;
    }
//    private String getLocation() {
//        String locality = "IB";
//        AppLocationService appLocationService = new AppLocationService(
//                context);
//        Location gpsLocation = appLocationService
//                .getLocation(LocationManager.GPS_PROVIDER);
//        if (gpsLocation != null) {
//            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
//            try {
//                List<Address> addressList = geocoder.getFromLocation(
//                        gpsLocation.getLatitude(), gpsLocation.getLongitude(), 1);
//                if (addressList != null && addressList.size() > 0) {
//                    Address address = addressList.get(0);
//                    StringBuilder sb = new StringBuilder();
//                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
//                        sb.append(address.getAddressLine(i)).append("\n");
//                    }
//                    sb.append(address.getLocality());
////                        sb.append(address.getPostalCode()).append("\n");
////                        sb.append(address.getCountryName());
//                    locality = sb.toString();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return locality;
//        }
//        Log.v(LOGTAG,"locality "+locality);
//        return locality;
//    }

    /**
     * This method is to get  latitude of the location
     * @return latitude
     */
    public double getLatitude() {
        AppLocationService appLocationService = new AppLocationService(
                context);
        Location gpsLocation = appLocationService
                .getLocation(LocationManager.NETWORK_PROVIDER);
       // Log.v(LOGTAG, "latitude " + gpsLocation.getLatitude());
        return gpsLocation.getLatitude();
    }

    /**
     * This method is to get  longitude of the location
     * @return longitude
     */
    public double getLongitude() {
        AppLocationService appLocationService = new AppLocationService(
                context);
        Location gpsLocation = appLocationService
                .getLocation(LocationManager.NETWORK_PROVIDER);
       // Log.v(LOGTAG, "longitude " + gpsLocation.getLongitude());
        return gpsLocation.getLongitude();
    }
}
