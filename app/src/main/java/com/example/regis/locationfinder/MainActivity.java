package com.example.regis.locationfinder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, TransferData {
    TextView result, longitudeText, latitudeText;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private int responseCode;
    private FusedLocationProviderApi locationProviderApi = LocationServices.FusedLocationApi;
    String Tag = "Main_Activity";
    Button check;
    private static final int FINE_LOCATION = 1;
    private static final int COARSE_LOCATION = 2;
    private boolean grantedPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result = (TextView) findViewById(R.id.ResulText);
        latitudeText = (TextView) findViewById(R.id.latitude);
        longitudeText = (TextView) findViewById(R.id.longitude);
        check = (Button) findViewById(R.id.SubmitButton);
        check.setOnClickListener(this);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        LocationRequestConfiguration();
        new MyAsync(this).execute();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.SubmitButton:
                CheckConnection();
                break;
        }
    }

    public void CheckConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        try {

            if (networkInfo != null && networkInfo.isConnected()) {
                result.setText("Connected to to "+networkInfo.getTypeName());
                boolean flag = checkInternetConnection();

                Log.v("boolean","value"+flag);
                if (flag) {
                    Toast.makeText(getApplicationContext(), "We have internet connection", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "We Dont have internet connection", Toast.LENGTH_SHORT).show();
                }


            } else {
                result.setText("No Network detected ");

            }
        } catch (Exception e) {
            Log.v("error ", e.toString());
        }
    }

    public void locationRequestUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION);
            }
            Log.v(Tag, "g");

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    grantedPermission = true;
                    Toast.makeText(getApplicationContext(), "granted", Toast.LENGTH_SHORT).show();
                } else {
                    grantedPermission = false;
                    String req = getResources().getString(R.string.request_not_granted);
                    Toast.makeText(getApplicationContext(), req, Toast.LENGTH_SHORT).show();
                }
                break;
            case COARSE_LOCATION:

                break;
        }

    }

    public void LocationRequestConfiguration() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        locationRequest = new LocationRequest();
        locationRequest.setInterval(60 * 1000);
        locationRequest.setFastestInterval(15 * 1000);

/*
        switch (networkInfo.getTypeName()) {
            case "WIFI":
                Toast.makeText(getApplicationContext(), "WIFI", Toast.LENGTH_SHORT).show();
                break;
            case "MOBILE":
                Toast.makeText(getApplicationContext(), "MOBILE", Toast.LENGTH_SHORT).show();
                break;
            case "null":
                Toast.makeText(getApplicationContext(), "Gps", Toast.LENGTH_SHORT).show();

        }
        */
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequestUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v(Tag, "Connection failed");
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (grantedPermission)
            googleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (grantedPermission)
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient.isConnected()) {
            locationRequestUpdate();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Double lon, lat;
        lon = location.getLongitude();
        lat = location.getLatitude();
        latitudeText.setText(getResources().getText(R.string.lat) + String.valueOf(lat));
        longitudeText.setText(getResources().getText(R.string.lon) + String.valueOf(lon));
        Log.v("Latitude ", "" + lat);
        Log.v("longitude ", " " + lon);
        Toast.makeText(getApplicationContext(), "\nNew location Updating ", Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "Latitude is " + lat + "\nLongitude is " + lon, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setData(int res) {
        responseCode = res;
    }

    public boolean checkInternetConnection() {
        try{
            URL url = new URL("https://www.google.com/");
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(1000);
            HttpURLConnection  httpUrlConnection = (HttpURLConnection)urlConnection;
            httpUrlConnection.connect();
            int res=httpUrlConnection.getResponseCode();
            Log.v("Respnseee","res"+res);
            return true;



        }
        catch(Exception ex){
            Toast.makeText(getApplicationContext(),"Exception raised",Toast.LENGTH_LONG).show();
            Log.v("fuck this shit",ex.getMessage());
            return false;

        }


    }
}
