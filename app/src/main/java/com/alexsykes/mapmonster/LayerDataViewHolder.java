package com.alexsykes.mapmonster;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public class LayerDataViewHolder extends ViewHolder {
    final TextView layerNameTextView;

    public LayerDataViewHolder(@NonNull View itemView) {
        super(itemView);
        layerNameTextView = itemView.findViewById(R.id.layerNameTextView);
    }


}
