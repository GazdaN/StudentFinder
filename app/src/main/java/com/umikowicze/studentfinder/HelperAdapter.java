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

        if (helper.getLocation()) {
            onlineImageView.setImageResource(android.R.drawable.presence_online);
            onlineTextView.setText("Dostępny w pobliżu wydziału");
        }
        else {
            onlineImageView.setImageResource(android.R.drawable.presence_offline);
            onlineTextView.setText("Niedostępny");
        }

        if (helper.getRating() < 0.1) {
            star1ImageView.setImageResource(android.R.drawable.btn_star_big_on);
            star2ImageView.setImageResource(android.R.drawable.btn_star_big_off);
            star3ImageView.setImageResource(android.R.drawable.btn_star_big_off);
            star4ImageView.setImageResource(android.R.drawable.btn_star_big_off);
            star5ImageView.setImageResource(android.R.drawable.btn_star_big_off);
        }
        else if (helper.getRating() >= 0.3 && helper.getRating() < 0.5) {
            star1ImageView.setImageResource(android.R.drawable.btn_star_big_on);
            star2ImageView.setImageResource(android.R.drawable.btn_star_big_on);
            star3ImageView.setImageResource(android.R.drawable.btn_star_big_off);
            star4ImageView.setImageResource(android.R.drawable.btn_star_big_off);
            star5ImageView.setImageResource(android.R.drawable.btn_star_big_off);
        }
        else if (helper.getRating() >= 0.5 && helper.getRating() < 0.7) {
            star1ImageView.setImageResource(android.R.drawable.btn_star_big_on);
            star2ImageView.setImageResource(android.R.drawable.btn_star_big_on);
            star3ImageView.setImageResource(android.R.drawable.btn_star_big_on);
            star4ImageView.setImageResource(android.R.drawable.btn_star_big_off);
            star5ImageView.setImageResource(android.R.drawable.btn_star_big_off);
        }
        else if (helper.getRating() >= 0.7 && helper.getRating() < 0.9) {
            star1ImageView.setImageResource(android.R.drawable.btn_star_big_on);
            star2ImageView.setImageResource(android.R.drawable.btn_star_big_on);
            star3ImageView.setImageResource(android.R.drawable.btn_star_big_on);
            star4ImageView.setImageResource(android.R.drawable.btn_star_big_on);
            star5ImageView.setImageResource(android.R.drawable.btn_star_big_off);
        }
        else if (helper.getRating() >= 0.9) {
            star1ImageView.setImageResource(android.R.drawable.btn_star_big_on);
            star2ImageView.setImageResource(android.R.drawable.btn_star_big_on);
            star3ImageView.setImageResource(android.R.drawable.btn_star_big_on);
            star4ImageView.setImageResource(android.R.drawable.btn_star_big_on);
            star5ImageView.setImageResource(android.R.drawable.btn_star_big_on);
        }

        return convertView;
    }
}
