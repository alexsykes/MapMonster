package com.alexsykes.mapmonster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Transaction;

public class MarkerViewHolder extends RecyclerView.ViewHolder {
    final TextView markerTextView;


    private MarkerViewHolder(View itemView)  {
        super(itemView);
        markerTextView = itemView.findViewById(R.id.markerTextView);
    }

    public void bind(String text) {
        markerTextView.setText(text);
    }

    static MarkerViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.marker_item, parent, false);
        return new MarkerViewHolder(view);
    }
}
