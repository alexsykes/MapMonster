package com.alexsykes.mapmonster.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.Icon;
import com.alexsykes.mapmonster.data.IconViewModel;
import com.alexsykes.mapmonster.data.LayerDataItem;
import com.alexsykes.mapmonster.data.LayerViewModel;
import com.alexsykes.mapmonster.data.LiveMarkerItem;
import com.alexsykes.mapmonster.data.MMDatabase;
import com.alexsykes.mapmonster.data.MarkerViewModel;
import com.alexsykes.mapmonster.data.SpinnerData;
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
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MarkerEditActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback {
    private static final String TAG = "Info";

    //  Data
    private MarkerViewModel markerViewModel;
    private LayerViewModel layerViewModel;
    private IconViewModel iconViewModel;
    LiveData<List<Icon>> allIcons;
    List<LayerDataItem> allLayers;
    List<LiveMarkerItem> markersFromVisibleLayers;

    List<SpinnerData> layerListForSpinner;
    ArrayAdapter<String> spinnerAdapter;
    List<String> layernamesForSpinner;
    Spinner layerSpinner;
    private boolean compassEnabled, mapToolbarEnabled, zoomControlsEnabled;

    // UIComponents
    RecyclerView markerListRV;
    TextView listTitleView, markerTitleView, latLabel, lngLabel;
    TextInputEditText markerNameTextInput, markerCodeTextInput, markerNotesTextInput;
    Button dismissButton, saveChangesButton;
    FloatingActionButton newMarkerFAB;
    LinearLayout markerDetailLL, buttonLL;

    // General
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private GoogleMap mMap;
    private LiveMarkerItem currentMarker;
    Marker currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_edit);

        markerListRV = findViewById(R.id.markerDataRecyclerView);
        markerListRV.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        final LiveMarkerEditListAdapter liveMarkerListAdapter =
                new LiveMarkerEditListAdapter(new LiveMarkerEditListAdapter.LiveMarkerDiff());

        markerListRV.setAdapter(liveMarkerListAdapter);
        markerListRV.setLayoutManager(new LinearLayoutManager(this));

        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);
        markerViewModel.getLiveMarkers().observe(this, markers -> {
            liveMarkerListAdapter.submitList(markers);
            markersFromVisibleLayers = markers;
            addMarkersToMap(markers);
        });

        setupMap();
        getData();
        setupUI();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.marker_edit_menu, menu);
        return true;
    }

    // Navigation
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.marker_list_menuitem) {
            goMarkerList();
            return true;
        }
        return false;
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        CameraPosition cameraPosition = getSavedCameraPosition();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setOnMarkerClickListener(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String maptype = preferences.getString("map_view_type","NORMAL");

        zoomControlsEnabled = preferences.getBoolean("zoomControlsEnabled", true);
        mapToolbarEnabled = preferences.getBoolean("mapToolbarEnabled", true);
        compassEnabled = preferences.getBoolean("compassEnabled", true);

        switch (maptype) {
            case "satellite":
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case "terrain":
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            default:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

        mMap.setMinZoomPreference(4);
        mMap.setMaxZoomPreference(20);
        mMap.getUiSettings().setZoomControlsEnabled(zoomControlsEnabled);
        mMap.getUiSettings().setMapToolbarEnabled(mapToolbarEnabled);
        mMap.getUiSettings().setCompassEnabled(compassEnabled);


        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            final DecimalFormat df = new DecimalFormat("#.#####");
            String latStr, lngStr;

            @Override
            public void onMarkerDrag(@NonNull Marker marker) {
////                Log.i(TAG, "onMarkerDrag: ");
//                currentLocation = marker.getPosition();
//                latStr = df.format(currentLocation.latitude);
//                lngStr = df.format(currentLocation.longitude);
//                markerLatTextView.setText(latStr);
//                markerLngTextView.setText(lngStr);
            }

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
//                Log.i(TAG, "onMarkerDragEnd: ");
                currentLocation = marker;
            }

            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {
//                currentLocation = marker.getPosition();
////                Log.i(TAG, "onMarkerDragStart: " + marker.getTag());
//                latStr = df.format(currentLocation.latitude);
//
//                int markerId = (Integer) marker.getTag();
//                MMarker currentMarker = markerViewModel.getMarker(markerId);
//                latStr = df.format(currentLocation.latitude);
//                lngStr = df.format(currentLocation.longitude);
//                markerInfoPanel.setVisibility(View.VISIBLE);
//                layerPanelLinearLayout.setVisibility(View.GONE);
//                addMarkerButton.setVisibility(View.GONE);
//
//                markerIdTextView.setText(new StringBuilder().append(getString(R.string.marker_id)).append(currentMarker.getMarker_id()).toString());
//                markerLatTextView.setText(latStr);
//                markerLngTextView.setText(lngStr);
//
//                markerCodeEditText.setText(currentMarker.getCode());
//                markerNameEditText.setText(currentMarker.getPlacename());
//                markerNotesEditText.setText(currentMarker.getNotes());
            }
        });
