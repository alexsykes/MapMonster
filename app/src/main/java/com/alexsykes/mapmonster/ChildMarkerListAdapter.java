package com.alexsykes.mapmonster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.data.MMarker;

import java.util.List;

public class ChildMarkerListAdapter extends RecyclerView.Adapter<ChildMarkerListAdapter.ViewHolder> {
    private List<MMarker> markers;

    public ChildMarkerListAdapter(List<MMarker> markers) {
        this.markers = markers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_marker_list_item, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.childMarkerNameTextView.setText("Name");
    }

    @Override
    public int getItemCount() {
        return markers.size();
//        return markers.size();
    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView childMarkerNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            childMarkerNameTextView = itemView.findViewById(R.id.childMarkerNameTextView);
        }
    }
}
