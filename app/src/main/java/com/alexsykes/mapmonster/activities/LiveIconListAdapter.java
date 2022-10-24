package com.alexsykes.mapmonster.activities;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.data.Icon;
import com.alexsykes.mapmonster.data.LiveLayerItem;

import java.util.List;
import java.util.Objects;

public class LiveIconListAdapter extends ListAdapter<Icon, LiveIconViewHolder> {

    public LiveIconListAdapter(@NonNull DiffUtil.ItemCallback<Icon> diffCallback) {
        super(diffCallback);
    }

    @Override
    public void onCurrentListChanged(List<Icon> previousList, List<Icon> currentList) {
        super.onCurrentListChanged(previousList, currentList);
    }

    @NonNull
    @Override
    public LiveIconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return LiveIconViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveIconViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Icon current = getItem(position);
        holder.bind(current, context);
    }

    static class IconDiff extends DiffUtil.ItemCallback<Icon> {

        @Override
        public boolean areItemsTheSame(@NonNull Icon oldItem, @NonNull Icon newItem) {
            return  oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Icon oldItem, @NonNull Icon newItem) {
            return Objects.equals(oldItem, newItem);
        }
    }
}
