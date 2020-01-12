package com.example.krushiler.testbase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlayerStatsAdapter extends RecyclerView.Adapter<PlayerStatsAdapter.StatsHolder>  {
    // List to store all the contact details
    private ArrayList<PlayerStats> statsList;
    private Context mContext;
    // Counstructor for the Class
    public PlayerStatsAdapter(ArrayList<PlayerStats> statsList, Context context) {
        this.statsList = statsList;
        this.mContext = context;
    }
    // This method creates views for the RecyclerView by inflating the layout
    // Into the viewHolders which helps to display the items in the RecyclerView
    @Override
    public StatsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Inflate the layout view you have created for the list rows here
        View view = layoutInflater.inflate(R.layout.list_item_results, parent, false);
        return new StatsHolder(view);
    }
    @Override
    public int getItemCount() {
        return statsList == null? 0: statsList.size();
    }
    // This method is called when binding the data to the views being created in RecyclerView
    @Override
    public void onBindViewHolder(@NonNull StatsHolder holder, final int position) {
        final PlayerStats stat = statsList.get(position);

        // Set the data to the views here
        holder.setStatName(stat.getName());
        holder.setStatMisses(stat.getMisses());
        holder.setStatPlace(stat.getPlace());
        holder.setStatTime(stat.getTime());
        // You can set click listners to indvidual items in the viewholder here
        // make sure you pass down the listner or make the Data members of the viewHolder public

    }
    // This is your ViewHolder class that helps to populate data to the view
    public class StatsHolder extends RecyclerView.ViewHolder {
        private TextView txtName;
        private TextView txtPlace;
        private TextView txtMisses;
        private TextView txtTime;

        public StatsHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.nameTV);
            txtPlace = itemView.findViewById(R.id.placeTV);
            txtMisses = itemView.findViewById(R.id.missesTV);
            txtTime = itemView.findViewById(R.id.timeTV);
        }

        public void setStatName(String name) {
            txtName.setText(name);
        }
        public void setStatPlace(String place) {
            txtPlace.setText(place);
        }
        public void setStatMisses(String misses) {
            txtMisses.setText(misses);
        }
        public void setStatTime(String time) {
            txtTime.setText(time);
        }
    }
}
