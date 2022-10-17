package com.alexsykes.mapmonster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.data.MapMarkerDataItem;

import java.util.List;

public class MarkerListAdapter extends RecyclerView.Adapter<MarkerListAdapter.MarkerEditViewHolder> {
    List<MapMarkerDataItem> allMarkers;
    public static final String TAG = "Info";

    public MarkerListAdapter(List<MapMarkerDataItem> markerList) {
        allMarkers = markerList;
    }

    @NonNull
    @Override
    public MarkerEditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.marker_detail_item, parent, false);
        return new MarkerListAdapter.MarkerEditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarkerEditViewHolder holder, int position) {
        Context context = holder.imageView.getContext();
        MapMarkerDataItem currentMarker = allMarkers.get(position);

        String markerID = String.valueOf(currentMarker.markerID);
        holder.markerID_textView.setText(markerID);
        holder.markerNameTextView.setText(currentMarker.placename);

        int resID = context.getResources().getIdentifier(currentMarker.filename, "drawable", context.getPackageName());
        holder.imageView.setImageResource(resID);
        holder.marker_id = allMarkers.get(position).getLayer_id();

        if (currentMarker.isVisible) {
            holder.markerToggleImage.setImageResource(holder.eye_open_id);
        } else {
            holder.markerToggleImage.setImageResource(holder.eye_closed_id);
        }
    }

    @Override
    public int getItemCount() {
        return allMarkers.size();
    }

    public class MarkerEditViewHolder extends RecyclerView.ViewHolder {
        final TextView markerID_textView;
        private final TextView markerNameTextView;
        private final ImageView imageView, markerToggleImage;
        private  int marker_id;
        private int eye_open_id, eye_closed_id;


        public MarkerEditViewHolder(@NonNull View itemView) {
            super(itemView);
             markerID_textView = itemView.findViewById(R.id.markerID_textView);
            markerNameTextView = itemView.findViewById(R.id.markerNameTextView);
            markerToggleImage = itemView.findViewById(R.id.markerToggleImage);
            imageView = itemView.findViewById(R.id.imageView);
            eye_open_id = itemView.getContext().getResources().getIdentifier("eye_outline", "drawable", itemView.getContext().getPackageName());
            eye_closed_id = itemView.getContext().getResources().getIdentifier("eye_off_outline", "drawable", itemView.getContext().getPackageName());
        }
    }
}
