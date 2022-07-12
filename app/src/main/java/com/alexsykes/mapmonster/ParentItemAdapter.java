package com.alexsykes.mapmonster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Set;

public class ParentItemAdapter extends RecyclerView.Adapter<ParentViewHolder> {
    Set<String> keySet;
    String[] sections;

    public ParentItemAdapter(Set<String> keySet) {
        this.keySet = keySet;
        sections = keySet.toArray(new String[keySet.size()]);
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.section_header, parent, false);

        return new ParentViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder holder, int position) {
        holder.sectionTitleTextView.setText(sections[position]);

    }

    @Override
    public int getItemCount() {
        return keySet.size();
    }
}
