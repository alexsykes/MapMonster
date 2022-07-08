package com.alexsykes.mapmonster;

import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.alexsykes.mapmonster.data.MMarker;

public class MarkerListAdapter extends ListAdapter<MMarker, MarkerViewHolder> {

    AdapterView.OnItemClickListener listener;

    public MarkerListAdapter(@NonNull DiffUtil.ItemCallback<MMarker> diffCallback) {
        super(diffCallback);
    }

    @Override
    public MarkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return MarkerViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull MarkerViewHolder holder, int position) {
        MMarker current= getItem(position);
        // String placename = current.getPlacename();
        holder.bind(current);
    }

    public interface OnItemClickListener {
        void onItemClick(MMarker marker);
    }

    public static class MarkerDiff extends DiffUtil.ItemCallback<MMarker> {
        @Override
        public boolean areItemsTheSame(@NonNull MMarker oldItem, @NonNull MMarker newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull MMarker oldItem, @NonNull MMarker newItem) {
            return oldItem.getPlacename().equals(newItem.getPlacename());
        }
    }
}
