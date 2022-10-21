package com.alexsykes.mapmonster.data;

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
    private final ImageView imageView, markerVisibilityImage, markerArchivedImage;
    private  int marker_id;
    private boolean isVisible, isArchived;
    private int eye_open_id, eye_closed_id, trash, archive;
    private CheckBox selectCheckBox;

    private LiveMarkerListViewHolder(View itemView) {
        super(itemView);

        selectCheckBox = itemView.findViewById(R.id.selectCheckBox);
        markerNameTextView = itemView.findViewById(R.id.markerNameTextView);
        markerCodeTextView = itemView.findViewById(R.id.markerCodeTextView);
        markerListNotesTextView = itemView.findViewById(R.id.markerListNotesTextView);
        markerID_textView = itemView.findViewById(R.id.markerID_textView);
        imageView = itemView.findViewById(R.id.imageView);
        markerVisibilityImage = itemView.findViewById(R.id.markerToggleImage);
        markerArchivedImage = itemView.findViewById(R.id.markerArchivedImage);

        eye_open_id = itemView.getContext().getResources().getIdentifier("eye_outline", "drawable", itemView.getContext().getPackageName());
        eye_closed_id = itemView.getContext().getResources().getIdentifier("eye_off_outline", "drawable", itemView.getContext().getPackageName());
        trash = itemView.getContext().getResources().getIdentifier("trash_can", "drawable", itemView.getContext().getPackageName());
        archive = itemView.getContext().getResources().getIdentifier("archive", "drawable", itemView.getContext().getPackageName());
    }

    public void bind(MMarker marker, Context context) {

       int resID;
//        imageView.setImageResource(resID);
        markerNameTextView.setText(marker.getPlacename());
        marker_id = marker.getMarker_id();
        isArchived = marker.isArchived();
        isVisible = marker.isVisible();
        markerID_textView.setText(String.valueOf(marker.getMarker_id()));
        markerNameTextView.setText(marker.getPlacename());
        markerCodeTextView.setText(marker.getCode());
        markerListNotesTextView.setText(marker.getNotes());
        selectCheckBox.setChecked(Boolean.valueOf(marker.isSelected()));
        if(marker.isArchived()) {
            resID = trash;
        } else {
            resID = 0;
        }
        markerArchivedImage.setImageResource(resID);

        if(marker.isVisible()) {
            resID = eye_open_id;
        } else {
            resID = eye_closed_id;
        }
        markerVisibilityImage.setImageResource(resID);
        markerVisibilityImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                ((LiveMarkerListActivity) context).visibilityToggled(marker);
            }
        });

        selectCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                ((LiveMarkerListActivity) context).selectionToggled(marker);
            }
        });
    }

    public static LiveMarkerListViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.marker_detail_item, parent, false);
        return new LiveMarkerListViewHolder(view);
    }
}
