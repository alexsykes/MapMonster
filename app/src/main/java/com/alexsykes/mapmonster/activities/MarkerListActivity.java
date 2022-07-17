package com.alexsykes.mapmonster.activities;
// See - https://www.youtube.com/watch?v=x5afKIu0JmY

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MarkerListActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG = "Info";

    private static final int DEFAULT_ZOOM = 12;
    private MarkerDao markerDao;
    public List<MMarker> visibleMarkerList;
    RecyclerView sectionListRV;
    private ArrayList<String> visibleLayers;
    private TextView showAllMarkersButton;
    private Map<String, List<MMarker>> markerMap;
    SharedPreferences defaults;
    SharedPreferences.Editor editor;
    private GoogleMap mMap;
    private FloatingActionButton addMarkerButton;
    private LatLng defaultLocation = new LatLng(53.59,-2.56);
    private boolean mapIsReady, markersAreWaiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_list);
        defaults = this.getPreferences(Context.MODE_PRIVATE);
        mapIsReady = false;
        markersAreWaiting = false;
        visibleLayers = new ArrayList<String>();

        setupUI();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        MMDatabase db = MMDatabase.getDatabase(this);
        markerDao = db.markerDao();
        markerMap = markerDao.getMarkersByLayer();

        // Main recyclerView
        sectionListRV = findViewById(R.id.sectionListRecyclerView);
        final SectionListAdapter layerListAdapter = new SectionListAdapter(markerMap);
        sectionListRV.setAdapter(layerListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        // getSavedCameraPosition();
//        defaults = this.getPreferences(Context.MODE_PRIVATE);
//        Set<String> set = defaults.getStringSet("visibleLayers", null);
//        visibleLayers = new ArrayList<String>(set);
//        visibleMarkerList = markerDao.getVisibleMarkerList(visibleLayers);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: MarkerListActivity");
        saveCameraPosition();

        Set<String> set = new HashSet<String>();
        set.addAll(visibleLayers);
        editor.putStringSet("visibleLayers", set);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: MarkerListActivity");
    }

    public void onLayerListItemClicked(String layerName, int visibility){
        if(visibility == 8) {
            visibleLayers.add(layerName);
        } else {
            visibleLayers.remove(visibleLayers.indexOf(layerName));
        }
        visibleMarkerList = markerDao.getVisibleMarkerList(visibleLayers);
        addMarkers(visibleMarkerList);
    }

    private void setupUI() {
        showAllMarkersButton = findViewById(R.id.showAllMarkers);
        showAllMarkersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: showAllMarkers");
                showAllMarkers();
            }
        });
        addMarkerButton = findViewById(R.id.addMarkerButton);
        addMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: newMarker");
            }
        });
    }

    private void showAllMarkers() {
        // Get database then markerList
        MMDatabase db = MMDatabase.getDatabase(this);
        markerDao = db.markerDao();
        visibleMarkerList = markerDao.getMarkerList();
        addMarkers(visibleMarkerList);
    }

    private void addMarkers(List<MMarker> markerList) {
        // Clear map of current markers, return if markerList is empty
        mMap.clear();
        if(markerList.isEmpty()) {
            return;
        }

        String code, type, snippet, marker_title;

        // Setup Bounds builder
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        int padding = 100;
        LatLng latLng;

        // Iterate through markerList
        for(int i = 0; i < markerList.size(); i++ ) {
            MMarker marker = markerList.get(i);

            // Get data set
            latLng = new LatLng(marker.getLatitude(), marker.getLongitude());
            code = marker.getCode();
            type = marker.getType();
            snippet = marker.getSnippet();
            marker_title = marker.getPlacename();

            // Create new marker
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(marker_title)
                    .snippet(snippet)
                    .draggable(true)
                    .visible(true);

            // with custom icons
            switch(type)  {
                case "Accommodation" :
                    markerOptions.icon(BitmapFromVector(getApplicationContext(), R.drawable.hotel_36));
                    break;
                case "Fuel" :
                    markerOptions.icon(BitmapFromVector(getApplicationContext(), R.drawable.fuel_36));
                    break;
                case "Waypoint" :
                    // markerOptions.icon(BitmapFromVector(getApplicationContext(), R.drawable.home_48));
                    break;
                case "Food" :
                    markerOptions.icon(BitmapFromVector(getApplicationContext(), R.drawable.food_36));
                    break;
                case "Parking" :
                    markerOptions.icon(BitmapFromVector(getApplicationContext(), R.drawable.parking_36));
                    break;
                default:
                    break;
            }

            // Include in vounds builder
            builder.include(latLng);

            // Add to map
            Marker marker1 = mMap.addMarker(markerOptions);
            // Set marker tag for editing
            marker1.setTag(marker.getMarker_id());
        }

        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }

//    private void showLayerMarkers(String layerName){
//        markerList = markerMap.get(layerName);
//        addMarkers(markerList);
//    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mapIsReady = true;
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

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        vectorDrawable.setBounds(0,0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);

        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}