//        addMarkersToMap(markers);
    }

    //  Utility methods
    private CameraPosition getSavedCameraPosition() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // "initial longitude" is only used on first startup
        double longitude = preferences.getFloat("longitude", (float) 0);
        double latitude = preferences.getFloat("latitude", (float) 0);
        float zoom = preferences.getFloat("zoom", 8);
        LatLng startPosition = new LatLng(latitude, longitude);

        return new CameraPosition.Builder()
                .target(startPosition)      // Sets the center of the map to Mountain View
                .zoom(zoom)
                .build();
    }
    private void saveCameraPosition() {
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
    private void updateCamera(List<LiveMarkerItem> mapMarkerDataItems) {
        LatLng latLng;
        if (!mapMarkerDataItems.isEmpty()) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            int padding = 100;
            for (LiveMarkerItem marker : mapMarkerDataItems) {
                latLng = new LatLng(marker.getLatitude(), marker.getLongitude());
                builder.include(latLng);
            }
            LatLngBounds bounds =
                    builder.build();

            if (mapMarkerDataItems.size() > 1) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
            } else {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 16));
            }
        }
    }
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);

        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void editMarker(LiveMarkerItem currentMarker) {
        this.currentMarker = currentMarker;
        Log.i(TAG, "editMarker: " + this.currentMarker.getPlacename());
        markerTitleView.setText("Editing " + this.currentMarker.getPlacename());

        // Populate UI with data
        markerNameTextInput.setText(currentMarker.getPlacename());
        markerCodeTextInput.setText(currentMarker.getCode());
        markerNotesTextInput.setText(currentMarker.getNotes());

        // Need to set layer in layerSpinner
        Log.i(TAG, "editMarker: " + currentMarker.getLayerName());
        layerSpinner.setSelection(spinnerAdapter.getPosition(currentMarker.getLayerName()));

        final DecimalFormat df = new DecimalFormat("#.#####°");

        latLabel.setText(df.format(this.currentMarker.getLatitude()));
        lngLabel.setText(df.format(this.currentMarker.getLongitude()));

        newMarkerFAB.setVisibility(View.GONE);
        markerDetailLL.setVisibility(View.VISIBLE);
        markerListRV.setVisibility(View.GONE);
        listTitleView.setVisibility(View.GONE);

        // redraw map with marker
        mMap.clear();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                return false;
            }
        });
        LatLng position = new LatLng(this.currentMarker.getLatitude(), this.currentMarker.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions()
                .draggable(true)
                .position(position)
                .title(currentMarker.getPlacename());

        currentLocation = mMap.addMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(position));
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
        saveCameraPosition();
    }

    // Setup methods
    private void getData() {
        MMDatabase db = MMDatabase.getDatabase(this);
        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);
        layerViewModel = new ViewModelProvider(this).get(LayerViewModel.class);
        iconViewModel = new ViewModelProvider(this).get(IconViewModel.class);
        allIcons = iconViewModel.getIconList();
        allLayers = layerViewModel.getLayerData();
        layerListForSpinner = layerViewModel.getLayerListForSpinner();
        layernamesForSpinner = layerViewModel.getLayernamesForSpinner();
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.markerMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    private void setupUI() {
        listTitleView = findViewById(R.id.listTitleView);
        markerTitleView = findViewById(R.id.markerTitleView);
        markerDetailLL = findViewById(R.id.markerDetailLL);
        buttonLL = findViewById(R.id.buttonLL);
        markerNameTextInput = findViewById(R.id.markerNameTextInput);
        markerCodeTextInput = findViewById(R.id.markerCodeTextInput);
        markerNotesTextInput = findViewById(R.id.markerNotesTextInput);
        latLabel = findViewById(R.id.latLabel);
        lngLabel = findViewById(R.id.lngLabel);

        newMarkerFAB = findViewById(R.id.newMarkerFAB);
        newMarkerFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newMarker();
            }
        });

        layerSpinner = findViewById(R.id.layerSpinner);
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        layerSpinner.setAdapter(spinnerAdapter);
        spinnerAdapter.addAll(layernamesForSpinner);
        spinnerAdapter.notifyDataSetChanged();
        layerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Log.i(TAG, "onItemClick: " + layerListForSpinner.get(position).getLayerID());
                currentMarker.setLayer_id(layerListForSpinner.get(position).getLayerID());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dismissButton = findViewById(R.id.dismissButton);
        saveChangesButton = findViewById(R.id.saveChangesButton);

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMarker.setLongitude(currentLocation.getPosition().longitude);
                currentMarker.setLatitude(currentLocation.getPosition().latitude);

                markerViewModel.saveCurrentMarker(currentMarker);
                markerDetailLL.setVisibility(View.GONE);
                markerListRV.setVisibility(View.VISIBLE);
                listTitleView.setVisibility(View.VISIBLE);
                newMarkerFAB.setVisibility(View.VISIBLE);

