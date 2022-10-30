package com.alexsykes.mapmonster.data;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.activities.LiveMarkerListActivity;

public class LiveMarkerListViewHolder extends RecyclerView.ViewHolder {
    final TextView markerID_textView;
    private final TextView markerNameTextView, markerCodeTextView, markerListNotesTextView;
    private final ImageView markerVisibilityImage, markerArchivedImage, imageView;
    private final int eye_open_id;
    private final int eye_closed_id;
    private final int trash;
    private final CheckBox selectCheckBox;

    private LiveMarkerListViewHolder(View itemView) {
        super(itemView);

        selectCheckBox = itemView.findViewById(R.id.selectCheckBox);
        markerNameTextView = itemView.findViewById(R.id.markerNameTextView);
        markerCodeTextView = itemView.findViewById(R.id.markerCodeTextView);
        markerListNotesTextView = itemView.findViewById(R.id.markerListNotesTextView);
        markerID_textView = itemView.findViewById(R.id.markerID_textView);
        markerVisibilityImage = itemView.findViewById(R.id.markerToggleImage);
        markerArchivedImage = itemView.findViewById(R.id.markerArchivedImage);
        imageView = itemView.findViewById(R.id.imageView);


        eye_open_id = itemView.getContext().getResources().getIdentifier("eye_outline", "drawable", itemView.getContext().getPackageName());
        eye_closed_id = itemView.getContext().getResources().getIdentifier("eye_off_outline", "drawable", itemView.getContext().getPackageName());
        trash = itemView.getContext().getResources().getIdentifier("trash_can", "drawable", itemView.getContext().getPackageName());
     }

    public void bind(LiveMarkerItem marker, Context context) {
        int archResID, visResID, imageID;

        int resID = context.getResources().getIdentifier(marker.getIconFilename(), "drawable", context.getPackageName());
        imageView.setImageResource(resID);
        markerNameTextView.setText(marker.getPlacename());
        markerID_textView.setText(String.valueOf(marker.getMarkerID()));
        markerNameTextView.setText(marker.getPlacename());
        markerCodeTextView.setText(marker.getCode());
        markerListNotesTextView.setText(marker.getNotes());
        selectCheckBox.setChecked(Boolean.valueOf(marker.isSelected()));

        archResID = marker.isArchived() ? trash : 0;
        markerArchivedImage.setImageResource(archResID);

        visResID = marker.isVisible() ? eye_open_id : eye_closed_id;

        markerVisibilityImage.setImageResource(visResID);
        markerVisibilityImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                ((LiveMarkerListActivity) context).visibilityToggled(marker.getMarkerID());
            }
        });

        selectCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                ((LiveMarkerListActivity) context).selectionToggled(marker.getMarkerID());
            }
        });
    }

    public static LiveMarkerListViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.marker_detail_item, parent, false);
        return new LiveMarkerListViewHolder(view);
    }
}
