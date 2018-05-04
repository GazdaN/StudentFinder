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
        ImageView star1ImageView = convertView.findViewById(R.id.star1ImageView);
        ImageView star2ImageView = convertView.findViewById(R.id.star2ImageView);
        ImageView star3ImageView = convertView.findViewById(R.id.star3ImageView);
        ImageView star4ImageView = convertView.findViewById(R.id.star4ImageView);
        ImageView star5ImageView = convertView.findViewById(R.id.star5ImageView);

        Helper helper = getItem(position);

        nameTextView.setText(helper.getName());

        Glide.with(photoImageView.getContext()).load(helper.getPhotoUrl()).into(photoImageView);

        if (helper.getLocation()) {
            onlineImageView.setImageResource(android.R.drawable.presence_online);
            onlineTextView.setText("Dostępny w pobliżu wydziału");
        }
        else {
            onlineImageView.setImageResource(android.R.drawable.presence_offline);
            onlineTextView.setText("Niedostępny");
        }

        if (helper.getRating() < 0.1) {
            star1ImageView.setImageResource(R.drawable.star16_2);
            star2ImageView.setImageResource(R.drawable.star16_2);
            star3ImageView.setImageResource(R.drawable.star16_2);
            star4ImageView.setImageResource(R.drawable.star16_2);
            star5ImageView.setImageResource(R.drawable.star16_2);
        }
        else if (helper.getRating() >= 0.1 && helper.getRating() < 0.3) {
            star1ImageView.setImageResource(R.drawable.star16);
            star2ImageView.setImageResource(R.drawable.star16_2);
            star3ImageView.setImageResource(R.drawable.star16_2);
            star4ImageView.setImageResource(R.drawable.star16_2);
            star5ImageView.setImageResource(R.drawable.star16_2);
        }
        else if (helper.getRating() >= 0.3 && helper.getRating() < 0.5) {
            star1ImageView.setImageResource(R.drawable.star16);
            star2ImageView.setImageResource(R.drawable.star16);
            star3ImageView.setImageResource(R.drawable.star16_2);
            star4ImageView.setImageResource(R.drawable.star16_2);
            star5ImageView.setImageResource(R.drawable.star16_2);
        }
        else if (helper.getRating() >= 0.5 && helper.getRating() < 0.7) {
            star1ImageView.setImageResource(R.drawable.star16);
            star2ImageView.setImageResource(R.drawable.star16);
            star3ImageView.setImageResource(R.drawable.star16);
            star4ImageView.setImageResource(R.drawable.star16_2);
            star5ImageView.setImageResource(R.drawable.star16_2);
        }
        else if (helper.getRating() >= 0.7 && helper.getRating() < 0.9) {
            star1ImageView.setImageResource(R.drawable.star16);
            star2ImageView.setImageResource(R.drawable.star16);
            star3ImageView.setImageResource(R.drawable.star16);
            star4ImageView.setImageResource(R.drawable.star16);
            star5ImageView.setImageResource(R.drawable.star16_2);
        }
        else if (helper.getRating() >= 0.9) {
            star1ImageView.setImageResource(R.drawable.star16);
            star2ImageView.setImageResource(R.drawable.star16);
            star3ImageView.setImageResource(R.drawable.star16);
            star4ImageView.setImageResource(R.drawable.star16);
            star5ImageView.setImageResource(R.drawable.star16);
        }

        return convertView;
    }
}
