package com.alexsykes.mapmonster;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.data.LayerDao;

import java.util.List;

public class LayerDataAdapter extends RecyclerView.Adapter<LayerDataAdapter.LayerDataViewHolder> {
    List<LayerDao.LayerData> layerData;
    public LayerDataAdapter(List<LayerDao.LayerData> allLayers) {
        this.layerData = allLayers;
    }


    @NonNull
    @Override
    public LayerDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull LayerDataViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return layerData.size();
    }

    public class LayerDataViewHolder extends RecyclerView.ViewHolder {
        TextView layerNameTextView;
        public LayerDataViewHolder(@NonNull View itemView) {
            super(itemView);
            layerNameTextView = itemView.findViewById(R.id.layerNameTextView);
        }
    }
}
