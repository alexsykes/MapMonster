package com.alexsykes.mapmonster;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.activities.LayerListActivity;
import com.alexsykes.mapmonster.data.LayerDataItem;

import java.util.List;

public class LayerDataAdapter extends RecyclerView.Adapter<LayerDataAdapter.LayerDataViewHolder> {

    List<LayerDataItem> layerDataItems;
    public static final String TAG = "Info";

    @Override
    public void onBindViewHolder(@NonNull LayerDataViewHolder holder, int position) {
        Context context = holder.imageView.getContext();
        LayerDataItem layerDataItem = layerDataItems.get(position);
        holder.getLayerNameTextView().setText(layerDataItem.layername);
        holder.isVisible = layerDataItem.isVisible;
        int resID = context.getResources().getIdentifier(layerDataItem.iconFilename, "drawable", context.getPackageName());
        holder.imageView.setImageResource(resID);
        holder.layer_id = layerDataItems.get(position).getLayer_id();
        if (holder.isVisible) {
            holder.layerToggleImage.setImageResource(holder.eye_open_id);
        } else {
            holder.layerToggleImage.setImageResource(holder.eye_closed_id);
        }
        holder.getLayerNameTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
//                int position = holder.getAdapterPosition();
                ((LayerListActivity) context).onLayerClickCalled(holder.layer_id);
            }
        });

        holder.layerToggleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.isVisible) {
                    holder.layerToggleImage.setImageResource(holder.eye_closed_id);
                    Log.i(TAG, "open eye");
                } else {
                    holder.layerToggleImage.setImageResource(holder.eye_open_id);
                    Log.i(TAG, "close eye");
                }
                holder.isVisible = !holder.isVisible;
                ((LayerListActivity) context).visibilityToggle(holder.layer_id);
            }
        });
    }

    public LayerDataAdapter(List<LayerDataItem> allLayers) {
        layerDataItems = allLayers;
    }

    @NonNull
    @Override
    public LayerDataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layer_data_item, viewGroup, false);
        return new LayerDataViewHolder(view);
    }

    public static class LayerDataViewHolder extends RecyclerView.ViewHolder {
        private final TextView layerNameTextView;
        private final ImageView imageView, layerToggleImage;
        private int layer_id;
        private int eye_open_id, eye_closed_id;
        private boolean isVisible;


        public LayerDataViewHolder(@NonNull View itemView) {
            super(itemView);
            layerNameTextView = itemView.findViewById(R.id.layerNameTextView);
            layerToggleImage = itemView.findViewById(R.id.layerToggleImage);
            imageView = itemView.findViewById(R.id.imageView);

            eye_open_id = itemView.getContext().getResources().getIdentifier("eye_outline", "drawable", itemView.getContext().getPackageName());
            eye_closed_id = itemView.getContext().getResources().getIdentifier("eye_off_outline", "drawable", itemView.getContext().getPackageName());
        }

        public TextView getLayerNameTextView() {
            return layerNameTextView;
        }
    }

    @Override
    public int getItemCount() {
        return layerDataItems.size();
    }

}
