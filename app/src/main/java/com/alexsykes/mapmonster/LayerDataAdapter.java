package com.alexsykes.mapmonster;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.activities.LayerListActivity;
import com.alexsykes.mapmonster.data.LayerDao;

import java.util.List;

public class LayerDataAdapter extends RecyclerView.Adapter<LayerDataAdapter.LayerDataViewHolder> {

    List<LayerDao.LayerDataItem> layerDataItems;
    public static final String TAG = "Info";

    public static class LayerDataViewHolder extends RecyclerView.ViewHolder {
        private final TextView layerNameTextView;

        public LayerDataViewHolder(@NonNull View itemView) {
            super(itemView);
            layerNameTextView = itemView.findViewById(R.id.layerNameTextView);

        }

        public TextView getLayerNameTextView() {
            return layerNameTextView;
        }
    }

    public LayerDataAdapter(List<LayerDao.LayerDataItem> allLayers) {
        layerDataItems = allLayers;
    }

    @NonNull
    @Override
    public LayerDataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layer_data_item, viewGroup, false);
        return new LayerDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LayerDataViewHolder holder, int position) {
        holder.getLayerNameTextView().setText(layerDataItems.get(position).layername);

        holder.getLayerNameTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                int position = holder.getAdapterPosition();
                ((LayerListActivity) context).onClickCalled(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return layerDataItems.size();
    }

}
