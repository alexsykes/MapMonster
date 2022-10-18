package com.alexsykes.mapmonster;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.activities.MarkerEditActivity;
import com.alexsykes.mapmonster.activities.MarkerListActivity;
import com.alexsykes.mapmonster.data.MapMarkerDataItem;
import com.alexsykes.mapmonster.data.MarkerViewModel;

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

        holder.marker_id = currentMarker.getMarkerID();
        holder.isArchived = currentMarker.isArchived;
        holder.isVisible = currentMarker.isVisible;
        holder.markerID_textView.setText(String.valueOf(holder.marker_id));
        holder.markerNameTextView.setText(currentMarker.placename);
        holder.markerCodeTextView.setText(currentMarker.code);
        holder.markerListNotesTextView.setText(currentMarker.notes);

        int resID = context.getResources().getIdentifier(currentMarker.filename, "drawable", context.getPackageName());
        holder.imageView.setImageResource(resID);
        holder.marker_id = allMarkers.get(position).getMarkerID();

        holder.selectCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Context context = buttonView.getContext();
//                int marker_id = holder.marker_id;
                ((MarkerListActivity) context).onSelectedChanged(currentMarker.markerID, isChecked);
            }
        });

//        holder.markerArchivedImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Log.i(TAG, "onClick: " + holder.marker_id);
//                // Toggle image
//
//                Context context = v.getContext();
//                holder.isArchived = !holder.isArchived;
//                if (holder.isArchived) {
//                    holder.markerArchivedImage.setImageResource(holder.archive);
//                } else {
//                    holder.markerArchivedImage.setImageResource(holder.trash);
//                }
//                ((MarkerListActivity) context).onArchiveImageCalled(currentMarker.markerID, holder.isArchived);
//            }
//        });

        holder.markerToggleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, "onClick: " + holder.marker_id);
                // Toggle image

                Context context = v.getContext();
                holder.isVisible = !holder.isVisible;
                if (holder.isVisible) {
                    holder.markerToggleImage.setImageResource(holder.eye_open_id);
                } else {
                    holder.markerToggleImage.setImageResource(holder.eye_closed_id);
                }
                ((MarkerListActivity) context).onVisibleImageCalled(currentMarker.markerID, holder.isVisible);
            }
        });

        if (holder.isVisible) {
            holder.markerToggleImage.setImageResource(holder.eye_open_id);
        } else {
            holder.markerToggleImage.setImageResource(holder.eye_closed_id);
        }

        if (holder.isArchived) {
            holder.markerArchivedImage.setImageResource(holder.trash);
        } else {
            holder.markerArchivedImage.setImageResource(0);
        }
    }

    @Override
    public int getItemCount() {
        return allMarkers.size();
    }

    public class MarkerEditViewHolder extends RecyclerView.ViewHolder {
        final TextView markerID_textView;
        private final TextView markerNameTextView, markerCodeTextView, markerListNotesTextView;
        private final ImageView imageView, markerToggleImage, markerArchivedImage;
        private  int marker_id;
        private boolean isVisible, isArchived;
        private int eye_open_id, eye_closed_id, trash, archive;
        private CheckBox selectCheckBox;

        public MarkerEditViewHolder(@NonNull View itemView) {
            super(itemView);
            markerID_textView = itemView.findViewById(R.id.markerID_textView);
            markerNameTextView = itemView.findViewById(R.id.markerNameTextView);
            markerToggleImage = itemView.findViewById(R.id.markerToggleImage);
            markerCodeTextView = itemView.findViewById(R.id.markerCodeTextView);
            markerListNotesTextView = itemView.findViewById(R.id.markerListNotesTextView);
            markerArchivedImage = itemView.findViewById(R.id.markerArchivedImage);
            imageView = itemView.findViewById(R.id.imageView);
            selectCheckBox = itemView.findViewById(R.id.selectCheckBox);

            eye_open_id = itemView.getContext().getResources().getIdentifier("eye_outline", "drawable", itemView.getContext().getPackageName());
            eye_closed_id = itemView.getContext().getResources().getIdentifier("eye_off_outline", "drawable", itemView.getContext().getPackageName());
            trash = itemView.getContext().getResources().getIdentifier("trash_can", "drawable", itemView.getContext().getPackageName());
            archive = itemView.getContext().getResources().getIdentifier("archive", "drawable", itemView.getContext().getPackageName());
        }
    }
}
