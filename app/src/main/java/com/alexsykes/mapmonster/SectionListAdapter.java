package com.alexsykes.mapmonster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.data.MMarker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SectionListAdapter extends RecyclerView.Adapter<SectionViewHolder> {
    Set<String> keySet;
    String[] sections;
    Map<String, List<MMarker>> map;
    List<MMarker> markerList;

    // Multiple constructors with arguments defined
    public SectionListAdapter(Set<String> keySet) {
        this.keySet = keySet;
        sections = keySet.toArray(new String[keySet.size()]);
    }

    public SectionListAdapter(Map<String, List<MMarker>> map) {
        this.map = map;
        this.keySet = map.keySet();
        sections = keySet.toArray(new String[keySet.size()]);
        markerList = new ArrayList(map.values());
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.section_header, parent, false);
        return new SectionViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
//        MMarker marker = markerList.get(position);
        String section = sections[position];
        List<MMarker> markers =  map.get(section);
        holder.sectionTitleTextView.setText(sections[position]);
        holder.markerList = markers;
    }

    @Override
    public int getItemCount() {
        return keySet.size();
    }
}
