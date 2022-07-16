package com.alexsykes.mapmonster;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.activities.MarkerListActivity;
import com.alexsykes.mapmonster.data.MMarker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SectionListAdapter extends RecyclerView.Adapter<SectionListAdapter.ViewHolder> {
    Set<String> keySet;
    String[] sections;
    Map<String, List<MMarker>> map;
    List<MMarker> markerList;
    public static final String TAG = "Info";

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.section_header,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String section = sections[position];
        markerList = map.get(section);

        Context context = holder.itemView.getContext();
        holder.sectionHeaderTextView.setText(section);
        holder.sectionHeaderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: ");
                if(holder.markerItemsRecyclerView.getVisibility() == View.VISIBLE) {
                    holder.markerItemsRecyclerView.setVisibility(View.GONE);
                } else {
                    holder.markerItemsRecyclerView.setVisibility(View.VISIBLE);
                    ((MarkerListActivity) context).onLayerListItemClicked(section);
                }
//                Context context = itemView.getContext();
            }
        });

        ChildMarkerListAdapter childMarkerListAdapter = new ChildMarkerListAdapter(markerList);
        holder.markerItemsRecyclerView.setAdapter(childMarkerListAdapter);
        holder.markerItemsRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
    }

    @Override
    public int getItemCount() {
        return map.size(); // Number of sections
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView sectionHeaderTextView;
        RecyclerView markerItemsRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionHeaderTextView = itemView.findViewById(R.id.sectionHeaderTextView);
            markerItemsRecyclerView = itemView.findViewById(R.id.markerItemsRecyclerView);
        }
    }

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
}
