package com.alexsykes.mapmonster;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.alexsykes.mapmonster.data.Layer;
import com.alexsykes.mapmonster.data.Marker;

public class LayerListAdapter extends ListAdapter<Layer, LayerViewHolder> {
    AdapterView.OnItemClickListener listener;
    
    public LayerListAdapter(@NonNull DiffUtil.ItemCallback<Layer> diffCallback) {
        super(diffCallback);
    }
    
    
    @NonNull
    @Override
    public LayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return LayerViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull LayerViewHolder holder, int position) {
        Layer current= getItem(position);
        // String placename = current.getPlacename();
        holder.bind(current);
    }
    public interface OnItemClickListener {
        void onItemClick(Marker marker);
    }

    public static class LayerDiff extends DiffUtil.ItemCallback<Layer> {
        @Override
        public boolean areItemsTheSame(@NonNull Layer oldItem, @NonNull Layer newItem) {
             Log.i("Info", "areItemsTheSame: ");
//            return oldItem == newItem;
            return true;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Layer oldItem, @NonNull Layer newItem) {
             Log.i("Info", "areContentsTheSame: ");
//            return oldItem.getLayername().equals(newItem.getLayername());
            return true;
        }
    }
}
