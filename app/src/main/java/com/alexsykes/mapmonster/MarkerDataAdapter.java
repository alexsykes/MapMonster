package com.alexsykes.mapmonster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.activities.LayerListActivity;
import com.alexsykes.mapmonster.activities.MarkerListActivity;
import com.alexsykes.mapmonster.data.MapMarkerDataItem;

import java.util.List;

public class MarkerDataAdapter extends RecyclerView.Adapter<MarkerDataAdapter.MarkerDataViewHolder> {

    List<MapMarkerDataItem> markerDataItems;


    public MarkerDataAdapter(List<MapMarkerDataItem> allMarkers) {
        markerDataItems = allMarkers;
    }

    @NonNull
    @Override
    public MarkerDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.marker_data_item, parent, false);
        return new MarkerDataViewHolder(view);
    }

    @Override
    // Consider adding marker_identifier to MarkerDataViewHolder
    public void onBindViewHolder(@NonNull MarkerDataViewHolder holder, int position) {
        holder.getMarkerNameTextView().setText(markerDataItems.get(position).placename);
        holder.marker_id = markerDataItems.get(position).getMarkerID();
        holder.getMarkerNameTextView().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                if (context.getClass() == MarkerListActivity.class ) {
                    ((MarkerListActivity) context).onMarkerClickCalled(holder.marker_id);
                }
                else if (context.getClass() == LayerListActivity.class ) {
//                    ((LayerListActivity) context).onMarkerClickCalled(holder.marker_id);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return markerDataItems.size();
    }

    public class MarkerDataViewHolder extends RecyclerView.ViewHolder {
        private final TextView markerNameTextView;
        private  int marker_id;

        public MarkerDataViewHolder(@NonNull View itemView) {
            super(itemView);
            markerNameTextView = itemView.findViewById(R.id.markerNameTextView);
        }
        public TextView getMarkerNameTextView() {
            return markerNameTextView;
        }
    }
}
