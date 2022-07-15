package com.alexsykes.mapmonster;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.activities.MarkerListActivity;
import com.alexsykes.mapmonster.data.MMarker;

import java.text.DecimalFormat;
import java.util.List;

public class ChildMarkerListAdapter extends RecyclerView.Adapter<ChildMarkerListAdapter.ViewHolder> {
    private List<MMarker> markers;
    public static final String TAG = "Info";

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
        MMarker marker = markers.get(position);

        final DecimalFormat df = new DecimalFormat("#.00000");
        String latStr = df.format(marker.getLatitude());
        String lngStr = df.format(marker.getLongitude());

        holder.childMarkerNameTextView.setText(marker.getPlacename());
        holder.codeTextView.setText(marker.getCode());
        holder.latTextView.setText(latStr);
        holder.lngTextView.setText(lngStr);

        holder.childMarkerNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int isVisible = holder.detailContainer.getVisibility();
                Log.i(TAG, "onClick: " + marker.getMarker_id());
                Context context = holder.childMarkerNameTextView.getContext();

                if(isVisible == View.VISIBLE) {
                    holder.detailContainer.setVisibility(View.GONE);
                } else {
                    holder.detailContainer.setVisibility(View.VISIBLE);
                                    }
                isVisible = holder.detailContainer.getVisibility();
                ((MarkerListActivity) context).onMarkerListItemClicked(marker, isVisible);
            }
        });
    }

    @Override
    public int getItemCount() {
 //       return 1;
        return markers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView childMarkerNameTextView, codeTextView, latTextView, lngTextView;
        private LinearLayout detailContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            childMarkerNameTextView = itemView.findViewById(R.id.childMarkerNameTextView);
            codeTextView = itemView.findViewById(R.id.codeTextView);
            latTextView = itemView.findViewById(R.id.latTextView);
            lngTextView = itemView.findViewById(R.id.lngTextView);
            detailContainer = itemView.findViewById(R.id.detailContainer);
        }
    }
}
