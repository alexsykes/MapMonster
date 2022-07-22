package com.alexsykes.mapmonster.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.Layer;
import com.alexsykes.mapmonster.data.LayerViewModel;
import com.alexsykes.mapmonster.data.MMDatabase;
import com.alexsykes.mapmonster.data.MMarker;
import com.alexsykes.mapmonster.data.MarkerDao;
import com.alexsykes.mapmonster.data.MarkerViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnMapLoadedCallback, OnMapReadyCallback {

    private GoogleMap mMap;

    private static final String TAG = "Info";
    private static final int DEFAULT_ZOOM = 12;

    private List<MMarker> markerList;
    private List<Layer> layerList;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    RecyclerView markerRV;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Location lastKnownLocation;
    private LatLng defaultLocation = new LatLng(53.59,-2.56);
    private LatLng curLocation;

    private MarkerViewModel markerViewModel;
    private LayerViewModel layerViewModel;
    private MarkerDao markerDao;

    SharedPreferences defaults;

    SharedPreferences.Editor editor;
    private int mode;
    private TextView markerLabel, markerPlus, layerLabel, layerDisc, markerDisc, markerDetailText, markerInfoLabel;
    private Button cancelNewMarkerButton, saveNewMarkerButton;
    private boolean locationPermissionGranted,compassEnabled, mapToolbarEnabled, zoomControlsEnabled, layersCollapsed, markersCollapsed, editingMarker;
    RecyclerView layerRV;
    private int current_marker_id;
    MarkerDetailFragment markerDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        setContentView(R.layout.activity_main);
        defaults = this.getPreferences(Context.MODE_PRIVATE);
        editor = defaults.edit();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        mode = defaults.getInt("mode",0);
        setupUIComponents();
        MMDatabase db = MMDatabase.getDatabase(this);
        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);
//        layerViewModel = new ViewModelProvider(this).get(LayerViewModel.class);
        markerDao = db.markerDao();

//        layerLabel.setOnClickListener(v -> {
//            Log.i(TAG, "onMarkerLabelClicked");
//            layersCollapsed = defaults.getBoolean("layersCollapsed", true);
//            if(layerRV.getVisibility() == View.VISIBLE) {
//                layerRV.setVisibility(View.GONE);
//                layerDisc.setText("↓");
//                layersCollapsed= true;
//            } else {
//                layerRV.setVisibility(View.VISIBLE);
//                layerDisc.setText("↑");
//                layersCollapsed= false;
//            }
//            editor.putBoolean("layersCollapsed", layersCollapsed);
//            editor.apply();
//        });
//        markerLabel.setOnClickListener(v -> {
//            Log.i(TAG, "onMarkerLabelClicked");
//            markersCollapsed = defaults.getBoolean("markersCollapsed", true);
//            if(markerRV.getVisibility() == View.VISIBLE) {
//                markerRV.setVisibility(View.GONE);
//                markerDisc.setText("↓");
//                markersCollapsed = true;
//            } else {
//                markerRV.setVisibility(View.VISIBLE);
//                markerDisc.setText("↑");
//                markersCollapsed = false;
//            }
//            editor.putBoolean("markersCollapsed", markersCollapsed);
//            editor.apply();
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
//        setupMode();
        getSavedCameraPosition();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
