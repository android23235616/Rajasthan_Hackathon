package com.example.pranshooverma.rajasthan_hackathon;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import java.util.List;

public class WiFiCheckService extends Service {
    private WifiManager wifimanager;

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

        initialize();
    }

    private void initialize() {
        wifimanager=(WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        registerReceiver(mWifiScanner,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifimanager.startScan();

    }
    private final BroadcastReceiver mWifiScanner=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> mScanResults = wifimanager.getScanResults();
                //Toast.makeText(context, mScanResults.get(0).BSSID, Toast.LENGTH_SHORT).show();
                
            }
        }};
}
