package com.alexsykes.mapmonster.activities;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.MarkerListAdapter;
import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.Marker;
import com.alexsykes.mapmonster.data.MarkerViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnMapLoadedCallback, OnMapReadyCallback {

    private GoogleMap mMap;

    private static final String TAG = "Info";
    private static final int DEFAULT_ZOOM = 12;

    private LiveData<List<Marker>> liveDataMarkers;
    private List<Marker> markerList;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean locationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Location lastKnownLocation;
    private LatLng defaultLocation = new LatLng(53.59,-2.56);

    SharedPreferences defaults;
    SharedPreferences.Editor editor;


    private MarkerViewModel markerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rv = findViewById(R.id.rv);

        final MarkerListAdapter adapter = new MarkerListAdapter(new MarkerListAdapter.MarkerDiff());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // List LiveData Markers
        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);
        markerViewModel.getAllMarkers().observe(this, markers -> {
            adapter.submitList(markers);
        });

        // Load markerList
        loadMarkerList();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.setOnMapLoadedCallback(this);
        mMap.setMinZoomPreference(8);
        mMap.setMaxZoomPreference(20);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        //MARK: MarkerDragListener
        mMap.setOnMarkerDragListener(
                new GoogleMap.OnMarkerDragListener() {
                    final DecimalFormat df = new DecimalFormat("#.#####");
                    LatLng startPos, endPos;

                    @Override
                    public void onMarkerDrag(@NonNull com.google.android.gms.maps.model.Marker marker) {
                        LatLng newpos = marker.getPosition();
                        String snippet = marker.getSnippet();
                        String latStr = df.format(newpos.latitude);
                        String lngStr = df.format(newpos.longitude);
                    }

                    @Override
                    public void onMarkerDragEnd(@NonNull com.google.android.gms.maps.model.Marker marker) {
                        LatLng newpos = marker.getPosition();
                        String snippet = marker.getSnippet();
                        String latStr = df.format(newpos.latitude);
                        String lngStr = df.format(newpos.longitude);
                    }

                    @Override
                    public void onMarkerDragStart(@NonNull com.google.android.gms.maps.model.Marker marker) {

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

        Log.i(TAG, "onMapReady: " + markerList.size());
        addMarkersToMap();
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
        for (Marker marker : markerList) {
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

            if (type.equals("Car park")) {
                markerOptions.visible(true);
                // markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                // markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.airport_runway));
            } else {
                markerOptions.visible(true);
            }
            markerOptions.draggable(true);
            mMap.addMarker(markerOptions);
        }
    }
    private void updateCamera() {
        if (!markerList.isEmpty()) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            int padding = 100;
            LatLng latLng;
            for (Marker marker : markerList) {
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
}