package com.umikowicze.studentfinder;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Console;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class HelpRequestAdapter extends ArrayAdapter<HelpRequest>{
    public HelpRequestAdapter(Context context, int resource, List<HelpRequest> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_help_request, parent, false);
        }

        final ImageView photoImageView = convertView.findViewById(R.id.photoImageView);
        final TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        final TextView areaTextView = convertView.findViewById(R.id.areaTextView);
        final TextView dateTextView = convertView.findViewById(R.id.dateTextView);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        HelpRequest helpRequest = getItem(position);

        Query query = databaseReference.child("Helpers").orderByKey().equalTo(helpRequest.getRequesterid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                 @Override
                                                 public void onDataChange(DataSnapshot dataSnapshot) {
                                                     if (dataSnapshot.exists()) {
                                                         for (DataSnapshot dss : dataSnapshot.getChildren()) {
                                                             final Helper helper = dss.getValue(Helper.class);
                                                             nameTextView.setText(helper.getName());
                                                             Glide.with(photoImageView.getContext()).load(helper.getPhotoUrl()).into(photoImageView);
                                                         }
                                                     }
                                                 }

                                                 @Override
                                                 public void onCancelled(DatabaseError databaseError) {

                                                 }
                                             });
        areaTextView.setText(helpRequest.getArea());

        Calendar requestDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm aa");
        try {
            requestDate.setTime(sdf.parse(helpRequest.getDatetime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar nowDate = Calendar.getInstance();
        long diff = nowDate.getTimeInMillis() - requestDate.getTimeInMillis();
        if (diff < 60 * 1000)
            dateTextView.setText("Mniej niż minutę temu");
        else if (diff >= 60 * 1000 && diff < 60 * 60 * 1000) {
            int mins = (int) diff / 60000;
            dateTextView.setText(mins + " min temu");
        }
        else if (diff >= 60 * 60 * 1000) {
            int mins = (int) diff / 3600000;
            dateTextView.setText(mins + " h temu");
        }

        return convertView;
    }
}
