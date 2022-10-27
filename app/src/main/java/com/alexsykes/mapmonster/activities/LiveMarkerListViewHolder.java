package com.alexsykes.mapmonster.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.LiveMarkerItem;

public class LiveMarkerListViewHolder extends  RecyclerView.ViewHolder {
    public static final String TAG = "Info";
    ImageView imageView, markerToggleImage;
    TextView markerNameTextView;
    private int eye_open_id, eye_closed_id;

    public LiveMarkerListViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageView);
        markerToggleImage = itemView.findViewById(R.id.markerToggleImage);
        markerNameTextView = itemView.findViewById(R.id.markerNameTextView);

        eye_open_id = itemView.getContext().getResources().getIdentifier("eye_outline", "drawable", itemView.getContext().getPackageName());
        eye_closed_id = itemView.getContext().getResources().getIdentifier("eye_off_outline", "drawable", itemView.getContext().getPackageName());
    }

    public static LiveMarkerListViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.marker_data_item, parent,false);
        return new LiveMarkerListViewHolder(view);
    }

    public void bind(LiveMarkerItem current, Context context) {
        int resID = context.getResources().getIdentifier(current.getIconFilename(), "drawable",
                context.getPackageName());
        imageView.setImageResource(resID);
        markerNameTextView.setText(current.getPlacename());

        int visResID = current.isVisible() ? eye_open_id : eye_closed_id;
        markerToggleImage.setImageResource(visResID);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MarkerEditActivity) context).onLayerClickCalled(current.getLayer_id());
            }
        });

        markerToggleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MarkerEditActivity) context).visibilityToggle(current.getLayer_id());
            }
        });
    }
}
