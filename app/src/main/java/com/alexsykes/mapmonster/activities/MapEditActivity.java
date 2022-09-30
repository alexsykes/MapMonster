package com.alexsykes.mapmonster.activities;
// See - https://www.youtube.com/watch?v=x5afKIu0JmY

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.SectionListAdapter;
import com.alexsykes.mapmonster.data.LayerViewModel;
import com.alexsykes.mapmonster.data.MMDatabase;
import com.alexsykes.mapmonster.data.MMarker;
import com.alexsykes.mapmonster.data.MarkerDao;
import com.alexsykes.mapmonster.data.MarkerViewModel;
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
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapEditActivity extends AppCompatActivity implements OnMapReadyCallback, MarkerDetailFragment.MarkerDetailFragmentListener {
    public static final String TAG = "Info";

    private static final int DEFAULT_ZOOM = 12;
    TextView markerIdTextView, markerLatTextView, markerLngTextView;
    EditText markerNameEditText, markerNotesEditText, markerCodeEditText;
    Button saveButton, cancelButton;
    FloatingActionButton addMarkerButton;
    SwitchMaterial showAllLayerListSwitch;
    RecyclerView sectionListRV;
    SharedPreferences.Editor editor;
    SharedPreferences preferences;
    private GoogleMap mMap;
    private final LatLng defaultLocation = new LatLng(53.59,-2.56);
    LinearLayout layerPanelLinearLayout, markerInfoPanel;
    MarkerDetailFragment markerDetailFragment;
    private LatLng currentLocation;
    private Marker currentMarker;
    private MarkerViewModel markerViewModel;
    private LayerViewModel layerViewModel;
    private ArrayList<String> visibleLayers ; // ArrayList of currently visible layers
    private List<MMarker> visibleMarkerList; // List of currently visible markers
    Map<String, List<MMarker>> markerMap; // Map of all Markers
    private boolean compassEnabled, mapToolbarEnabled, zoomControlsEnabled;
    // --Commented out by Inspection (20/08/2022, 09:41):MarkerManager markerManager;
    // --Commented out by Inspection (20/08/2022, 09:41):ArrayList<MarkerManager.Collection> layerList;
    MarkerDao markerDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_edit);

        getPreferences();   // Get saved values
        getData();          // and saved data
        setupUI();          //

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        // Main recyclerView - show list/sublist of layers/markers
        sectionListRV = findViewById(R.id.sectionListRecyclerView);
        final SectionListAdapter layerListAdapter = new SectionListAdapter(markerMap, visibleLayers);
        sectionListRV.setAdapter(layerListAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveCameraPosition();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: MarkerListActivity");
        // Get list of visible layers
//        visibleMarkerList = getVisibleMarkers();
    }

    @Override
    public void onReturn(Editable name, Editable code, Editable markerNotes, String layer) {
        mMap.clear();
        Log.i(TAG, "Before getData: " + visibleMarkerList.size());
        Log.i(TAG, "onReturn: ");
        LatLng curLocation = mMap.getCameraPosition().target;
        MMarker mMarker = new MMarker(curLocation.latitude, curLocation.longitude, name.toString(),code.toString(),2, markerNotes.toString());
        markerViewModel.insert(mMarker);
        Log.i(TAG, "After insert getData: " + visibleMarkerList.size());
        getData();
        Log.i(TAG, "After getData: " + visibleMarkerList.size());
        addMarkers(visibleMarkerList);
        CameraPosition cameraPosition = new CameraPosition(curLocation,18,0,0);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(zoomControlsEnabled);
        mMap.getUiSettings().setMapToolbarEnabled(mapToolbarEnabled);
        mMap.getUiSettings().setCompassEnabled(compassEnabled);

        // Drag listener
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            final DecimalFormat df = new DecimalFormat("#.#####");
            String latStr, lngStr;
            @Override
            public void onMarkerDrag(@NonNull Marker marker) {
//                Log.i(TAG, "onMarkerDrag: ");
                currentLocation = marker.getPosition();
                latStr = df.format(currentLocation.latitude);
                lngStr = df.format(currentLocation.longitude);
                markerLatTextView.setText(latStr);
                markerLngTextView.setText(lngStr);
            }

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
//                Log.i(TAG, "onMarkerDragEnd: ");
                currentMarker = marker;
            }

            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {
                currentLocation = marker.getPosition();
//                Log.i(TAG, "onMarkerDragStart: " + marker.getTag());
                latStr = df.format(currentLocation.latitude);

                int markerId = (Integer) marker.getTag();
                MMarker currentMarker = markerViewModel.getMarker(markerId);
                latStr = df.format(currentLocation.latitude);
                lngStr = df.format(currentLocation.longitude);
                markerInfoPanel.setVisibility(View.VISIBLE);
                layerPanelLinearLayout.setVisibility(View.GONE);
                addMarkerButton.setVisibility(View.GONE);

                markerIdTextView.setText(new StringBuilder().append(getString(R.string.marker_id)).append(currentMarker.getMarker_id()).toString());
                markerLatTextView.setText(latStr);
                markerLngTextView.setText(lngStr);

                markerCodeEditText.setText(currentMarker.getCode());
                markerNameEditText.setText(currentMarker.getPlacename());
                markerNotesEditText.setText(currentMarker.getNotes());
            }
        });

        CameraPosition cameraPosition = getSavedCameraPosition();
        addMarkers(visibleMarkerList);
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    // Navigation
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_edit_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.layer_list_item:
                goLayerList();
                return true;

            case R.id.settings_menu_item:
                return true;

            case R.id.go_layer_list:
                toggleLayerPanel();
                return true;

            case R.id.marker_list_menu_item:
                goMarkerList();
                return true;
            default:
        }

        return false;
    }

    private void goMarkerList() {
        Intent intent = new Intent(MapEditActivity.this,MarkerListActivity.class);
        startActivity(intent);
    }

    private void goLayerList() {
        Intent intent = new Intent(MapEditActivity.this,LayerListActivity.class);
        startActivity(intent);
    }

    public void onMarkerListItemClicked(MMarker marker, int isVisible) {
        LatLng loc = new LatLng(marker.getLatitude(), marker.getLongitude());
        Log.i(TAG, "onMarkerListItemClicked: " + isVisible);
    }

