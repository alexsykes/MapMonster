package com.alexsykes.mapmonster.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.Icon;
import com.alexsykes.mapmonster.data.IconViewModel;
import com.alexsykes.mapmonster.data.LayerViewModel;
import com.alexsykes.mapmonster.data.MMDatabase;
import com.alexsykes.mapmonster.data.MapMarkerDataItem;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


// TODO Check out use of tag - line 398
public class MainActivity extends AppCompatActivity implements GoogleMap.OnMapLoadedCallback, OnMapReadyCallback {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;

    private static final String TAG = "Info";
    private static final int DEFAULT_ZOOM = 0;

    private List<MapMarkerDataItem> markerList;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
//    private Location lastKnownLocation;

    private MarkerViewModel markerViewModel;
    private LayerViewModel layerViewModel;
    private IconViewModel iconViewModel;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private boolean locationPermissionGranted,compassEnabled, mapToolbarEnabled, zoomControlsEnabled;
    private List<String> visibleLayerList;
    private List<Icon> iconList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        setContentView(R.layout.activity_main);

        // get saved values and editor from prefs
        // defaults = this.getPreferences(Context.MODE_PRIVATE);
        // Prefs shared across app
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
//        mapFragment.getMapAsync(this);
        setupUIComponents();
    }

    private void getData() {
        // Get data
        MMDatabase db = MMDatabase.getDatabase(this);
        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);
        layerViewModel = new ViewModelProvider(this).get(LayerViewModel.class);
        iconViewModel = new ViewModelProvider(this).get(IconViewModel.class);
        markerList = markerViewModel.getMarkerList();

        markerList = markerViewModel.getVisibleMarkerDataList();
        visibleLayerList = layerViewModel.getVisibleLayerList();
        iconList = iconViewModel.getIconList();

        Resources resources = this.getResources();
//      Image from database - https://stackoverflow.com/questions/42992989/storing-image-resource-id-in-sqlite-database-and-retrieving-it-in-int-array
//        https://stackoverflow.com/questions/21402275/best-way-to-store-resource-id-in-database-on-android
        for (Icon item: iconList) {
            int resID = resources.getIdentifier(item.getIconFilename() , "drawable", this.getPackageName());
//            Log.i(TAG, "refID: " + resID);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        getData();
//        addMarkersToMap();
        mapFragment.getMapAsync(this);
        getSavedCameraPosition();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
        editor.apply();
        saveCameraPosition();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("MissingPermission")
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady: ");
        mMap = googleMap;

        boolean success = mMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.map_style)));
        if (!success) {
            Log.e(TAG, "Style parsing failed.");
        }

        // Position the map's camera near Sydney, Australia.
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-34, 151)));

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
        mMap.setOnMapLoadedCallback(this);
        mMap.setMinZoomPreference(4);
        mMap.setMaxZoomPreference(20);
        mMap.getUiSettings().setZoomControlsEnabled(zoomControlsEnabled);
        mMap.getUiSettings().setMapToolbarEnabled(mapToolbarEnabled);
        mMap.getUiSettings().setCompassEnabled(compassEnabled);

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

//        addMarkersToMap();
        Log.i(TAG, "onMapReady: " + markerList.size() + " markers loaded");
    }

    @Override
    public void onMapLoaded() {
        Log.i(TAG, "onMapLoaded: ");
//        loadMarkerList();
        addMarkersToMap();
        updateCamera();
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

            case R.id.go_layer_list:
                goLayerList();
                return true;

            case R.id.go_marker_list:
                goMarkerList();
                return true;
            default:
        }
        return false;
    }

    // Navigation
    private void goLayerList() {
        Intent intent = new Intent(MainActivity.this, LayerListActivity.class);
        startActivity(intent);
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

    // Permissions
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
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        Log.i(TAG, "onMyLocationButtonClick: ");
                        return false;
                    }
                });
//                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    // Utility methods
    private void setupUIComponents() {
        preferences = this.getPreferences(Context.MODE_PRIVATE);
    }
    private void addMarkersToMap() {
        if (markerList.size() == 0) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No saved markers",
                    Toast.LENGTH_LONG);

            toast.show();
            return;
        }
        String marker_title, code, type;
        LatLng latLng;
        int layer_id;
        String filename;
        mMap.clear();
        for (MapMarkerDataItem marker : markerList) {
            latLng = new LatLng(marker.getLatitude(), marker.getLongitude());
            code = marker.getCode();
            layer_id = marker.getLayer_id();
            String snippet = marker.getNotes();
            filename = marker.getFilename();

            int resID = getResources().getIdentifier(filename, "drawable", getPackageName());

//            Log.i(TAG, "icon: " + filename);
            marker_title = marker.getPlacename();
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(marker_title)
                    .draggable(false)
                    .snippet(code)
                    .icon(BitmapFromVector(getApplicationContext(), resID))
                    .visible(true);

            Marker marker1 = mMap.addMarker(markerOptions);
            marker1.setTag(marker.getMarkerID());
        }
    }
    private void updateCamera() {
        LatLng latLng = new LatLng(0,0);
        if (!markerList.isEmpty()) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            int padding = 100;
            for (MapMarkerDataItem marker : markerList) {
                latLng = new LatLng(marker.getLatitude(), marker.getLongitude());
                builder.include(latLng);
            }
            if(markerList.size() > 1) {
                LatLngBounds bounds =
                        builder.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
            } else {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }
        }
    }
    CameraPosition getSavedCameraPosition() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // "initial longitude" is only used on first startup
        double longitude = preferences.getFloat("longitude", 0.0F);
        double latitude = preferences.getFloat("latitude", 0.0F);
        float zoom = preferences.getFloat("zoom", DEFAULT_ZOOM);
//        if(latitude && latitude) {
            LatLng startPosition = new LatLng(latitude, longitude);
//        }
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(startPosition)      // Sets the center of the map to Mountain View
                .zoom(zoom)
                .build();                   // Creates a CameraPosition from the builder

        return cameraPosition;
    }
    void saveCameraPosition() {
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

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        vectorDrawable.setBounds(0,0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);

        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