//        editor.putInt("mode",mode);
//        editor.putBoolean("layersCollapsed",layersCollapsed);
//        editor.putBoolean("markersCollapsed",markersCollapsed);
//        editor.putBoolean("editingMarker",editingMarker);
        editor.apply();
        saveCameraPosition();
    }

    private void setupUIComponents() {
        defaults = this.getPreferences(Context.MODE_PRIVATE);
//        layersCollapsed = defaults.getBoolean("layersCollapsed", true);
//        markersCollapsed = defaults.getBoolean("markersCollapsed", true);
//        editingMarker = defaults.getBoolean("editingMarker", false);

        // Lists
//        markerLabel = findViewById(R.id.markerLabel);
//        markerDisc = findViewById(R.id.markerDisc);
//        markerPlus = findViewById(R.id.markerPlus);
//        markerRV = findViewById(R.id.markerRv);
//
//        layerLabel = findViewById(R.id.layerLabel);
//        layerDisc = findViewById(R.id.layerDisc);
//        layerRV = findViewById(R.id.layerRecyclerView);

//        displayBoxes();

        // Marker info box
//        markerInfoLabel = findViewById(R.id.markerInfoLabel);
//        markerDetailText = findViewById(R.id.markerDetailText);
//        cancelNewMarkerButton = findViewById(R.id.cancelNewMarkerButton);
//        saveNewMarkerButton = findViewById(R.id.saveNewMarkerButton);

//        markerDetailText.setVisibility(View.GONE);
//        markerInfoLabel.setVisibility(View.GONE);
//        cancelNewMarkerButton.setVisibility(View.GONE);
//        saveNewMarkerButton.setVisibility(View.GONE);
//
//        cancelNewMarkerButton.setOnClickListener(v -> {
//            markerDetailText.setVisibility(View.GONE);
//            markerInfoLabel.setVisibility(View.GONE);
//            cancelNewMarkerButton.setVisibility(View.GONE);
//            saveNewMarkerButton.setVisibility(View.GONE);
//            editor.putBoolean("editingMarker", false);
//            editor.apply();
//            mode = 0;
//            setupMode();
//            mMap.clear();
//            // loadMarkerList();
//            addMarkersToMap();
//        });
//        saveNewMarkerButton.setOnClickListener(v -> {
//            markerDetailText.setVisibility(View.GONE);
//            markerInfoLabel.setVisibility(View.GONE);
//            cancelNewMarkerButton.setVisibility(View.GONE);
//            saveNewMarkerButton.setVisibility(View.GONE);
//            editor.putBoolean("editingMarker", false);
//            editor.apply();
//            mode = 0;
//            setupMode();
//            if(current_marker_id == -999) {
//                markerDao.insertMarker(new MMarker(curLocation.latitude, curLocation.longitude, "Waypoint", "Code", "waypoint", ""));
//            } else {
//                markerDao.updateMarker(current_marker_id, curLocation.latitude, curLocation.longitude, true);
//            }
//
//        });
//
//        markerPlus.setOnClickListener(v -> {
//            mode = 0;
//            editor.putInt("mode", mode);
//            editor.apply();
//            setupMode();
//            showEditDialog();
//            Log.i(TAG, "onClick: Add new marker");
//            curLocation = mMap.getCameraPosition().target;
//            MarkerOptions newMarker = new MarkerOptions()
//                    .position(curLocation)
//                    .draggable(true);
//
////                mMap.addMarker(newMarker);
//
//            //MARK: MarkerDragListener
////                mMap.setOnMarkerDragListener(
////                        new GoogleMap.OnMarkerDragListener() {
////                            final DecimalFormat df = new DecimalFormat("#.#####");
////
////                            @Override
////                            public void onMarkerDrag(@NonNull Marker marker) {
////                            }
////
////                            @Override
////                            public void onMarkerDragEnd(@NonNull Marker marker) {
////                                LatLng newpos = marker.getPosition();
////                                String snippet = marker.getSnippet();
////                            }
////
////                            @Override
////                            public void onMarkerDragStart(@NonNull Marker marker) {
////                                cancelNewMarkerButton.setVisibility(View.VISIBLE);
////                                saveNewMarkerButton.setVisibility(View.VISIBLE);
////                                curLocation = marker.getPosition();
////                                String snippet = marker.getSnippet();
////                                String latStr = df.format(curLocation.latitude);
////                                String lngStr = df.format(curLocation.longitude);
////                                current_marker_id = (int) marker.getTag();
////
////                                markerDetailText.setText("Lat: " + latStr + System.lineSeparator() + "Lng: " + lngStr
////                                        + System.lineSeparator() + "Marker id: " + current_marker_id);
////                                Log.i(TAG, "onMarkerDrag: id " + current_marker_id);
////                            }
////                        }
////                );
//        });
    }

//    /* Modes
//        0 - Normal mode - Marker and Layer boxes visible, MarkerInfo box GONE
//        1 - Editing marker mode
//     */
//    private void setupMode(){
//        switch (mode) {
//            case 0:
//                markerLabel.setVisibility(View.VISIBLE);
//                markerPlus.setVisibility(View.VISIBLE);
//                markerDisc.setVisibility(View.VISIBLE);
//                if(markersCollapsed) {
//                    markerRV.setVisibility(View.GONE);
//                    markerDisc.setText("↓");
//                } else {
//                    markerRV.setVisibility(View.VISIBLE);
//                    markerDisc.setText("↑");
//                }
//                layerLabel.setVisibility(View.VISIBLE);
//                layerDisc.setVisibility(View.VISIBLE);
//                if(layersCollapsed) {
//                    layerRV.setVisibility(View.GONE);
//                    layerDisc.setText("↓");
//                } else {
//                    layerRV.setVisibility(View.VISIBLE);
//                    layerDisc.setText("↑");
//                }
//
//                markerInfoLabel.setVisibility(View.GONE);
//                markerDetailText.setVisibility(View.GONE);
//                cancelNewMarkerButton.setVisibility(View.GONE);
//                saveNewMarkerButton.setVisibility(View.GONE);
//                break;
//
//            case 1:
//                markerLabel.setVisibility(View.GONE);
//                markerPlus.setVisibility(View.GONE);
//                markerDisc.setVisibility(View.GONE);
//                markerRV.setVisibility(View.GONE);
//                layerLabel.setVisibility(View.GONE);
//                layerDisc.setVisibility(View.GONE);
//                layerRV.setVisibility(View.GONE);
//
//                markerInfoLabel.setVisibility(View.VISIBLE);
//                markerDetailText.setVisibility(View.VISIBLE);
//                break;
//
//            case 2:
//                markerLabel.setVisibility(View.GONE);
//                markerPlus.setVisibility(View.GONE);
//                markerDisc.setVisibility(View.GONE);
//                markerRV.setVisibility(View.GONE);
//                layerLabel.setVisibility(View.GONE);
//                layerDisc.setVisibility(View.GONE);
//                layerRV.setVisibility(View.GONE);
//
//                markerInfoLabel.setVisibility(View.VISIBLE);
//                markerInfoLabel.setText("New marker");
//                markerDetailText.setVisibility(View.VISIBLE);
//                break;
//        }
//    }

