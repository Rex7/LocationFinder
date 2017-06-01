package com.example.regis.locationfinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;


public class BroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar =Calendar.getInstance();
        Log.v("system started at ","Time"+calendar.getTime());
        Toast.makeText(context, "Started booted at ", Toast.LENGTH_LONG).show();
        context.startService(new Intent(context,BackgroundServiceListener.class));
    }
}
