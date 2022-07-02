package com.alexsykes.mapmonster;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.activities.MainActivity;
import com.alexsykes.mapmonster.data.Layer;

public class LayerViewHolder extends RecyclerView.ViewHolder{
//    final TextView layerNameTextView, codeTextView;
    CheckBox layerVisibilityCheckBox;
    public LayerViewHolder(@NonNull View itemView) {
        super(itemView);
//        layerNameTextView = itemView.findViewById(R.id.layerNameTextView);
//        codeTextView = itemView.findViewById(R.id.layerCodeTextView);
        layerVisibilityCheckBox = itemView.findViewById(R.id.layerCheckBox);
    }

    public void bind(Layer layer)  {
//        codeTextView.setText(layer.getCode());
//        layerNameTextView.setText(layer.getLayername());
        int marker_id = layer.getLayer_id();
        boolean isChecked = layer.isVisible();
        layerVisibilityCheckBox.setText(layer.getLayername());
        layerVisibilityCheckBox.setChecked(isChecked);
//
//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Log.i("Info", "onClick: ");
//                Context context = itemView.getContext();
//                ((MainActivity) context).onLayerListItemClicked(layer);
//            }
//        });

        layerVisibilityCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i("Info", "onCheckedChanged: " + layer.getLayername());
                Context context = itemView.getContext();
                ((MainActivity) context).onLayerListItemCheckedChanged(layer, isChecked);
            }
        });
    }

    static LayerViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layer_item, parent, false);
        return new LayerViewHolder(view);
    }
}
