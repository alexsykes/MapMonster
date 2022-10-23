package com.alexsykes.mapmonster.activities;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.alexsykes.mapmonster.data.LiveLayerItem;

import java.util.List;
import java.util.Objects;

public class LiveLayerListAdapter extends ListAdapter<LiveLayerItem, LiveLayerViewHolder> {
    protected LiveLayerListAdapter(@NonNull DiffUtil.ItemCallback<LiveLayerItem> diffCallback) {
        super(diffCallback);
    }

    @Override
    public void onCurrentListChanged(List<LiveLayerItem> previousList, List<LiveLayerItem> currentList) {
        super.onCurrentListChanged(previousList, currentList);
    }

    @NonNull
    @Override
    public LiveLayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return LiveLayerViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveLayerViewHolder holder, int position) {
        LiveLayerItem current = getItem(position);
        Context context = holder.itemView.getContext();
        holder.bind(current, context);
    }

    public static class LiveLayerDiff extends DiffUtil.ItemCallback<LiveLayerItem> {
        @Override
        public boolean areItemsTheSame(@NonNull LiveLayerItem oldItem, @NonNull LiveLayerItem newItem) {
            return true;
        }

        @Override
        public boolean areContentsTheSame(@NonNull LiveLayerItem oldItem, @NonNull LiveLayerItem newItem) {
            return Objects.equals(oldItem, newItem);
        }
    }
}
