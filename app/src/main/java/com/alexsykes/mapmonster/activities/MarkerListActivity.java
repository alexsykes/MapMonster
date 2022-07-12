package com.alexsykes.mapmonster.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.ParentItemAdapter;
import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.LayerViewModel;
import com.alexsykes.mapmonster.data.MMDatabase;
import com.alexsykes.mapmonster.data.MMarker;
import com.alexsykes.mapmonster.data.MarkerDao;
import com.alexsykes.mapmonster.data.MarkerViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MarkerListActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG = "Info";
    private List<Integer> listLiveData;
    private MarkerViewModel markerViewModel;
    private LayerViewModel layerViewModel;
    private MarkerDao markerDao;
    public int numMarkers;
    public List<MMarker> markerList;
    RecyclerView sectionListRV;
    private Map<String, List<MMarker>> map;
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
        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);
        layerViewModel = new ViewModelProvider(this).get(LayerViewModel.class);
        markerDao = db.markerDao();
        listLiveData = markerDao.allMarkersByLayer();
        map = markerDao.getMarkersByLayer();
        numSections = map.size();
        Set<String> sections = map.keySet();
        Log.i(TAG, "onCreate: " + numSections);

        for (String key : map.keySet()) {
            sectionName = key;
            numMarkers = map.get(key).size();
            Log.i(TAG, "Section: " + sectionName + " - " + numMarkers + " items");
        }


        sectionListRV = findViewById(R.id.sectionListRecyclerView);
        final ParentItemAdapter layerListAdapter = new ParentItemAdapter(map.keySet());
        sectionListRV.setAdapter(layerListAdapter);
        sectionListRV.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }
}