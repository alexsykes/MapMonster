package com.alexsykes.mapmonster.activities;
// See - https://www.youtube.com/watch?v=x5afKIu0JmY

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.SectionListAdapter;
import com.alexsykes.mapmonster.data.MMDatabase;
import com.alexsykes.mapmonster.data.MMarker;
import com.alexsykes.mapmonster.data.MarkerDao;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Map;

public class MarkerListActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG = "Info";

    private static final int DEFAULT_ZOOM = 12;
    private MarkerDao markerDao;
    public int numMarkers;
    public List<MMarker> markerList;
    private List sectionList;
    RecyclerView sectionListRV;
    private Map<String, List<MMarker>> markerMap;
    private String sectionName;
    private int numSections;
    SharedPreferences defaults;
    SharedPreferences.Editor editor;
    private GoogleMap mMap;
    private FloatingActionButton addMarkerButton;
    private Location lastKnownLocation;
    private LatLng defaultLocation = new LatLng(53.59,-2.56);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_list);
        setupUI();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        MMDatabase db = MMDatabase.getDatabase(this);
        markerDao = db.markerDao();
        markerMap = markerDao.getMarkersByLayer();

        // Main recycerView
        sectionListRV = findViewById(R.id.sectionListRecyclerView);
        final SectionListAdapter layerListAdapter = new SectionListAdapter(markerMap);
        sectionListRV.setAdapter(layerListAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        // getSavedCameraPosition();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: MarkerListActivity");
        saveCameraPosition();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: MarkerListActivity");
    }

    private void setupUI() {
        addMarkerButton = findViewById(R.id.addMarkerButton);
        addMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: newMarker");
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        CameraPosition cameraPosition = getSavedCameraPosition();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    void saveCameraPosition() {
        defaults = this.getPreferences(Context.MODE_PRIVATE);
        editor = defaults.edit();
        CameraPosition mMyCam = mMap.getCameraPosition();
        double longitude = mMyCam.target.longitude;
        double latitude = mMyCam.target.latitude;
        float zoom = mMyCam.zoom;

        editor.putFloat("longitude", (float) longitude);
        editor.putFloat("latitude", (float) latitude);
        editor.putFloat("zoom", zoom);
        editor.apply();
    }

    CameraPosition getSavedCameraPosition() {
        defaults = this.getPreferences(Context.MODE_PRIVATE);

        // "initial longitude" is only used on first startup
        double longitude = defaults.getFloat("longitude", (float) defaultLocation.longitude);
        double latitude = defaults.getFloat("latitude", (float) defaultLocation.latitude);
        float zoom = defaults.getFloat("zoom", DEFAULT_ZOOM);
        LatLng startPosition = new LatLng(latitude, longitude);


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(startPosition)      // Sets the center of the map to Mountain View
                .zoom(zoom)
                .build();                   // Creates a CameraPosition from the builder

        return cameraPosition;
    }

    public void onMarkerListItemClicked(MMarker marker, int isVisible) {
        LatLng loc = new LatLng(marker.getLatitude(), marker.getLongitude());
        Log.i(TAG, "onMarkerListItemClicked: " + isVisible);
    }
}