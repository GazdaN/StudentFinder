package com.umikowicze.studentfinder;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.Console;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class HelperAdapter extends ArrayAdapter<Helper>{
    public HelperAdapter(Context context, int resource, List<Helper> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_helper, parent, false);
        }

        ImageView photoImageView = convertView.findViewById(R.id.photoImageView);
        ImageView onlineImageView = convertView.findViewById(R.id.onlineImageView);
        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        TextView onlineTextView = convertView.findViewById(R.id.onlineTextView);
        RatingBar ratingBar = convertView.findViewById(R.id.ratingBar);

        Helper helper = getItem(position);

        nameTextView.setText(helper.getName());

        Glide.with(photoImageView.getContext()).load(helper.getPhotoUrl()).into(photoImageView);

        if (helper.getLocation()) {
            onlineImageView.setImageResource(android.R.drawable.presence_online);
            onlineTextView.setText("Dostępny w pobliżu wydziału");
        }
        else {
            onlineImageView.setImageResource(android.R.drawable.presence_offline);
            onlineTextView.setText("Niedostępny w pobliżu wydziału");
        }

        ratingBar.setRating((float)helper.getRating()*5);

        return convertView;
    }
}
