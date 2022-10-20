package com.alexsykes.mapmonster.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.Icon;
import com.alexsykes.mapmonster.data.LiveMarkerListAdapter;
import com.alexsykes.mapmonster.data.MMDatabase;
import com.alexsykes.mapmonster.data.MMarker;
import com.alexsykes.mapmonster.data.MapMarkerDataItem;
import com.alexsykes.mapmonster.data.MarkerViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class LiveMarkerListActivity extends AppCompatActivity {
    public static final String TAG = "Info";
    MarkerViewModel markerViewModel;
    LiveData<List<MMarker>> liveData;
    List<MMarker> mapMarkerDataItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_marker_list);
//        getData();

        FloatingActionButton button = findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerViewModel.insert(new MMarker(1, 1, "aaa", "aaa", 1, "Hello"));
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        final LiveMarkerListAdapter adapter = new LiveMarkerListAdapter(new LiveMarkerListAdapter.MMarkerDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);
        markerViewModel.getLiveMarkerList().observe(this, mMarkers -> {
            adapter.submitList(mMarkers);
        });
    }


    private void getData() {
        // Get data
        MMDatabase db = MMDatabase.getDatabase(this);
        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);

        liveData = markerViewModel.getLiveMarkerList();
        mapMarkerDataItems = liveData.getValue();

    }
}