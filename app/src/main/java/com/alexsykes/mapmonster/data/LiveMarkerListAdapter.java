package com.alexsykes.mapmonster.data;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import java.util.List;
import java.util.Objects;

public class LiveMarkerListAdapter extends ListAdapter<LiveMarkerItem, LiveMarkerListViewHolder> {
    public static final String TAG = "Info";
    
    public LiveMarkerListAdapter(@NonNull DiffUtil.ItemCallback<LiveMarkerItem> diffCallback) {
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

    public static class MMarkerDiff extends DiffUtil.ItemCallback<LiveMarkerItem> {
        @Override
        // https://medium.com/tech-insider/diffutil-handling-recyclerview-smartly-ac3401d22903
        // Tests and scrolls entire table
//        DiffUtil to decide whether two objects represent the same Item. (
//        define its body if the data types of your model are variable and list items can be different) Usually, we return true in this case
        public boolean areItemsTheSame(@NonNull LiveMarkerItem oldItem, @NonNull LiveMarkerItem newItem) {
            return  true; // oldItem == newItem;
        }

        @Override
//        Called by the DiffUtil when it wants to check whether two items have the same data. (contents inside your data model)
        public boolean areContentsTheSame(@NonNull LiveMarkerItem oldItem, @NonNull LiveMarkerItem newItem) {
            return Objects.equals(oldItem, newItem);
        }
    }
}
