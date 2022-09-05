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

import com.alexsykes.mapmonster.activities.MapEditActivity;
import com.alexsykes.mapmonster.data.MMarker;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SectionListAdapter extends RecyclerView.Adapter<SectionListAdapter.ViewHolder> {
    Set<String> keySet;
    String[] sections;
    Map<String, List<MMarker>> map;
    List<MMarker> markerList;
    ArrayList<String> visibleLayers;
    public static final String TAG = "Info";

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.section_header,parent,false);
        return new ViewHolder(view);
    }

    public SectionListAdapter(Map<String, List<MMarker>> map, ArrayList<String> visibleLayers) {
        this.map = map;
        this.keySet = map.keySet();
        this.visibleLayers = new ArrayList<>(visibleLayers);
        sections = keySet.toArray(new String[keySet.size()]);
        markerList = new ArrayList(map.values());
    }

    @Override
    public int getItemCount() {
        return map.size(); // Number of sections
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView sectionHeaderTextView;
        SwitchMaterial sectionSwitch;
        RecyclerView markerItemsRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionSwitch = itemView.findViewById(R.id.sectionSwitch);
            markerItemsRecyclerView = itemView.findViewById(R.id.markerItemsRecyclerView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String section = sections[position];
        markerList = map.get(section);

        Context context = holder.itemView.getContext();
        holder.sectionSwitch.setText(section);

        if(visibleLayers.contains(section)){
            holder.sectionSwitch.setChecked(true);
            holder.markerItemsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            holder.sectionSwitch.setChecked(false);
            holder.markerItemsRecyclerView.setVisibility(View.GONE);
        }
        holder.sectionSwitch.setOnClickListener(new View.OnClickListener() {

            // Toggle marker list visibility on click
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onBindViewHolder - onClick: ");
                ((MapEditActivity) context).onLayerListItemClicked(section, holder.markerItemsRecyclerView.getVisibility());
//                ((MarkerListActivity) context).onLayerListItemClicked(section, holder.sectionSwitch.isChecked());
                if(holder.markerItemsRecyclerView.getVisibility() == View.VISIBLE) {
                    holder.markerItemsRecyclerView.setVisibility(View.GONE);
                } else {
                    holder.markerItemsRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        ChildMarkerListAdapter childMarkerListAdapter = new ChildMarkerListAdapter(markerList);
        holder.markerItemsRecyclerView.setAdapter(childMarkerListAdapter);
        holder.markerItemsRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
    }
}
