package com.alexsykes.mapmonster.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.R;

public class LiveMarkerListViewHolder extends RecyclerView.ViewHolder {
    private final TextView markerNameItemView;

    private LiveMarkerListViewHolder(View itemView) {
        super(itemView);
        markerNameItemView = itemView.findViewById(R.id.markerNameItemView);
    }

    public void bind(String text) {
        markerNameItemView.setText(text);
    }

    public static LiveMarkerListViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.live_data_item, parent, false);
        return new LiveMarkerListViewHolder(view);
    }
}