//    private void displayBoxes() {
//        if(markersCollapsed) {
//            markerRV.setVisibility(View.GONE);
//        } else {
//            markerRV.setVisibility(View.VISIBLE);
//        }
//
//        if(layersCollapsed) {
//            layerRV.setVisibility(View.GONE);
//        } else {
//            layerRV.setVisibility(View.VISIBLE);
//        }
//    }

    @Override
    protected void onStart() {
        super.onStart();
//        final LayerListAdapter layerListAdapter = new LayerListAdapter(new LayerListAdapter.LayerDiff());
//        layerRV.setAdapter(layerListAdapter);
//        layerRV.setLayoutManager(new LinearLayoutManager(this));
//        layerRV.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//
//        final MarkerListAdapter adapter = new MarkerListAdapter(new MarkerListAdapter.MarkerDiff());
//        markerRV.setAdapter(adapter);
//        markerRV.setLayoutManager(new LinearLayoutManager(this));
//        markerRV.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//        // List LiveData Markers
//        markerViewModel.getAllMarkers().observe(this, markers -> adapter.submitList(markers));
//
//        layerList = layerViewModel.getLayerList();
//        layerListAdapter.submitList(layerList);
//        layerViewModel.setVisibility(false,4);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    // Navigation
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help_menu_item:
                goHelp();
                return true;

            case R.id.settings_menu_item:
                goSettings();
                return true;

            case R.id.marker_list_item:
                goMarkerList();
                return true;
            default:
        }
        return false;
    }

    private void goMarkerList() {
        Intent intent = new Intent(MainActivity.this,MarkerListActivity.class);
        startActivity(intent);
    }
    private void goSettings() {
        Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(intent);
    }
    private void goHelp() {
        Intent intent = new Intent(MainActivity.this,HelpActivity.class);
        startActivity(intent);
    }
    @SuppressLint("MissingPermission")
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady: ");
        mMap = googleMap;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String maptype = preferences.getString("map_view_type","NORMAL");

        zoomControlsEnabled = preferences.getBoolean("zoomControlsEnabled", true);
        mapToolbarEnabled = preferences.getBoolean("mapToolbarEnabled", true);
        compassEnabled = preferences.getBoolean("compassEnabled", true);

        switch (maptype) {
            case "normal":
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case "satellite":
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case "terrain":
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            default:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        mMap.setOnMapLoadedCallback(this);
        mMap.setMinZoomPreference(8);
        mMap.setMaxZoomPreference(20);
        mMap.getUiSettings().setZoomControlsEnabled(zoomControlsEnabled);
        mMap.getUiSettings().setMapToolbarEnabled(mapToolbarEnabled);
        mMap.getUiSettings().setCompassEnabled(compassEnabled);

//        //MARK: MarkerDragListener
//        mMap.setOnMarkerDragListener(
//                new GoogleMap.OnMarkerDragListener() {
//                    final DecimalFormat df = new DecimalFormat("#.00000");
//                    LatLng startPos, endPos;
//
//                    @Override
//                    public void onMarkerDrag(@NonNull com.google.android.gms.maps.model.Marker marker) {
//                        LatLng newpos = marker.getPosition();
//                        String snippet = marker.getSnippet();
//                        String latStr = df.format(newpos.latitude);
//                        String lngStr = df.format(newpos.longitude);
////                        showLayers(false);
////                        showLayers(false);
//                    }
//
//                    @Override
//                    public void onMarkerDragEnd(@NonNull com.google.android.gms.maps.model.Marker marker) {
//                        cancelNewMarkerButton.setVisibility(View.VISIBLE);
//                        saveNewMarkerButton.setVisibility(View.VISIBLE);
//                        curLocation = marker.getPosition();
//                        String snippet = marker.getSnippet();
//                        String latStr = df.format(curLocation.latitude);
//                        String lngStr = df.format(curLocation.longitude);
//                        if(marker.getTag()!=null) {
//                            current_marker_id = (int) marker.getTag();
//                        } else {
//                            current_marker_id = -999;
//                        }
//                        markerDetailText.setText("Lat: " + latStr + System.lineSeparator() + "Lng: " + lngStr
//                                + System.lineSeparator() + "Marker id: " + current_marker_id);
//                        Log.i(TAG, "onMarkerDrag: id " + current_marker_id);
//                    }
//
//                    @Override
//                    public void onMarkerDragStart(@NonNull com.google.android.gms.maps.model.Marker marker) {
//                        editor.putBoolean("editingMarker", true);
//                        editor.apply();
////                        mode = 1;
////                        setupMode();
//                        String snippet = marker.getSnippet();
//                        String placename = marker.getTitle();
//                        LatLng newpos = marker.getPosition();
//                        String latStr = df.format(newpos.latitude);
//                        String lngStr = df.format(newpos.longitude);
//
//                        String markerText = "Snippet: " + snippet;
//                        markerDetailText.setText("Lat: " + latStr + System.lineSeparator() + "Lng: " + lngStr
//                                + System.lineSeparator() + "Marker id: " + snippet);
//                        markerInfoLabel.setText(placename);
//                    }
//                }
//        );

        // MarkerClickListener
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                          @Override
                                          public boolean onMarkerClick(@NonNull com.google.android.gms.maps.model.Marker marker) {
                                              // marker.setVisible(!marker.isVisible());
                                              Log.i(TAG, "onMarkerClick: ");
                                              marker.showInfoWindow();
                                              return false;
                                          }
                                      }
        );

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                Log.i(TAG, "onMapLongClick: ");
            }
        });
        getLocationPermission();
        mMap.setMyLocationEnabled(locationPermissionGranted);

        CameraPosition cameraPosition = getSavedCameraPosition();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

