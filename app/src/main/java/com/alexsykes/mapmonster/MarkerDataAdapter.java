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

import com.alexsykes.mapmonster.activities.MarkerEditActivity;
import com.alexsykes.mapmonster.data.LiveMarkerItem;

import java.util.List;

public class MarkerDataAdapter extends RecyclerView.Adapter<MarkerDataAdapter.MarkerDataViewHolder> {

    List<LiveMarkerItem> markersFromVisibleLayers;
    public static final String TAG = "Info";

    @SuppressLint("RecyclerView")
    @Override
    // Consider adding marker_identifier to MarkerDataViewHolder
    public void onBindViewHolder(@NonNull MarkerDataViewHolder holder, int position) {
        Context context = holder.imageView.getContext();
        LiveMarkerItem currentMarker = markersFromVisibleLayers.get(position);
        int resID = context.getResources().getIdentifier(currentMarker.getIconFilename(), "drawable",
                context.getPackageName());
        holder.imageView.setImageResource(resID);
        holder.marker_id = markersFromVisibleLayers.get(position).getLayer_id();

        if (currentMarker.isVisible()) {
            holder.markerToggleImage.setImageResource(holder.eye_open_id);
        } else {
            holder.markerToggleImage.setImageResource(holder.eye_closed_id);
        }

        holder.getMarkerNameTextView().setText(currentMarker.getPlacename());
        holder.marker_id = markersFromVisibleLayers.get(position).getMarkerID();
//        holder.marker_id = position;
        holder.getMarkerNameTextView().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Context context = v.getContext();
//                ((MarkerEditActivity) context).onMarkerClickCalled(holder.marker_id);
            }
        });

        holder.markerToggleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MarkerEditActivity) context).visibilityToggle(holder.marker_id);
            }
        });
    }


    public MarkerDataAdapter(List<LiveMarkerItem> allMarkers) {
        markersFromVisibleLayers = allMarkers;
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
        return markersFromVisibleLayers.size();
    }

    public static class MarkerDataViewHolder extends RecyclerView.ViewHolder {
        private final TextView markerNameTextView;
        private final ImageView imageView, markerToggleImage;
        private  int marker_id;
        private final int eye_open_id;
        private final int eye_closed_id;

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
