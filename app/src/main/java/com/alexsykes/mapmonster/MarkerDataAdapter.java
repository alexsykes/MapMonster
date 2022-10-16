package com.alexsykes.mapmonster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.activities.LayerListActivity;
import com.alexsykes.mapmonster.activities.MarkerListActivity;
import com.alexsykes.mapmonster.data.MapMarkerDataItem;

import java.util.List;

public class MarkerDataAdapter extends RecyclerView.Adapter<MarkerDataAdapter.MarkerDataViewHolder> {

    List<MapMarkerDataItem> markerDataItems;
    public static final String TAG = "Info";

    @SuppressLint("RecyclerView")
    @Override
    // Consider adding marker_identifier to MarkerDataViewHolder
    public void onBindViewHolder(@NonNull MarkerDataViewHolder holder, int position) {
        Context context = holder.imageView.getContext();
        MapMarkerDataItem currentMarker = markerDataItems.get(position);
        int resID = context.getResources().getIdentifier(currentMarker.filename, "drawable", context.getPackageName());
        holder.imageView.setImageResource(resID);
        holder.marker_id = markerDataItems.get(position).getLayer_id();

        if (currentMarker.isVisible) {
            holder.markerToggleImage.setImageResource(holder.eye_open_id);
        } else {
            holder.markerToggleImage.setImageResource(holder.eye_closed_id);
        }

        holder.getMarkerNameTextView().setText(currentMarker.placename);
        holder.marker_id = markerDataItems.get(position).getMarkerID();
//        holder.marker_id = position;
        holder.getMarkerNameTextView().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                ((MarkerListActivity) context).onMarkerClickCalled(holder.marker_id);
            }
        });        holder.markerToggleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (holder.isVisible) {
//                    holder.layerToggleImage.setImageResource(holder.eye_closed_id);
//                    Log.i(TAG, "open eye");
//                } else {
//                    holder.layerToggleImage.setImageResource(holder.eye_open_id);
//                    Log.i(TAG, "close eye");
//                }
//                holder.isVisible = !holder.isVisible;
                ((MarkerListActivity) context).visibilityToggle(holder.marker_id);
            }
        });
    }


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
    public int getItemCount() {
        return markerDataItems.size();
    }

    public static class MarkerDataViewHolder extends RecyclerView.ViewHolder {
        private final TextView markerNameTextView;
        private final ImageView imageView, markerToggleImage;
        private  int marker_id;
        private int eye_open_id, eye_closed_id;

        public MarkerDataViewHolder(@NonNull View itemView) {
            super(itemView);
            markerNameTextView = itemView.findViewById(R.id.markerNameTextView);
            markerToggleImage = itemView.findViewById(R.id.markerToggleImage);
            imageView = itemView.findViewById(R.id.imageView);
            eye_open_id = itemView.getContext().getResources().getIdentifier("eye_outline", "drawable", itemView.getContext().getPackageName());
            eye_closed_id = itemView.getContext().getResources().getIdentifier("eye_off_outline", "drawable", itemView.getContext().getPackageName());
        }
        public TextView getMarkerNameTextView() {
            return markerNameTextView;
        }
    }
}
