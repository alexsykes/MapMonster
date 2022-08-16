package com.alexsykes.mapmonster.activities;
// See - https://www.youtube.com/watch?v=x5afKIu0JmY

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
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

public class MarkerListActivity extends AppCompatActivity implements OnMapReadyCallback, MarkerDetailFragment.MarkerDetailFragmentListener {
    public static final String TAG = "Info";

    private static final int DEFAULT_ZOOM = 12;
    Map<String, List<MMarker>> markerMap;
    TextView showAllMarkersButton, markerIdTextView, markerLatTextView, markerLngTextView;
    EditText markerNameEditText, markerNotesEditText, markerCodeEditText;
    Button saveButton, cancelButton;
    FloatingActionButton addMarkerButton;
    SwitchMaterial showAllLayerList;
    RecyclerView sectionListRV;
    private List<MMarker> visibleMarkerList;
    SharedPreferences defaults;
    SharedPreferences.Editor editor;
    private GoogleMap mMap;
    private final LatLng defaultLocation = new LatLng(53.59,-2.56);
    LinearLayout layerPanelLinearLayout, markerInfoPanel;
    private ArrayList<String> visibleLayers ;
    MarkerDetailFragment markerDetailFragment;
    private LatLng currentLocation;
    private Marker currentMarker;
    private MarkerViewModel markerViewModel;
    private LayerViewModel layerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_list);
        defaults = this.getPreferences(Context.MODE_PRIVATE);

        MMDatabase db = MMDatabase.getDatabase(this);
        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);
        layerViewModel = new ViewModelProvider(this).get(LayerViewModel.class);
        // Get all markers / layers for menu
        markerMap = markerViewModel.getMarkersByLayer();
        visibleLayers = new ArrayList<>(layerViewModel.getVisibleLayerList());

        setupUI();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        // Main recyclerView - show list/sbulist of layers/markers
        sectionListRV = findViewById(R.id.sectionListRecyclerView);
        final SectionListAdapter layerListAdapter = new SectionListAdapter(markerMap, visibleLayers);
        sectionListRV.setAdapter(layerListAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.i(TAG, "onPause: MarkerListActivity");
        saveCameraPosition();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: MarkerListActivity");
        // Get list of visible layers
        visibleMarkerList = getVisibleMarkers();
    }

    // Navigation
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.marker_list_item) {
            toggleLayerPanel();
            return true;
        }
        return false;
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


        showAllLayerList = findViewById(R.id.showLayerList);
        showAllLayerList.setChecked(true);
        showAllLayerList.setOnClickListener(v -> {
            Log.i(TAG, "onClick: showAllMarkers");
            boolean setVisible = showAllLayerList.isChecked();
            showAllLayers(setVisible);
        });

        addMarkerButton = findViewById(R.id.addMarkerButton);
        addMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: New Marker;");
                addNewMarker();
            }
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

    // Used from Show/Hide button
    private void toggleAllMarkers() {
        // Get database then markerList
        MMDatabase db = MMDatabase.getDatabase(this);

        if ( showAllMarkersButton.getText().toString().equals(getString(R.string.show_all))) {
            visibleMarkerList = markerViewModel.getMarkerList();
            layerViewModel.setVisibilityForAll(true);
            showAllMarkersButton.setText(R.string.hide_all);
        } else {
            layerViewModel.setVisibilityForAll(false);
            showAllMarkersButton.setText("Show all");
        }
        visibleMarkerList = getVisibleMarkers();
        addMarkers(visibleMarkerList);
    }

    private void addMarkers(List<MMarker> markerList) {
        // Clear map of current markers, return if markerList is empty
        mMap.clear();
        if(markerList.isEmpty()) {
            return;
        }

        String type, snippet, marker_title;

        // Setup Bounds builder
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        int padding = 100;
        LatLng latLng;

        // Iterate through markerList
        for(int i = 0; i < markerList.size(); i++ ) {
            MMarker marker = visibleMarkerList.get(i);

            // Get data set
            latLng = new LatLng(marker.getLatitude(), marker.getLongitude());
            type = marker.getType();
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
            switch(type)  {
                case "Accommodation" :
                    markerOptions.icon(BitmapFromVector(getApplicationContext(), R.drawable.hotel_24));
                    break;
                case "Fuel" :
                    markerOptions.icon(BitmapFromVector(getApplicationContext(), R.drawable.fuel_24));
                    break;
                case "Waypoint" :
                    // markerOptions.icon(BitmapFromVector(getApplicationContext(), R.drawable.home_48));
                    break;
                case "Food" :
                    markerOptions.icon(BitmapFromVector(getApplicationContext(), R.drawable.food_24));
                    break;
                case "Parking" :
                    markerOptions.icon(BitmapFromVector(getApplicationContext(), R.drawable.parking_24));
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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;


        // Get list of visible layers
        visibleMarkerList = getVisibleMarkers();
        // Drag listener
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            final DecimalFormat df = new DecimalFormat("#.#####");
            String latStr, lngStr;
            @Override
            public void onMarkerDrag(@NonNull Marker marker) {
                Log.i(TAG, "onMarkerDrag: ");
                currentLocation = marker.getPosition();
                latStr = df.format(currentLocation.latitude);
                lngStr = df.format(currentLocation.longitude);
                markerLatTextView.setText(latStr);
                markerLngTextView.setText(lngStr);
            }

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
                Log.i(TAG, "onMarkerDragEnd: ");
                currentMarker = marker;
            }

            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {
                currentLocation = marker.getPosition();
                Log.i(TAG, "onMarkerDragStart: " + marker.getTag());
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

        return new CameraPosition.Builder()
                .target(startPosition)      // Sets the center of the map to Mountain View
                .zoom(zoom)
                .build();
    }

    List<MMarker> getVisibleMarkers() {
        MMDatabase db = MMDatabase.getDatabase(this);

        ArrayList<String> visibleLayerList = new ArrayList<>(layerViewModel.getVisibleLayerList());
        visibleMarkerList = markerViewModel.getVisibleMarkerList(visibleLayerList);
        return visibleMarkerList;
    }

    public void onMarkerListItemClicked(MMarker marker, int isVisible) {
        LatLng loc = new LatLng(marker.getLatitude(), marker.getLongitude());
        Log.i(TAG, "onMarkerListItemClicked: " + isVisible);
    }

    private void showEditDialog(double lat, double lng) {
        LatLng latLng = new LatLng(lat,lng);
        FragmentManager fm = getSupportFragmentManager();
        markerDetailFragment = new MarkerDetailFragment(latLng);
        markerDetailFragment.show(fm, "marker_detail_edit_name");

    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        assert vectorDrawable != null;
        vectorDrawable.setBounds(0,0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);

        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onReturn(Editable name, Editable code, Editable markerNotes, String layer) {
        mMap.clear();
        Log.i(TAG, "onReturn: ");
        LatLng curLocation = mMap.getCameraPosition().target;
        MMarker mMarker = new MMarker(curLocation.latitude, curLocation.longitude, name.toString(),code.toString(),layer, markerNotes.toString());
        markerViewModel.insert(mMarker);
        getVisibleMarkers();
        addMarkers(visibleMarkerList);
        CameraPosition cameraPosition = new CameraPosition(curLocation,18,0,0);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void redrawMarkers() {
        mMap.clear();
        visibleMarkerList = getVisibleMarkers();
        addMarkers(visibleMarkerList);

        // Main recyclerView - show list/sbulist of layers/markers
        visibleLayers = new ArrayList<>(layerViewModel.getVisibleLayerList());
        sectionListRV = findViewById(R.id.sectionListRecyclerView);
        markerMap = markerViewModel.getMarkersByLayer();
        final SectionListAdapter layerListAdapter = new SectionListAdapter(markerMap, visibleLayers);
        sectionListRV.setAdapter(layerListAdapter);
    }
}