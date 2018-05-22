package com.umikowicze.studentfinder;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import static android.location.Location.distanceBetween;
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

         float[] dist = new float[1];

         distanceBetween(loc.getLatitude(),loc.getLongitude(),52.219071,21.011797,dist);

         if(dist[0]/1000 > 0.4){
             Toast.makeText(context, "Daleko od uczelni", Toast.LENGTH_LONG).show();
         } else
         {
             Toast.makeText(context, "Jestes w pobliżu uczelni", Toast.LENGTH_LONG).show();
         }
    }
    @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
}

