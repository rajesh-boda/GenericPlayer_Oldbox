package com.ideabytes.qezytv.genericplayer.ethernet;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Selection;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ideabytes.qezytv.genericplayer.R;
import com.ideabytes.qezytv.genericplayer.constants.DeviceConstants;
import com.ideabytes.qezytv.genericplayer.database.Database_for_Inactive;
import com.ideabytes.qezytv.genericplayer.database.Inactive_status;
import com.ideabytes.qezytv.genericplayer.utils.DeviceInfo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by suman on 4/1/16.
 */
public class Ethernet extends Activity implements DeviceConstants {
    private static final String LOGTAG = "Ethernet";
    private TextView tvReason;
    private EditText etIpAddress;
    private EditText etMask;
    private EditText etGateway;
    private EditText etDns;
    private RadioGroup radioGroup;
    private final int DHCP = 0;
    private final int STATIC = 1;
    private String ip;
    private int dns;
    private int gateway;
    private String subnetMask;
    private Timer timer;
    private DeviceInfo deviceInfo;
    private String db_value2;
    private Database_for_Inactive db;
    public SharedPreferences pref = null;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        pref = getSharedPreferences("GenericPlayerEthernet", Context.MODE_PRIVATE);

        setContentView(R.layout.ethernet2);
        WifiManager mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int wifiState = mWifiManager.getWifiState();
        deviceInfo = new DeviceInfo(this);

        db = new Database_for_Inactive(getApplicationContext());

//        logoStatus(0);

        int count = db.getContactsCount();
        if(count > 0){

        }
        else{
            db.addStatus(new Inactive_status("static","0"));
        }

//           if(db.getContactsCount()>0) {
//        Inactive_status inactive_status = db.getStatus(1);
//        String DorS = inactive_status.getName();
//               if(DorS.equals("static")){
//            Log.v(LOGTAG,"STATE STATIC");
//        }else if(DorS.equals("dhcp")){
//            Log.v(LOGTAG,"STATE DHCP");
//        }else{
//                   Log.v(LOGTAG,"STATE STATIC");
//               }
//         }

//        String DHCPorSTATIC = deviceInfo.getDHCPorSTATIC();
//        Log.v(LOGTAG,"STATE getprop " + DHCPorSTATIC);
//        if(DHCPorSTATIC.equals("static")){
//            Log.v(LOGTAG,"STATE STATIC");
//        }else{
//            Log.v(LOGTAG,"STATE DHCP");
//        }
        // Log.v(LOGTAG,"state "+wifiState);
        //wifi state 3 means connected, 1 means not connected,when wifi connected disable connection
        if (wifiState == 3) {
            mWifiManager.setWifiEnabled(false);
        }


