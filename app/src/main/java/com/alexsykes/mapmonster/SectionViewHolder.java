package com.alexsykes.mapmonster;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.data.MMarker;

import java.util.List;

public class SectionViewHolder extends RecyclerView.ViewHolder {
    final TextView sectionTitleTextView;
    RecyclerView markerRV;
    List<MMarker> markerList;

    public SectionViewHolder(@NonNull View itemView) {
        super(itemView);
        sectionTitleTextView = itemView.findViewById(R.id.sectionHeaderTextView);

        markerRV = itemView.findViewById(R.id.markerItemsRecyclerView);
        markerRV.setAdapter(new ChildMarkerListAdapter(markerList));
        markerRV.setLayoutManager(new LinearLayoutManager(markerRV.getContext()));

    }

//    public void setMarkerList(List<MMarker> markerList){
//        this.markerList = markerList;
//    }
}