//                updateMarkerRV();
//                addMarkersToMap(markers);
//                updateCamera(visibleMarkers);
            }
        });

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerDetailLL.setVisibility(View.GONE);
                markerListRV.setVisibility(View.VISIBLE);
                listTitleView.setVisibility(View.VISIBLE);
                newMarkerFAB.setVisibility(View.VISIBLE);
//                updateMarkerRV();
//                addMarkersToMap(markers);
//                updateCamera(visibleMarkers);
            }
        });

        markerNameTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentMarker.setPlacename(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        markerCodeTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentMarker.setCode(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        markerNotesTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentMarker.setNotes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void goMarkerList() {
        Intent intent = new Intent(MarkerEditActivity.this, LiveMarkerListActivity.class);
        startActivity(intent);
    }


    private void addMarkersToMap(List<LiveMarkerItem> markers) {
        markersFromVisibleLayers = markers;
        mMap.clear();
        mMap.setOnMarkerClickListener(this);
        if (markersFromVisibleLayers.size() == 0) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No visible markers",
                    Toast.LENGTH_LONG);

            toast.show();
            return;
        }
        String marker_title, code, type;
        LatLng latLng;
        String filename;
        boolean isVisible;

        for (LiveMarkerItem marker : markersFromVisibleLayers) {
            latLng = new LatLng(marker.getLatitude(), marker.getLongitude());
            code = marker.getCode();
            filename = marker.getIconFilename();
            isVisible = marker.isVisible();

            int resID = getResources().getIdentifier(filename, "drawable", getPackageName());

            marker_title = marker.getPlacename();
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(marker_title)
                    .draggable(false)
                    .snippet(code)
                    .icon(BitmapFromVector(getApplicationContext(), resID))
                    .visible(isVisible);
            Marker marker1 = mMap.addMarker(markerOptions);
            marker1.setTag(marker);
        }
    }

    // Method to edit Marker from map location
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        currentMarker = (LiveMarkerItem) marker.getTag();
        Log.i(TAG, "onMarkerClick: " + marker.getTag());
        saveCameraPosition();
        editMarker(currentMarker);
        return false;
    }

    // Toggle marker visibility - working
    public void visibilityToggle(int marker_id) {
        Log.i(TAG, "markerVisibilityToggle: " + marker_id);
        markerViewModel.toggle(marker_id);

//        updateMarkerRV();
//        addMarkersToMap(markers);
//        setupLayerRV();
    }


    // Method to edit marker from RecyclerView
    public void onMarkerClickCalled(LiveMarkerItem currentMarker) {
        Log.i(TAG, "Marker selected: " + currentMarker);
        saveCameraPosition();
        editMarker(currentMarker);
    }

//  Add new marker from FAB
    private void newMarker() {
        Log.i(TAG, "newMarker: from FAB");
        currentMarker = new LiveMarkerItem();
        currentMarker.setPlacename("New marker");
        currentMarker.setCode("Code");
        currentMarker.setNotes("Add description here…");
        currentMarker.setLayer_id(1);
        currentMarker.setLatitude(mMap.getCameraPosition().target.latitude);
        currentMarker.setLongitude(mMap.getCameraPosition().target.longitude);
        editMarker(currentMarker);
    }
}