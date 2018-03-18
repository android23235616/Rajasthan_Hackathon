package com.example.pranshooverma.rajasthan_hackathon;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WiFiCheckService extends Service {
    private WifiManager wifimanager;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private Timer timer = new Timer();
    public static final String SSOS = "12345";



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
        mGoogleApiClient = QR_SCANNING.mGoogleApiClient;
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
/////////////////////////////////////////////To Do: WiFi Disconnected, update data////////////////////////////////////////////////
                    Toast.makeText(WiFiCheckService.this, "WiFi Disconnected", Toast.LENGTH_SHORT).show();
                    displayLocation();


                }

                if(flag == 0 && prev == 0){
                    Toast.makeText(WiFiCheckService.this, "WiFi Not Found", Toast.LENGTH_SHORT).show();
                }
            }
        }};

    private void updateData(String[] res) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String URL = constants.url_stop_trip+"?chesis="+constants.CHASSIS+"&SSOS="+SSOS+"&lat="+res[0]+"&lng="+res[1];

        Toast.makeText(this, "Requesting URL: "+URL, Toast.LENGTH_LONG).show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    Toast.makeText(WiFiCheckService.this, response, Toast.LENGTH_SHORT).show();
                    flag = 0;
                    prev = 0;

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(WiFiCheckService.this, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);


    }


    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            String [] r=new String[2];
            //return r;
        }

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if(mLastLocation!=null)
        {
            String[] ret=new String[2];
            ret[0]= String.valueOf(mLastLocation.getLatitude());
            ret[1]= String.valueOf(mLastLocation.getLongitude());

            updateData(ret);
            //return ret;
            //lat long
        }

        else
        {

            Toast.makeText(this, "Couldnt Fetch Location, Retrying again", Toast.LENGTH_SHORT).show();
            //return null;
        }


    }

}
