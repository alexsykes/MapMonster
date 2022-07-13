package com.alexsykes.mapmonster;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.data.MMarker;

import java.util.List;
import java.util.Map;

public class SectionViewHolder extends RecyclerView.ViewHolder {
    final TextView sectionTitleTextView;
    RecyclerView markerRV;
    List<MMarker> map;

    public SectionViewHolder(@NonNull View itemView,
                             Map<String, List<MMarker>> map) {
        super(itemView);
        sectionTitleTextView = itemView.findViewById(R.id.sectionHeaderTextView);
        markerRV = itemView.findViewById(R.id.markerItemsRecyclerView);
    }

//    static SectionViewHolder create(ViewGroup parent) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.section_header, parent, false);
//
//        return new SectionViewHolder(view);
//    }

//    public void bind(String sectionName) {
//        sectionTitleTextView.setText(sectionName);
////        final MarkerDetailListAdapter markerDetailListAdapter = new MarkerDetailListAdapter();
////        markerRV.setLayoutManager();
////        markerRV.setAdapter();
//    }
}
