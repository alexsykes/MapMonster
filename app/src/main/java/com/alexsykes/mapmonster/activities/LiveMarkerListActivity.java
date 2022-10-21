package com.alexsykes.mapmonster.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.LiveMarkerListAdapter;
import com.alexsykes.mapmonster.data.MMarker;
import com.alexsykes.mapmonster.data.MarkerViewModel;

public class LiveMarkerListActivity extends AppCompatActivity {
    public static final String TAG = "Info";
    MarkerViewModel markerViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_marker_list);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        final LiveMarkerListAdapter adapter = new LiveMarkerListAdapter(new LiveMarkerListAdapter.MMarkerDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);
        markerViewModel.getLiveMarkerData().observe(this, mMarkers -> {
            adapter.submitList(mMarkers);
        });
    }

    public void onItemClicked(MMarker marker) {

        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);
        markerViewModel.selected(marker.getMarker_id(), !marker.isSelected());
        markerViewModel.setVisibility(marker.getMarker_id(), !marker.isVisible());
    }
}