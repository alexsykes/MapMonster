package com.alexsykes.mapmonster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.activities.MainActivity;
import com.alexsykes.mapmonster.data.Layer;

public class LayerViewHolder extends RecyclerView.ViewHolder{
    final TextView layerNameTextView, codeTextView;
    public LayerViewHolder(@NonNull View itemView) {
        super(itemView);
        layerNameTextView = itemView.findViewById(R.id.layerNameTextView);
        codeTextView = itemView.findViewById(R.id.layerCodeTextView);
    }

    public void bind(Layer layer)  {
        codeTextView.setText(layer.getCode());
        layerNameTextView.setText(layer.getLayername());

        int marker_id = layer.getLayer_id();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = itemView.getContext();
                ((MainActivity) context).onLayerListItemClicked(layer);
            }
        });
    }

    static LayerViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layer_item, parent, false);
        return new LayerViewHolder(view);
    }
}
