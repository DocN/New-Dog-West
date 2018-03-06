package com.drnserver.newdogwest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.drnserver.newdogwest.Models.ParkProperties;

import java.util.List;

public class ParkAdapter extends RecyclerView.Adapter<ParkAdapter.MyViewHolder> {

    private List<ParkProperties> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            genre = (TextView) view.findViewById(R.id.genre);
            year = (TextView) view.findViewById(R.id.year);
        }
    }


    public ParkAdapter(List<ParkProperties> moviesList) {
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
        ParkProperties park = moviesList.get(position);
        holder.title.setText(park.getParkName());
        holder.genre.setText(park.getStrName());
        holder.year.setText(park.getDistance());
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