//        Log.i(TAG, "onMapReady: " + markerList.size());
        // addMarkersToMap();
    }
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                @SuppressLint("MissingPermission") Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    @SuppressLint("MissingPermission")
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
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

    @Override
    public void onMapLoaded() {
        Log.i(TAG, "onMapLoaded: ");
        loadMarkerList();
        addMarkersToMap();
        //updateCamera();
    }

    // Utility methods
    private void addMarkersToMap() {
        if (markerList.size() == 0) {
            return;
        }
        String marker_title, code, type;
        LatLng latLng;

        mMap.clear();
        for (MMarker marker : markerList) {
            latLng = new LatLng(marker.getLatitude(), marker.getLongitude());
            code = marker.getCode();
            type = marker.getType();
            String snippet = marker.getSnippet();

            marker_title = marker.getPlacename();
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(marker_title)
                    .snippet(snippet)
                    .visible(true);

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

//            if (type.equals("Car park")) {
//                markerOptions.visible(true);
//            } else {
//                markerOptions.visible(true);
//            }
            markerOptions.draggable(true);

            Marker marker1 = mMap.addMarker(markerOptions);
            marker1.setTag(marker.getMarker_id());
        }
    }
    private void updateCamera() {
        if (!markerList.isEmpty()) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            int padding = 100;
            LatLng latLng;
            for (MMarker marker : markerList) {
                latLng = new LatLng(marker.getLatitude(), marker.getLongitude());
                builder.include(latLng);
            }
            LatLngBounds bounds =
                    builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        }
    }
    private void loadMarkerList() {
        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);
        markerList = markerViewModel.getMarkerList();
    }

//    public void onMarkerListItemClicked(MMarker marker) {
//        Log.i(TAG, "onMarkerListItemClicked: " + marker.getCode());
//        LatLng target = new LatLng(marker.getLatitude(), marker.getLongitude());
//        int padding = 100;
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(target, 18));
//    }
//    public void onLayerListItemCheckedChanged(Layer layer, boolean isChecked) {
//        Log.i(TAG, "onLayerListItemCheckedChanged: " + layer.getLayer_id() + isChecked);
//        layerViewModel.setVisibility(isChecked, layer.getLayer_id());
//    }
//    private void showEditDialog() {
//        FragmentManager fm = getSupportFragmentManager();
//        markerDetailFragment = new MarkerDetailFragment();
//        markerDetailFragment.show(fm, "marker_detail_edit_name");
//    }

//    @Override
//    public void onReturn(Editable text, Editable code, String layer) {
//        Log.i(TAG, "onReturn: " + text + code + layer);
//
//        curLocation = mMap.getCameraPosition().target;
//        MMarker mMarker = new MMarker(curLocation.latitude, curLocation.longitude, text.toString(),code.toString(),layer, "");
//        markerDao.insertMarker(mMarker);
//        mMap.clear();
//        loadMarkerList();
//        addMarkersToMap();
//    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        vectorDrawable.setBounds(0,0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);

        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
