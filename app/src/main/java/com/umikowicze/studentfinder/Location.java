package com.umikowicze.studentfinder;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import static android.support.constraint.Constraints.TAG;

class Location implements LocationListener{

    Context context;

    Location(Context context) {
    this.context=context;
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}


     @Override
     public void onLocationChanged(android.location.Location loc) {
        Toast.makeText(
                context,
                "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                        + loc.getLongitude(), Toast.LENGTH_SHORT).show();
        String longitude = "Longitude: " + loc.getLongitude();
        Log.v(TAG, longitude);
        String latitude = "Latitude: " + loc.getLatitude();
        Log.v(TAG, latitude);
    }

    @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
}

