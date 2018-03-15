package com.drnserver.newdogwest;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.drnserver.newdogwest.Models.PlaceProperties;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import javax.security.auth.callback.Callback;

public class ParkAdapter extends RecyclerView.Adapter<ParkAdapter.MyViewHolder> {

    private List<PlaceProperties> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;
        public ImageView avatar;

        public MyViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            genre = (TextView) view.findViewById(R.id.genre);
            year = (TextView) view.findViewById(R.id.year);
            avatar = (ImageView) view.findViewById(R.id.dogeAvatar);
        }
    }


    public ParkAdapter(List<PlaceProperties> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.park_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PlaceProperties park = moviesList.get(position);
        holder.title.setText(park.getParkName());
        holder.genre.setText(park.getStrName());
        holder.year.setText(park.getDistance() + "km");
        if(park.getImgUrl().length() > 0) {
            Picasso.with(holder.itemView.getContext()).load(park.getImgUrl()).resize(100, 100).into(holder.avatar);
        }
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
