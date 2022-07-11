package com.alexsykes.mapmonster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Set;

public class SectionListAdapter extends RecyclerView.Adapter<SectionViewHolder> {
    Set<String> keySet;
    String[] sections;

    public SectionListAdapter(Set<String> keySet) {
        this.keySet = keySet;
        sections = keySet.toArray(new String[keySet.size()]);
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
        holder.sectionTitleTextView.setText(sections[position]);

    }

    @Override
    public int getItemCount() {
        return keySet.size();
    }
}
