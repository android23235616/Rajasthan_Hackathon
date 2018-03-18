package com.example.pranshooverma.rajasthan_hackathon;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WiFiCheckService extends Service {
    private WifiManager wifimanager;

    private Timer timer = new Timer();

    int flag = 0;
    int prev = 0;

    public WiFiCheckService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Toast.makeText(this, "Service onCreate started", Toast.LENGTH_SHORT).show();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                initialize();
            }
        }, 0, 5*1000);
    }

    private void initialize() {
        //Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        wifimanager=(WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        registerReceiver(mWifiScanner,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifimanager.startScan();

    }

    private final BroadcastReceiver mWifiScanner=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) && constants.vehicleMAC != null) {
                List<ScanResult> mScanResults = wifimanager.getScanResults();
                //Toast.makeText(context, mScanResults.get(0).BSSID, Toast.LENGTH_SHORT).show();


                for (int i = 0; i < mScanResults.size(); i++){
                    //Toast.makeText(WiFiCheckService.this, ""+mScanResults.get(i).BSSID, Toast.LENGTH_LONG).show();

                    if(mScanResults.get(i).BSSID.toString().equalsIgnoreCase(constants.vehicleMAC)){

                        Toast.makeText(WiFiCheckService.this, ""+mScanResults.get(i).level+" dB", Toast.LENGTH_LONG).show();
                        prev = 1;
                        flag = 1;
                        break;
                    }
                    else {
                        flag = 0;
                    }
                }

                if(flag == 0 && prev == 1){
                    Toast.makeText(WiFiCheckService.this, "WiFi Disconnected", Toast.LENGTH_SHORT).show();
                }

                if(flag == 0 && prev == 0){
                    Toast.makeText(WiFiCheckService.this, "WiFi Not Found", Toast.LENGTH_SHORT).show();
                }
            }
        }};

}
