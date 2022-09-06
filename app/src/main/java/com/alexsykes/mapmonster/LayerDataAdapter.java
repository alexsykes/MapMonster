package com.alexsykes.mapmonster;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.data.Layer;
import com.alexsykes.mapmonster.data.LayerDao;
import com.alexsykes.mapmonster.LayerDataViewHolder;

import java.util.List;

public class LayerDataAdapter extends RecyclerView.Adapter<LayerDataViewHolder> {
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
}
