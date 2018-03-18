package com.example.pranshooverma.rajasthan_hackathon;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class QR_SCANNING extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Button scan_qr;

    private IntentIntegrator qrscan;

    ProgressDialog progress;


    //for location
    private Location mLastLocation;
    public static GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates = false;
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 5; // 10 meters
    double latitude = 0.0;
    double longitude = 0.0;
    int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;


    String chasis=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qr__scanning);

        initialize();

        //setting onClick Listener of android
        scan_qr.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                qrscan.initiateScan();

            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                progress.dismiss();
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Display(result.getContents());
                    JSONObject obj = new JSONObject(result.getContents());
                    chasis = obj.getString("chasis_number");
                    constants.CHASSIS = chasis;
                    progress.setMessage("Do not minimize the screen. Please Wait");
                    progress.setCancelable(false);
                    progress.show();
                    getCurrentLocation();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } else {

            super.onActivityResult(requestCode, resultCode, data);

        }


    }

    private void getCurrentLocation() {
        //fetching current location when app gets started
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent serviceIntent = new Intent(QR_SCANNING.this, WiFiCheckService.class);
        stopService(serviceIntent);

    }

    private void fetch_vehicle_wifi_mac(final String chasis, String lat, String lon, String SSO) {
        //add volley in this for getting the mac address of the wifi
        StringRequest stringReques = new StringRequest(Request.Method.GET, constants.qr_send_url_to_get_ap + "?chesis=" + chasis+"&ssos=12345"+"&lat="+lat+"&lon="+lon, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //       progress.dismiss();
                Display(response);
                constants.vehicleMAC = response;
                Intent serviceIntent = new Intent(QR_SCANNING.this, WiFiCheckService.class);
                startService(serviceIntent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Display(error.toString());
            }
        });

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(stringReques);
    }

    private void Display(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void initialize() {
        scan_qr = (Button) findViewById(R.id.scan_qr);
        qrscan = new IntentIntegrator(this);
        progress = new ProgressDialog(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        WifiManager wifiManager=(WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(true);
        }
    }

    public class doIn extends AsyncTask<Object, Object, String[]> {

        @Override
        protected String[] doInBackground(Object... voids) {

            String[] s = displayLocation();
            return s;
        }


        @Override
        protected void onPostExecute(String[] s) {
            if (s[0] != null) {
                progress.dismiss();

                if(chasis!=null)
                {
                    fetch_vehicle_wifi_mac(chasis,s[0],s[1],"12345");//rightmost is sso id
                }
                else
                {
                    Toast.makeText(QR_SCANNING.this, "QR IS NOT VALID", Toast.LENGTH_SHORT).show();
                }
                mGoogleApiClient.disconnect();
            } else {
                Toast.makeText(QR_SCANNING.this, "Please Try Again", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private String[] displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
           String [] r=new String[2];
            return r;
        }

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if(mLastLocation!=null)
        {
            String[] ret=new String[2];
            ret[0]= String.valueOf(mLastLocation.getLatitude());
            ret[1]= String.valueOf(mLastLocation.getLongitude());

            return ret;
            //lat long
        }

        else
        {
            Toast.makeText(this, "Couldnt Fetch Location, Retrying again", Toast.LENGTH_SHORT).show();
            return null;
        }


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Toast.makeText(this, "Fetching Location", Toast.LENGTH_SHORT).show();
        doIn a=new doIn();
        a.execute();
    }

    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;
        doIn a=new doIn();
        a.execute();
    }
}
