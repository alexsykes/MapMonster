package com.alexsykes.mapmonster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.data.Layer;

public class LayerViewHolder extends RecyclerView.ViewHolder{
//    final TextView layerNameTextView, codeTextView;
    CheckBox layerVisibilityCheckBox;
    public LayerViewHolder(@NonNull View itemView) {
        super(itemView);
        layerVisibilityCheckBox = itemView.findViewById(R.id.layerCheckBox);
    }

    public void bind(Layer layer)  {
        int marker_id = layer.getLayer_id();
        boolean isChecked = layer.isVisible();
        layerVisibilityCheckBox.setText(layer.getLayername());
        layerVisibilityCheckBox.setChecked(isChecked);
    }

    static LayerViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layer_item, parent, false);
        return new LayerViewHolder(view);
    }
}
