package com.alexsykes.mapmonster.activities;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.alexsykes.mapmonster.data.LiveLayerItem;
import com.alexsykes.mapmonster.data.LiveMarkerItem;
import com.alexsykes.mapmonster.data.LiveMarkerListViewHolder;

import java.util.List;
import java.util.Objects;

public class LiveMarkerListAdapter extends ListAdapter<LiveMarkerItem, LiveMarkerListViewHolder> {
    public static final String TAG = "Info";
    protected LiveMarkerListAdapter(@NonNull DiffUtil.ItemCallback<LiveMarkerItem> diffCallback) {
        super(diffCallback);
    }

    @Override
    public void onCurrentListChanged(@NonNull List<LiveMarkerItem> previousList, @NonNull List<LiveMarkerItem> currentList) {
        super.onCurrentListChanged(previousList, currentList);
    }


    @NonNull
    @Override
    public LiveMarkerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return LiveMarkerListViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveMarkerListViewHolder holder, int position) {
        LiveMarkerItem current = getItem(position);
        Context context = holder.itemView.getContext();
        holder.bind(current, context);
    }

    public static class LiveMarkerDiff extends DiffUtil.ItemCallback<LiveMarkerItem> {
        @Override
        public boolean areItemsTheSame(@NonNull LiveMarkerItem oldItem, @NonNull LiveMarkerItem newItem) {
            if(oldItem.isVisible() != newItem.isVisible() ) {
                Log.i(TAG, "areItemsTheSame = Visibility changed: " + oldItem.getMarkerID());
            }
            return true;
        }

        @Override
        public boolean areContentsTheSame(@NonNull LiveMarkerItem oldItem, @NonNull LiveMarkerItem newItem) {
            if(oldItem.isVisible() != newItem.isVisible() ) {
                Log.i(TAG, "areContentsTheSame = Visibility changed: ");
            }
            return Objects.equals(oldItem, newItem);
        }
    }
}
