package com.alexsykes.mapmonster.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.LayerViewModel;
import com.alexsykes.mapmonster.data.MMDatabase;
import com.alexsykes.mapmonster.data.MarkerDao;
import com.alexsykes.mapmonster.data.MarkerViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;

public class MarkerListActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG = "Info";
    private List<Integer> listLiveData;
    private MarkerViewModel markerViewModel;
    private LayerViewModel layerViewModel;
    private MarkerDao markerDao;
    private List<Integer> markerList;

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
        Log.i(TAG, "onCreate: ");
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }
}