//  Utility methods
    private void getPreferences() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String maptype = preferences.getString("map_view_type","NORMAL");

        zoomControlsEnabled = preferences.getBoolean("zoomControlsEnabled", true);
        mapToolbarEnabled = preferences.getBoolean("mapToolbarEnabled", true);
        compassEnabled = preferences.getBoolean("compassEnabled", true);
    }

    private void getData() {
        MMDatabase db = MMDatabase.getDatabase(this);
        markerDao = db.markerDao();
        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);
        layerViewModel = new ViewModelProvider(this).get(LayerViewModel.class);
        // Get all markers / layers for menu
        markerMap = markerViewModel.getMarkersByLayer();
        visibleLayers = new ArrayList<>(layerViewModel.getVisibleLayerList());
        visibleMarkerList = markerDao.getVisibleMarkerList(visibleLayers);
    }

    private void toggleLayerPanel() {
        if (sectionListRV.getVisibility() == View.VISIBLE) {
            sectionListRV.setVisibility(View.GONE);
            mMap.setPadding(0,0,0,0); }
        else {
            sectionListRV.setVisibility(View.VISIBLE);
            mMap.setPadding(0,0,100,0);
        }
    }

    public void onLayerListItemClicked(String layerName, int visibility){
        Log.i(TAG, "onLayerListItemClicked: " + layerName + visibility);
        if(visibility == 8) {
            visibleLayers.add(layerName);
            layerViewModel.setVisibility(layerName, true);
        } else {
            visibleLayers.remove(layerName);
            layerViewModel.setVisibility(layerName, false);
        }
        visibleMarkerList = markerViewModel.getVisibleMarkerList(visibleLayers);
        addMarkers(visibleMarkerList);
    }

    private void setupUI() {
        layerPanelLinearLayout = findViewById(R.id.layerPanelLinearLayout);
        markerInfoPanel = findViewById(R.id.markerInfoPanel);
        markerIdTextView = findViewById(R.id.markerIdTextView);
        markerLatTextView = findViewById(R.id.markerLatTextView);
        markerLngTextView = findViewById(R.id.markerLngTextView);
        markerNameEditText = findViewById(R.id.markerNameEditText);
        markerNotesEditText = findViewById(R.id.markerNotesEditText);
        markerCodeEditText =  findViewById(R.id.markerCodeEditText);

        markerInfoPanel.setVisibility(View.GONE);

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            layerPanelLinearLayout.setVisibility(View.VISIBLE);
            markerInfoPanel.setVisibility(View.GONE);
            addMarkerButton.setVisibility(View.GONE);

            int markerId = (Integer) currentMarker.getTag();

            String markerName = markerNameEditText.getText().toString();
            String markerCode = markerCodeEditText.getText().toString();
            String markerNotes = markerNotesEditText.getText().toString();

            double lat = currentMarker.getPosition().latitude;
            double lng = currentMarker.getPosition().longitude;

            markerViewModel.updateMarker(markerId, markerCode, markerNotes, markerName, lat, lng);
            redrawMarkers();

        });

        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> {
            layerPanelLinearLayout.setVisibility(View.VISIBLE);
            markerInfoPanel.setVisibility(View.GONE);
            addMarkerButton.setVisibility(View.VISIBLE);
            redrawMarkers();
        });


        showAllLayerListSwitch = findViewById(R.id.showLayerList);
        showAllLayerListSwitch.setChecked(true);
        showAllLayerListSwitch.setOnClickListener(v -> {
            Log.i(TAG, "onClick: showAllMarkers");
            boolean setVisible = showAllLayerListSwitch.isChecked();
            showAllLayers(setVisible);
        });

        addMarkerButton = findViewById(R.id.addMarkerButton);
        addMarkerButton.setOnClickListener(v -> {
            Log.i(TAG, "onClick: New Marker;");
            addNewMarker();
        });
    }

    private void addNewMarker() {
        // Get current marker location
        LatLng loc = mMap.getCameraPosition().target;
        double lat = loc.latitude;
        double lng = loc.longitude;

        showEditDialog(lat, lng);

    }

    private void showAllLayers(boolean isVisible) {
        sectionListRV = findViewById(R.id.sectionListRecyclerView);
        if (isVisible) {
            sectionListRV.setVisibility(View.VISIBLE);
        } else {
            sectionListRV.setVisibility(View.GONE);
        }
    }

    private void addMarkers(List<MMarker> markerList) {
        // Clear map of current markers, return if markerList is empty
        mMap.clear();
        if(markerList.isEmpty()) {
            return;
        }

        String type, snippet, marker_title;
        int layer_id;

        // Setup Bounds builder
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        int padding = 100;
        LatLng latLng;

        // Iterate through markerList
        for(int i = 0; i < markerList.size(); i++ ) {
            MMarker marker = visibleMarkerList.get(i);

            // Get data set
            latLng = new LatLng(marker.getLatitude(), marker.getLongitude());
            layer_id = marker.getLayer_id();
            snippet = marker.getNotes();
            marker_title = marker.getPlacename();

            // Create new marker
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(marker_title)
                    .snippet(snippet)
                    .draggable(true)
                    .visible(true);

            // with custom icons
            switch(layer_id)  {
                case 0 :
                    markerOptions.icon(BitmapFromVector(getApplicationContext(), R.drawable.hotel_24));
                    break;
                case 1 :
                    markerOptions.icon(BitmapFromVector(getApplicationContext(), R.drawable.fuel_24));
                    break;
                case 2 :
                    // markerOptions.icon(BitmapFromVector(getApplicationContext(), R.drawable.home_48));
                    break;
                case 3 :
                    markerOptions.icon(BitmapFromVector(getApplicationContext(), R.drawable.food_24));
                    break;
                case 4 :
                    markerOptions.icon(BitmapFromVector(getApplicationContext(), R.drawable.car_park));
                    break;
                default:
                    break;
            }

            // Include in bounds builder
            builder.include(latLng);

            // Add to map
            Marker marker1 = mMap.addMarker(markerOptions);
            // Set marker tag for editing
            assert marker1 != null;
            marker1.setTag(marker.getMarker_id());
        }

        if(markerList.size() == 1) {
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 16));
        } else {
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        }
    }

    public void redrawMarkers() {
        mMap.clear();
//        visibleMarkerList = getVisibleMarkers();
        addMarkers(visibleMarkerList);

        // Main recyclerView - show list/sbulist of layers/markers
        visibleLayers = new ArrayList<>(layerViewModel.getVisibleLayerList());
        sectionListRV = findViewById(R.id.sectionListRecyclerView);
        markerMap = markerViewModel.getMarkersByLayer();
        final SectionListAdapter layerListAdapter = new SectionListAdapter(markerMap, visibleLayers);
        sectionListRV.setAdapter(layerListAdapter);
    }

    void saveCameraPosition() {
//        defaults = this.getPreferences(Context.MODE_PRIVATE);
        editor = preferences.edit();
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
//        defaults = this.getPreferences(Context.MODE_PRIVATE);

        // "initial longitude" is only used on first startup
        double longitude = preferences.getFloat("longitude", (float) defaultLocation.longitude);
        double latitude = preferences.getFloat("latitude", (float) defaultLocation.latitude);
        float zoom = preferences.getFloat("zoom", DEFAULT_ZOOM);
        LatLng startPosition = new LatLng(latitude, longitude);

        return new CameraPosition.Builder()
                .target(startPosition)      // Sets the center of the map to Mountain View
                .zoom(zoom)
                .build();
    }

    private void showEditDialog(double lat, double lng) {
        LatLng latLng = new LatLng(lat,lng);
        FragmentManager fm = getSupportFragmentManager();
        markerDetailFragment = new MarkerDetailFragment(latLng);
        markerDetailFragment.show(fm, "marker_detail_edit_name");

    }

//  Utility method to use vectorDrawable items as bitmap images
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0,0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}