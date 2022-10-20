package com.alexsykes.mapmonster.data;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import java.util.List;

public class LiveMarkerListAdapter extends ListAdapter<MMarker, LiveMarkerListViewHolder> {
    
    public LiveMarkerListAdapter(@NonNull DiffUtil.ItemCallback<MMarker> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public LiveMarkerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return LiveMarkerListViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveMarkerListViewHolder holder, int position) {
        MMarker current = getItem(position);
        holder.bind(current.getPlacename());
    }

    public void submitList(List<MMarker> mMarkers) {

    }

    public static class MMarkerDiff extends DiffUtil.ItemCallback<MMarker> {

        @Override
        public boolean areItemsTheSame(@NonNull MMarker oldItem, @NonNull MMarker newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull MMarker oldItem, @NonNull MMarker newItem) {
            return oldItem.getCode().equals(newItem.getCode());
        }
    }
}
