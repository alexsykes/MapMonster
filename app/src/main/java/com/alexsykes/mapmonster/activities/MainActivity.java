package com.alexsykes.mapmonster.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.LayerListAdapter;
import com.alexsykes.mapmonster.MarkerListAdapter;
import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.Layer;
import com.alexsykes.mapmonster.data.LayerViewModel;
import com.alexsykes.mapmonster.data.MMarker;
import com.alexsykes.mapmonster.data.MarkerViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnMapLoadedCallback, OnMapReadyCallback {

    private GoogleMap mMap;

    private static final String TAG = "Info";
    private static final int DEFAULT_ZOOM = 12;

    private List<MMarker> markerList;
    private List<Layer> layerList;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean locationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Location lastKnownLocation;
    private LatLng defaultLocation = new LatLng(53.59,-2.56);

    SharedPreferences defaults;
    SharedPreferences.Editor editor;
    private MarkerViewModel markerViewModel;
    private LayerViewModel layerViewModel;
    private TextView markerLabel, layerLabel, layerDisc, markerDisc, markerDetailText, markerInfoLabel;
    private Button cancelNewMarkerButton, saveNewMarkerButton;


    RecyclerView rv;
    RecyclerView layerRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
setupUIComponents();

        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);
        layerViewModel = new ViewModelProvider(this).get(LayerViewModel.class);

        layerLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onMarkerLabelClicked");
                if(layerRV.getVisibility() == View.VISIBLE) {
                    layerRV.setVisibility(View.GONE);
                    layerDisc.setText("↓");
                } else {
                    layerRV.setVisibility(View.VISIBLE);
                    layerDisc.setText("↑");
                }
            }
        });
        markerLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onMarkerLabelClicked");
                if(rv.getVisibility() == View.VISIBLE) {
                    rv.setVisibility(View.GONE);
                    markerDisc.setText("↓");
                } else {
                    rv.setVisibility(View.VISIBLE);
                    markerDisc.setText("↑");
                }
            }
        });





        // Load markerList