        etIpAddress = (EditText) findViewById(R.id.etIp);
        etMask = (EditText) findViewById(R.id.etMask);
        etGateway = (EditText) findViewById(R.id.etGateway);
        etDns = (EditText) findViewById(R.id.etDns);


        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        Log.v(LOGTAG,"line-->121" + " "+ db.getContactsCount());
        if(db.getContactsCount()>0) {
            Inactive_status inactive_status = db.getStatus(1);
            String DorS = inactive_status.getName();
            db_value2 = inactive_status.getInactiveStatus();
            if(DorS.equals("static")){
                Log.v(LOGTAG,"auto STATE STATIC" + " "+ DorS);
                radioGroup.check(R.id.rStatic);
            }else if(DorS.equals("dhcp")){
                radioGroup.check(R.id.rDhcp);
                Log.v(LOGTAG,"STATE DHCP" + " "+ DorS);
            }else{
                Log.v(LOGTAG,"STATE STATIC" + " "+ DorS);
                radioGroup.check(R.id.rStatic);
                db.updateContact(new Inactive_status(1, "static", db_value2));

            }
        }
        //radioGroup.check(R.id.rStatic);
        if(radioGroup.getCheckedRadioButtonId() == R.id.rDhcp) {
            setItems(DHCP);
        }else{
            setItems(STATIC);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if(checkedId == R.id.rDhcp) {
                    Log.v(LOGTAG, "btn DHCP");
                    setDHCP();
                    setItems(DHCP);
                } else {
                    Log.v(LOGTAG, "btn Static");
                    setItems(STATIC);
                }
            }

        });

        final Button back=(Button) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Ethernet.this, com.ideabytes.qezytv.genericplayer.Settings.class);
                intent.putExtra(MESSAGE, USER_STOPPED_PLAY);
                startActivity(intent);
                // finish();
            }
        });

        final Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v(LOGTAG, "connectToEtherNet()::DHCP123");
                connectToEtherNet();
            }
        });
        final Button btnReset = (Button) findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when static only reset all feolds to empty, if dhcp then anyway fields will fill dynamically
                if (radioGroup.getCheckedRadioButtonId() == R.id.rStatic) {
                    etIpAddress.setText("");
                    etMask.setText("");
                    etDns.setText("");
                    etGateway.setText("");
                }
            }
        });
    }
    private boolean connectToEtherNet() {

        try {


            if(radioGroup.getCheckedRadioButtonId() == R.id.rDhcp)
            {
                Log.v(LOGTAG, "connectToEtherNet()::DHCP");
                db.updateContact(new Inactive_status(1, "dhcp", db_value2));
                //Runtime.getRuntime().exec("ifconfig eth0 dhcp start");
                ///////////////////////////////////////////////////////////////////////////////////////////
                SharedPreferences.Editor ed = pref.edit();
                ed.putString("IpAddress",  etIpAddress.getText().toString().trim());
                ed.putString("netmask",  etMask.getText().toString().trim());
                ed.putString("Gateway",  etGateway.getText().toString().trim());
                ed.putString("DNS",  etDns.getText().toString().trim());
                ed.commit();




                ///////////////////////////////////////////////////////////////////////////////////////////
                String line;
                Process p = Runtime.getRuntime().exec("getprop");

                BufferedReader input = new BufferedReader (new InputStreamReader(p.getInputStream()));

                while ((line = input.readLine()) != null) {
                    Log.v(LOGTAG,"getprop: "+line);
//                    if(line.contains("eth0")){
//                        if(line.contains("UP")){
//                            r=true;
//                        }
//                        else{
//                            turnEthOn();
//                            r=false;
//                        }
//                    }
                }
                input.close();
            }
            else{
                db.updateContact(new Inactive_status(1, "static", db_value2));
                Log.v(LOGTAG, "connectToEtherNet()::Static IP2"+etIpAddress.getText().toString().trim() );
                //changeIp();


                //   Log.v(LOGTAG, "connectToEtherNet()::Static IP"+etIpAddress.getText().toString().trim() );
                String line1;
                String[] mycommand = {"ifconfig eth0" + " " +  etIpAddress.getText().toString().trim() + " " + "netmask" + " " + etMask.getText().toString().trim() + " ", "netcfg eth0 up",
                        "route add default gw" + " " + etGateway.getText().toString().trim() + " " + "dev eth0"};

                SharedPreferences.Editor ed = pref.edit();
                ed.putString("IpAddress",  etIpAddress.getText().toString().trim());
                ed.putString("netmask",  etMask.getText().toString().trim());
                ed.putString("Gateway",  etGateway.getText().toString().trim());
                ed.putString("DNS",  etDns.getText().toString().trim());
                ed.commit();

                try {

                    //    Process pp = Runtime.getRuntime().exec(mycommand);
                    java.lang.Process process = Runtime.getRuntime().exec("su");
                    DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());
                    for (String tmpcmd : mycommand) {
                        outputStream.writeBytes(tmpcmd + "\n");
                    }
                    outputStream.writeBytes("exit\n");
                    outputStream.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.v(LOGTAG, "ifconfig eth0 " + mycommand.toString());
                String str123 = etDns.getText().toString().trim();
                Log.v(LOGTAG, "setprop dns1 " + str123);
//                Runtime.getRuntime().exec("setprop net.eth0.dns1 "+ str123);
                Runtime.getRuntime().exec("setprop net.eth0.dns1 8.8.8.8");
                Runtime.getRuntime().exec("setprop net.eth0.dns2 4.4.4.4");


                String line;
                Process p = Runtime.getRuntime().exec("getprop");

                BufferedReader input = new BufferedReader (new InputStreamReader(p.getInputStream()));

                while ((line = input.readLine()) != null) {
                    Log.v(LOGTAG,"getprop: "+line);
//                    if(line.contains("eth0")){
//                        if(line.contains("UP")){
//                            r=true;
//                        }
//                        else{
//                            turnEthOn();
//                            r=false;
//                        }
//                    }
                }
                input.close();

            }
            if (isEthOn()) {
               // Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
                //Intent intent = new Intent(this, VideoActivity.class);
                //startActivity(intent);
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.ib.viplov.gp_installer");
                if (launchIntent != null) {
                    launchIntent.putExtra("message", "restart");

                    startActivity(launchIntent);//null pointer check in case package name was not found
                    //VideoActivity.getActivity().finish();
                    //context.stopService(new Intent(context.getApplicationContext(), WatchDogToUpdateStatus.class));
                }
                Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_LONG).show();
                 /*    commented below by rajesh 27June18
                String nameOfProcess = "com.ideabytes.qezytv.genericplayer";
                ActivityManager  manager = (ActivityManager)Ethernet.this.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> listOfProcesses = manager.getRunningAppProcesses();
                Log.e("Proccess" , "Exit app-----123");


                for (ActivityManager.RunningAppProcessInfo process : listOfProcesses)
                {
                    if (process.processName.contains(nameOfProcess))
                    {
                        Log.e("Proccess" , process.processName + " : " + process.pid);
                        android.os.Process.killProcess(process.pid);
                        android.os.Process.sendSignal(process.pid, android.os.Process.SIGNAL_KILL);
                        manager.killBackgroundProcesses(process.processName);
                        break;
                    }
                }
                Log.e("Proccess" , "Exit app-----");
                Toast.makeText(getApplicationContext(),"exit",Toast.LENGTH_LONG).show();
               finish();//---need to comment by rajesh 26/06/18
                */

            } else {
                Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_LONG).show();
            }


        } catch (IOException e) {
            Log.e(LOGTAG, "Runtime Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        return true;
    }
    public boolean isEthOn() {

        try {

            String line;
            boolean r = false;

            Process p = Runtime.getRuntime().exec("netcfg");

            BufferedReader input = new BufferedReader (new InputStreamReader(p.getInputStream()));

            while ((line = input.readLine()) != null) {
                Log.v(LOGTAG,"line: "+line);
                if(line.contains("eth0")){
                    if(line.contains("UP")){
                        r=true;
                    }
                    else{
                        turnEthOn();
                        r=false;
                    }
                }
            }
            input.close();
            Log.e(LOGTAG, "isEthOn: " + r);
            return r;

        } catch (IOException e) {
            Log.e(LOGTAG,"Runtime Error: "+e.getMessage());
            e.printStackTrace();
            return false;
        }

    }
    public void turnEthOn() {

        try {
            Runtime.getRuntime().exec("ifconfig eth0 up");
        } catch (IOException e) {
            Log.e(LOGTAG,"turnEthOn()::Runtime Error: "+e.getMessage());
            e.printStackTrace();
        }
    }

    public void setDHCP(){
//        String[] mycommand = {"ifconfig eth0 dhcp start" + " ", "netcfg eth0 up"};

        //String[] mycommand = {"ifconfig eth0 dhcp start"};
        String[] mycommand = {"netcfg eth0 dhcp"};



        try {
            java.lang.Process process = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());
            for (String tmpcmd : mycommand) {
                outputStream.writeBytes(tmpcmd + "\n");
            }
            outputStream.writeBytes("exit\n");
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (!deviceInfo.getIpAddress().equals(etIpAddress.getText().toString())){
                            Log.e(LOGTAG, "timer running " + deviceInfo.getIpAddress());

                            etIpAddress.setEnabled(false);
                            etIpAddress.setText(deviceInfo.getIpAddress());
                            // etIpAddress.setText(deviceInfo.getIpAddress());
                            etMask.setEnabled(false);
                            etMask.setText(deviceInfo.getDHCPmask());
                            etGateway.setEnabled(false);
                            etGateway.setText(deviceInfo.getDHCPGateway());
                            etDns.setEnabled(false);
                            etDns.setText(deviceInfo.getDNS1());
                        }else{
                            Log.e(LOGTAG, "timer stopped " );
                            timer.cancel();
                            timer.purge();
                        }
                    }
                });

            }
        };
        // here we can set the timer to reset watchdog time
        timer.schedule(timerTask, 2 * 1000, 1 * 1000);
