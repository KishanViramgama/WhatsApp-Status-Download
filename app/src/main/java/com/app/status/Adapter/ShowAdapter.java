package com.app.status.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.app.status.Activity.VideoPlayer;
import com.app.status.R;
import com.app.status.Util.TouchImageView;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class ShowAdapter extends PagerAdapter {

    private List<File> string;
    private Activity activity;
    private String type;

    public ShowAdapter(Activity activity, List<File> string, String type) {
        this.activity = activity;
        this.string = string;
        this.type = type;
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup container, final int position) {

        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View view = layoutInflater.inflate(R.layout.show_adapter, container, false);

        TouchImageView touchImageView = view.findViewById(R.id.imageView_image_show_adapter);
        ImageView imageView_play = view.findViewById(R.id.imageView_play);

        if (type.equals("image")) {
            imageView_play.setVisibility(View.GONE);
        } else if (type.equals("all")) {
            if (string.get(position).toString().contains(".jpg")) {
                imageView_play.setVisibility(View.GONE);
            }
        }

        imageView_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, VideoPlayer.class)
                        .putExtra("link", string.get(position).toString()));
            }
        });

        Glide.with(activity).load(string.get(position).toString())
                .placeholder(R.drawable.place_holder)
                .into(touchImageView);

        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return string.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}

