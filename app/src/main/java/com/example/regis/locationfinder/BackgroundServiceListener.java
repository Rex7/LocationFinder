package com.example.regis.locationfinder;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;


public class BackgroundServiceListener extends Service {
    @Override
    public void onCreate() {
        Toast.makeText(getApplicationContext(), "Service created in LocationFinder", Toast.LENGTH_LONG).show();
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       Toast.makeText(getApplicationContext(),"Am in start Command",Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(),"Service Destroyed",Toast.LENGTH_LONG).show();
        super.onDestroy();
    }


}
