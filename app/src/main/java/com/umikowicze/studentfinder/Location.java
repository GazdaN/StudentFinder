package com.umikowicze.studentfinder;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

import static android.location.Location.distanceBetween;
import static android.support.constraint.Constraints.TAG;

class Location implements LocationListener{

    Context context;

    private DatabaseReference mRootReference;
    private String mcurrentUserId;

    Location(Context context, String currentUserId) {

        this.context=context;
        mcurrentUserId = currentUserId;
        mRootReference = FirebaseDatabase.getInstance().getReference();

    }


    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}


     @Override
     public void onLocationChanged(android.location.Location loc) {

        /*
        String longitude = "Longitude: " + loc.getLongitude();
        String latitude = "Latitude: " + loc.getLatitude();*/

         float[] dist = new float[1];

         distanceBetween(loc.getLatitude(),loc.getLongitude(),52.219071,21.011797,dist);

         if(dist[0]/1000 > 1){
             //Toast.makeText(context, "Daleko od uczelni", Toast.LENGTH_LONG).show();
             mRootReference.child("Helpers").child(mcurrentUserId).child("location").setValue(false);
         } else
         {
             //Toast.makeText(context, "Jestes w pobliżu uczelni", Toast.LENGTH_LONG).show();
             mRootReference.child("Helpers").child(mcurrentUserId).child("location").setValue(true);
         }
    }
    @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
}

