package com.alexsykes.mapmonster.activities;
// See - https://www.youtube.com/watch?v=x5afKIu0JmY

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.SectionListAdapter;
import com.alexsykes.mapmonster.data.MMDatabase;
import com.alexsykes.mapmonster.data.MMarker;
import com.alexsykes.mapmonster.data.MarkerDao;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;
import java.util.Map;

public class MarkerListActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG = "Info";

    private MarkerDao markerDao;
    public int numMarkers;
    public List<MMarker> markerList;
    private List sectionList;
    RecyclerView sectionListRV;
    private Map<String, List<MMarker>> markerMap;
    private String sectionName;
    private int numSections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_list);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        MMDatabase db = MMDatabase.getDatabase(this);
        markerDao = db.markerDao();
        markerMap = markerDao.getMarkersByLayer();

        sectionListRV = findViewById(R.id.sectionListRecyclerView);

        // Pass marker data into adapter
        final SectionListAdapter layerListAdapter = new SectionListAdapter(markerMap);
        sectionListRV.setAdapter(layerListAdapter);
        sectionListRV.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }
}