//        loadMarkerList();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setupUIComponents() {
        layerLabel = findViewById(R.id.layerLabel);
        markerLabel = findViewById(R.id.markerLabel);
        layerDisc = findViewById(R.id.layerDisc);
        markerDisc = findViewById(R.id.markerDisc);
        markerDetailText = findViewById(R.id.markerDetailText);
        markerInfoLabel = findViewById(R.id.markerInfoLabel);
        cancelNewMarkerButton = findViewById(R.id.cancelNewMarkerButton);
        saveNewMarkerButton = findViewById(R.id.saveNewMarkerButton);
        markerDetailText.setVisibility(View.GONE);
        markerInfoLabel.setVisibility(View.GONE);
        cancelNewMarkerButton.setVisibility(View.GONE);
        saveNewMarkerButton.setVisibility(View.GONE);

        saveNewMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerDetailText.setVisibility(View.GONE);
                markerInfoLabel.setVisibility(View.GONE);
                cancelNewMarkerButton.setVisibility(View.GONE);
                saveNewMarkerButton.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
                layerRV.setVisibility(View.VISIBLE);
                layerLabel.setVisibility(View.VISIBLE);
                layerDisc.setVisibility(View.VISIBLE);
                markerLabel.setVisibility(View.VISIBLE);
                markerDisc.setVisibility(View.VISIBLE);
            }
        });

        cancelNewMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerDetailText.setVisibility(View.GONE);
                markerInfoLabel.setVisibility(View.GONE);
                cancelNewMarkerButton.setVisibility(View.GONE);
                saveNewMarkerButton.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
                layerRV.setVisibility(View.VISIBLE);
                layerLabel.setVisibility(View.VISIBLE);
                layerDisc.setVisibility(View.VISIBLE);
                markerLabel.setVisibility(View.VISIBLE);
                markerDisc.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        rv = findViewById(R.id.markerRv);
       layerRV = findViewById(R.id.layerRecyclerView);
        final LayerListAdapter layerListAdapter = new LayerListAdapter(new LayerListAdapter.LayerDiff());
        layerRV.setAdapter(layerListAdapter);
        layerRV.setLayoutManager(new LinearLayoutManager(this));

        final MarkerListAdapter adapter = new MarkerListAdapter(new MarkerListAdapter.MarkerDiff());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        // List LiveData Markers
        markerViewModel.getAllMarkers().observe(this, markers -> {
            adapter.submitList(markers);
        });

        layerList = layerViewModel.getLayerList();
        layerListAdapter.submitList(layerList);
        layerViewModel.setVisibility(false,4);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help_menu_item:
                goHelp();
                return true;

            case R.id.settings_menu_item:
                goSettings();
                return true;
            default:
        }
        return false;
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
        mMap = googleMap;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String maptype = preferences.getString("map_view_type","NORMAL");
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
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        //MARK: MarkerDragListener
        mMap.setOnMarkerDragListener(
                new GoogleMap.OnMarkerDragListener() {
                    final DecimalFormat df = new DecimalFormat("#.00000");
                    LatLng startPos, endPos;

                    @Override
                    public void onMarkerDrag(@NonNull com.google.android.gms.maps.model.Marker marker) {
                        LatLng newpos = marker.getPosition();
                        String snippet = marker.getSnippet();
                        String latStr = df.format(newpos.latitude);
                        String lngStr = df.format(newpos.longitude);
                        int markerID  = (int) marker.getTag();

                        Log.i(TAG, "onMarkerDrag: id " + markerID);
                    }

                    @Override
                    public void onMarkerDragEnd(@NonNull com.google.android.gms.maps.model.Marker marker) {
                        LatLng newpos = marker.getPosition();
                        String snippet = marker.getSnippet();
                        String latStr = df.format(newpos.latitude);
                        String lngStr = df.format(newpos.longitude);
                        String marker_id = marker.getId();

                        markerDetailText.setText("Lat: " + latStr + System.lineSeparator() + "Lng: " + lngStr
                                + System.lineSeparator() + "Marker id: " + marker_id);
                    }

                    @Override
                    public void onMarkerDragStart(@NonNull com.google.android.gms.maps.model.Marker marker) {

                        markerDetailText.setVisibility(View.VISIBLE);
                        markerInfoLabel.setVisibility(View.VISIBLE);
                        cancelNewMarkerButton.setVisibility(View.VISIBLE);
                        saveNewMarkerButton.setVisibility(View.VISIBLE);
                        rv.setVisibility(View.GONE);
                        layerRV.setVisibility(View.GONE);
                        layerLabel.setVisibility(View.GONE);
                        layerDisc.setVisibility(View.GONE);
                        markerLabel.setVisibility(View.GONE);
                        markerDisc.setVisibility(View.GONE);

                        String snippet = marker.getSnippet();
                        String placename = marker.getTitle();
                        LatLng newpos = marker.getPosition();
                        String latStr = df.format(newpos.latitude);
                        String lngStr = df.format(newpos.longitude);

                        String markerText = "Snippet: " + snippet;
                        markerDetailText.setText("Lat: " + latStr + System.lineSeparator() + "Lng: " + lngStr
                                + System.lineSeparator() + "Marker id: " + snippet);
                        markerInfoLabel.setText(placename);
                    }
                }
        );

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
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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
        updateCamera();
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
            type = marker.getPlacename();
            String snippet = String.valueOf(marker.getMarker_id());

            marker_title = marker.getPlacename() + " " + code;
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(marker_title)
                    .snippet(snippet)
                    .visible(true);

            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.home_72));
            if (type.equals("Car park")) {
                markerOptions.visible(true);
                // markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                // markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.airport_runway));
            } else {
                markerOptions.visible(true);
            }
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

    public void onMarkerListItemClicked(MMarker marker) {
        Log.i(TAG, "onMarkerListItemClicked: " + marker.getCode());

        LatLng target = new LatLng(marker.getLatitude(), marker.getLongitude());
        int padding = 100;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(target, 18));
        // Zoom to marker_id
    }

//    public void onLayerListItemClicked(Layer layer) {
//    }

    public void onLayerListItemCheckedChanged(Layer layer, boolean isChecked) {
        Log.i(TAG, "onLayerListItemCheckedChanged: " + layer.getLayer_id() + isChecked);
        layerViewModel.setVisibility(isChecked, layer.getLayer_id());
    }
}
