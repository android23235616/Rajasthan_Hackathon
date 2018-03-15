package com.example.pranshooverma.rajasthan_hackathon;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);



    }
    private void send_to_QR_activity()
    {
        final Handler handler=new Handler();
        final Runnable r=new Runnable() {
            @Override
            public void run() {
                //Send it to another activity
                Intent send=new Intent(getApplicationContext(),QR_SCANNING.class);
                send.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(send);
            }
        };
        handler.postDelayed(r,4000);
    }

    private boolean check_permission_accepted()
    {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[] {Manifest.permission.CAMERA}, 1011);
            return false;
        }
        else
            return true;
    }

    private boolean internet_connection_available() {
        try {
            final InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;

    }

}
