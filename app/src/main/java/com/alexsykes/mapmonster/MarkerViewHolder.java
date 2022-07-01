package com.alexsykes.mapmonster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.activities.MainActivity;
import com.alexsykes.mapmonster.data.Marker;

public class MarkerViewHolder extends RecyclerView.ViewHolder {
    final TextView markerTextView, codeTextView;


    private MarkerViewHolder(View itemView)  {
        super(itemView);
        markerTextView = itemView.findViewById(R.id.markerTextView);
        codeTextView = itemView.findViewById(R.id.codeTextView);
    }

    public void bind(Marker marker) {
        codeTextView.setText(marker.getCode());
        markerTextView.setText(marker.getPlacename());

        int marker_id = marker.getMarker_id();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = itemView.getContext();
                ((MainActivity) context).onMarkerListItemClicked(marker);
            }
        });
    }

    static MarkerViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.marker_item, parent, false);
        return new MarkerViewHolder(view);
    }
}
