package com.alexsykes.mapmonster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ParentViewHolder extends RecyclerView.ViewHolder {
    final TextView sectionTitleTextView;

    public ParentViewHolder(@NonNull View itemView) {
        super(itemView);
        sectionTitleTextView = itemView.findViewById(R.id.sectionHeaderTextView);
    }

    static ParentViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.section_header, parent, false);
        return new ParentViewHolder(view);
    }

    public void bind(String sectionName) {
        sectionTitleTextView.setText(sectionName);
    }
}
