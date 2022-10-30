package com.alexsykes.mapmonster.activities;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.alexsykes.mapmonster.data.LiveMarkerItem;

import java.util.List;
import java.util.Objects;

public class LiveMarkerEditListAdapter extends ListAdapter<LiveMarkerItem, LiveMarkerEditListViewHolder> {
    public static final String TAG = "Info";
    protected LiveMarkerEditListAdapter(@NonNull DiffUtil.ItemCallback<LiveMarkerItem> diffCallback) {
        super(diffCallback);
    }

    @Override
    public void onCurrentListChanged(@NonNull List<LiveMarkerItem> previousList, @NonNull List<LiveMarkerItem> currentList) {
        super.onCurrentListChanged(previousList, currentList);
    }


    @NonNull
    @Override
    public LiveMarkerEditListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return LiveMarkerEditListViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveMarkerEditListViewHolder holder, int position) {
        LiveMarkerItem current = getItem(position);
        Context context = holder.itemView.getContext();
        holder.bind(current, context);
    }

    public static class LiveMarkerDiff extends DiffUtil.ItemCallback<LiveMarkerItem> {
        @Override
        public boolean areItemsTheSame(@NonNull LiveMarkerItem oldItem, @NonNull LiveMarkerItem newItem) {
            if(oldItem != newItem ) {
                Log.i(TAG, "oldItem != newItem");
            }
            return true;
        }

        @Override
        public boolean areContentsTheSame(@NonNull LiveMarkerItem oldItem, @NonNull LiveMarkerItem newItem) {
            if(oldItem.getPlacename() != newItem.getPlacename()  ) {
                Log.i(TAG, "areContentsTheSame = placename changed: ");
            }
            return Objects.equals(oldItem, newItem);
        }
    }
}
