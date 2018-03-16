package com.example.pranshooverma.rajasthan_hackathon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class checking_wifi_RSSI extends AppCompatActivity {

    private WifiManager wifimanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checking_wifi__rssi);
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