//                    Log.e(LOGTAG,"timer running " + deviceInfo.getIpAddress());
//                   etIpAddress.setEnabled(false);
//                    etIpAddress.setText(deviceInfo.getIpAddress());
//                    // etIpAddress.setText(deviceInfo.getIpAddress());
//                    etMask.setEnabled(false);
//                    etMask.setText(deviceInfo.getSubnetMask());
//                    etGateway.setEnabled(false);
//                    etGateway.setText(deviceInfo.getGateway());
//                    etDns.setEnabled(false);
//                    etDns.setText(deviceInfo.getDNS1());




//        etIpAddress.setEnabled(false);
//        etIpAddress.setText(deviceInfo.getIpAddress());
//        // etIpAddress.setText(deviceInfo.getIpAddress());
//        etMask.setEnabled(false);
//        etMask.setText(deviceInfo.getSubnetMask());
//        etGateway.setEnabled(false);
//        etGateway.setText(deviceInfo.getGateway());
//        etDns.setEnabled(false);
//        etDns.setText(deviceInfo.getDNS1());

    }
    private static String intToIP(int ipAddress) {
        String ret = String.format("%d.%d.%d.%d", (ipAddress & 0xff),
                (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
                (ipAddress >> 24 & 0xff));

        return ret;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setItems(final int type) {
        WifiManager mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
        WifiInfo wifiInf = mWifiManager.getConnectionInfo();
        final int dns1 = dhcpInfo.dns1;
        Log.v(LOGTAG,"dns1 "+intToIP(dns1));
        final int gateway = dhcpInfo.gateway;
        Log.v(LOGTAG, "gateway " + intToIP(gateway));
        final int ipAddress = wifiInf.getIpAddress();
        final String ip = intToIP(ipAddress);
        Log.v(LOGTAG,"ip "+ip);
        String local= null;
        try {
            local = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String[] ip_component = local.split("\\.");
        final String subnet=ip_component[0]+"."+ip_component[1]+"."+ip_component[2]+"."+ip_component[3];
        Log.v(LOGTAG, "subnet mask " + subnet);

        //set values to variables
        setIpDetails(ip,subnet,gateway,dns1);
        switch (type) {
            case DHCP :
                /*
                Disabled by Rajesh 080618
                etIpAddress.setEnabled(false);
                // etIpAddress.setText("IP Address :" + deviceInfo.getIpAddress());
                etIpAddress.setText(deviceInfo.getIpAddress());
                etMask.setEnabled(false);
                //  etMask.setText("Subnet Mask :"+deviceInfo.getSubnetMask());
                etMask.setText(deviceInfo.getDHCPmask());
                etGateway.setEnabled(false);
                //  etGateway.setText("Gateway :"+deviceInfo.getGateway());
                etGateway.setText(deviceInfo.getDHCPGateway());
                etDns.setEnabled(false);
                // etDns.setText("DNS1 :"+deviceInfo.getDNS1());
                etDns.setText(deviceInfo.getDNS1());
                */

                etIpAddress.setEnabled(false);
                // etIpAddress.setText("IP Address :" + deviceInfo.getIpAddress());
                String ipAdress=pref.getString("IpAddress","");
                if( !ipAdress.equals(""))
                {
                    etIpAddress.setText(ipAdress);
                }
                else
                    etIpAddress.setText(deviceInfo.getIpAddress());

                String netmask=pref.getString("netmask","");
                etMask.setEnabled(false);
                if( !netmask.equals(""))
                {
                    etMask.setText(netmask);
                }
                else
                    etMask.setText(deviceInfo.getSubnetMask());

                String gateWay=pref.getString("Gateway","");
                etGateway.setEnabled(false);
                if( !gateWay.equals(""))
                {
                    etGateway.setText(gateWay);
                }
                else
                    etGateway.setText(deviceInfo.getGateway());
                String DNS=pref.getString("DNS","");
                etDns.setEnabled(false);
                // etDns.setText("DNS1 :"+deviceInfo.getDNS1());

                if( !DNS.equals(""))
                {
                    etDns.setText(DNS);
                }
                else
                    etDns.setText(deviceInfo.getDNS1());

                break;
            case STATIC :

              /*   Disabled by Rajesh 080618
                //hard coded, have to remove
                etIpAddress.setEnabled(true);
               // etIpAddress.setText("IP Address :" + deviceInfo.getIpAddress());
                etIpAddress.setText(deviceInfo.getIpAddress());
                etMask.setEnabled(true);
              //  etMask.setText("Subnet Mask :"+deviceInfo.getSubnetMask());
                etMask.setText(deviceInfo.getSubnetMask());
                etGateway.setEnabled(true);
              //  etGateway.setText("Gateway :"+deviceInfo.getGateway());
                etGateway.setText(deviceInfo.getGateway());
                etDns.setEnabled(true);
               // etDns.setText("DNS1 :"+deviceInfo.getDNS1());
                etDns.setText(deviceInfo.getDNS1());

              */

                etIpAddress.setEnabled(true);
                // etIpAddress.setText("IP Address :" + deviceInfo.getIpAddress());
                ipAdress=pref.getString("IpAddress","");
                if( !ipAdress.equals(""))
                {
                    etIpAddress.setText(ipAdress);
                    Selection.setSelection(etIpAddress.getText(), etIpAddress.getText().length());
                }
                else {
                    etIpAddress.setText(deviceInfo.getIpAddress());
                    Selection.setSelection(etIpAddress.getText(), etIpAddress.getText().length());
                }

                netmask=pref.getString("netmask","");
                etMask.setEnabled(true);
                if( !netmask.equals(""))
                {
                    etMask.setText(netmask);
                    Selection.setSelection(etMask.getText(), etMask.getText().length());
                }
                else {
                    etMask.setText(deviceInfo.getSubnetMask());
                    Selection.setSelection(etMask.getText(), etMask.getText().length());
                }

                gateWay=pref.getString("Gateway","");
                etGateway.setEnabled(true);
                if( !gateWay.equals(""))
                {
                    etGateway.setText(gateWay);
                    Selection.setSelection(etGateway.getText(), etGateway.getText().length());
                }
                else {
                    etGateway.setText(deviceInfo.getGateway());
                    Selection.setSelection(etGateway.getText(), etGateway.getText().length());
                }
                DNS=pref.getString("DNS","");
                etDns.setEnabled(true);
                // etDns.setText("DNS1 :"+deviceInfo.getDNS1());

                if( !DNS.equals(""))
                {
                    etDns.setText(DNS);
                    Selection.setSelection(etDns.getText(), etDns.getText().length());
                }
                else {
                    etDns.setText(deviceInfo.getDNS1());
                    Selection.setSelection(etDns.getText(), etDns.getText().length());
                }

                break;
        }
    }



    private void setIpDetails(final String ip,final String subnetMask,final int gateway,final int dns) {
        this.ip = ip;
        this.subnetMask = subnetMask;
        this.gateway = gateway;
        this.dns = dns;
    }
    public  static void changeIp()
    {
        String str1="192.168.1.21";

        String str2="255.255.255.0";
        String[] command1 = { "netsh", "interface", "ip", "set", "address",
                "name=", "Local Area Connection" ,"source=static", "addr=",str1,
                "mask=", str2};
        try {


            Process pp = Runtime.getRuntime().exec(command1);
            Log.v(LOGTAG, "connectToEtherNet()::Static IP"+"changed ip" );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {

        try{

            Intent intent = new Intent(Ethernet.this, com.ideabytes.qezytv.genericplayer.Settings.class);
            intent.putExtra(MESSAGE, USER_STOPPED_PLAY);
            startActivity(intent);
            finish();;

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
