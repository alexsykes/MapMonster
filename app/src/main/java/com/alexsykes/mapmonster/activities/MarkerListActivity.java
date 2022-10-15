package com.alexsykes.mapmonster.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.MarkerDataAdapter;
import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.Icon;
import com.alexsykes.mapmonster.data.IconViewModel;
import com.alexsykes.mapmonster.data.LayerDataItem;
import com.alexsykes.mapmonster.data.LayerViewModel;
import com.alexsykes.mapmonster.data.MMDatabase;
import com.alexsykes.mapmonster.data.MapMarkerDataItem;
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

public class MarkerListActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback {
    private static final String TAG = "Info";

    //  Data
    private MarkerViewModel markerViewModel;
    private LayerViewModel layerViewModel;
    private IconViewModel iconViewModel;
    List<Icon> allIcons;
    List<LayerDataItem> allLayers;
    List<MapMarkerDataItem> visibleMarkers;

    List<SpinnerData> layerListForSpinner;
    ArrayAdapter<String> spinnerAdapter;
    List<String> layernamesForSpinner;
    Spinner layerSpinner;

    // UIComponents
    RecyclerView markerDataRV;
    TextView listTitleView, markerTitleView, latLabel, lngLabel;
    TextInputEditText markerNameTextInput, markerCodeTextInput, markerNotesTextInput;
    Button dismissButton, saveChangesButton;
    FloatingActionButton newMarkerFAB;
    LinearLayout markerDetailLL, buttonLL;

    // General
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private GoogleMap mMap;
    private MapMarkerDataItem currentMarker;
    Marker currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_list);
        setupMap();
        getData();
        setupUI();
        setupMarkerRV();
        currentMarker = new MapMarkerDataItem();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        CameraPosition cameraPosition = getSavedCameraPosition();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setOnMarkerClickListener(this);

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);

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
        addMarkersToMap();
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
    private void updateCamera(List<MapMarkerDataItem> mapMarkerDataItems) {
        LatLng latLng;
        if (!mapMarkerDataItems.isEmpty()) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            int padding = 100;
            for (MapMarkerDataItem marker : mapMarkerDataItems) {
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

    private void editMarker(MapMarkerDataItem mapMarkerDataItem) {
        currentMarker = mapMarkerDataItem;
        Log.i(TAG, "editMarker: " + currentMarker.getPlacename());
        markerTitleView.setText("Editing " + currentMarker.placename);

        // Populate UI with data
        markerNameTextInput.setText(mapMarkerDataItem.placename);
        markerCodeTextInput.setText(mapMarkerDataItem.code);
        markerNotesTextInput.setText(mapMarkerDataItem.getNotes());

        // Need to set layer in layerSpinner
        layerSpinner.setSelection(spinnerAdapter.getPosition(mapMarkerDataItem.layername));

        final DecimalFormat df = new DecimalFormat("#.#####°");

        latLabel.setText(df.format(currentMarker.latitude));
        lngLabel.setText(df.format(currentMarker.longitude));

        newMarkerFAB.setVisibility(View.GONE);
        markerDetailLL.setVisibility(View.VISIBLE);
        markerDataRV.setVisibility(View.GONE);
        listTitleView.setVisibility(View.GONE);

        // redraw map with marker
        mMap.clear();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                return false;
            }
        });
        LatLng position = new LatLng(currentMarker.getLatitude(), currentMarker.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions()
                .draggable(true)
                .position(position)
                .title(mapMarkerDataItem.placename);

        currentLocation = mMap.addMarker(markerOptions);
//        mMap.animateCamera(CameraUpdateFactory.newLatLng(position));

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
        visibleMarkers = markerViewModel.getVisibleMarkerDataList();
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
                currentMarker = new MapMarkerDataItem();
                currentMarker.placename = "New marker";
                currentMarker.code = "Code";
                currentMarker.notes = "Add description here…";
                currentMarker.layer_id = 1;
                currentMarker.latitude = mMap.getCameraPosition().target.latitude;
                currentMarker.longitude = mMap.getCameraPosition().target.longitude;
                editMarker(currentMarker);
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
                currentMarker.layer_id = layerListForSpinner.get(position).getLayerID();
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
                currentMarker.longitude = currentLocation.getPosition().longitude;
                currentMarker.latitude = currentLocation.getPosition().latitude;

                markerViewModel.saveCurrentMarker(currentMarker);
                markerDetailLL.setVisibility(View.GONE);
                markerDataRV.setVisibility(View.VISIBLE);
                listTitleView.setVisibility(View.VISIBLE);
                newMarkerFAB.setVisibility(View.VISIBLE);

                updateMarkerRV();
                addMarkersToMap();
//                updateCamera(visibleMarkers);
            }
        });

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerDetailLL.setVisibility(View.GONE);
                markerDataRV.setVisibility(View.VISIBLE);
                listTitleView.setVisibility(View.VISIBLE);
                newMarkerFAB.setVisibility(View.VISIBLE);
                updateMarkerRV();
                addMarkersToMap();
//                updateCamera(visibleMarkers);
            }
        });

        markerNameTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentMarker.placename = s.toString();
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
                currentMarker.code = s.toString();
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
                currentMarker.notes = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void addMarkersToMap() {
        visibleMarkers = markerViewModel.getVisibleMarkerDataList();
        mMap.clear();
        mMap.setOnMarkerClickListener(this);
        if (visibleMarkers.size() == 0) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No saved markers",
                    Toast.LENGTH_LONG);

            toast.show();
            return;
        }
        String marker_title, code, type;
        LatLng latLng;
        String filename;

        for (MapMarkerDataItem marker : visibleMarkers) {
            latLng = new LatLng(marker.getLatitude(), marker.getLongitude());
            code = marker.getCode();
            filename = marker.getFilename();

            int resID = getResources().getIdentifier(filename, "drawable", getPackageName());

            marker_title = marker.getPlacename();
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(marker_title)
                    .draggable(false)
                    .snippet(code)
                    .icon(BitmapFromVector(getApplicationContext(), resID))
                    .visible(true);
            Marker marker1 = mMap.addMarker(markerOptions);
            marker1.setTag(marker);

        }
    }

    private void setupMarkerRV() {
        markerDataRV = findViewById(R.id.markerDataRecyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        markerDataRV.setLayoutManager(llm);
        markerDataRV.setHasFixedSize(true);
        markerDataRV.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        final MarkerDataAdapter markerDataAdapter = new MarkerDataAdapter(visibleMarkers);
        markerDataRV.setAdapter(markerDataAdapter);
    }

    private void updateMarkerRV() {
        final MarkerDataAdapter markerDataAdapter = new MarkerDataAdapter(visibleMarkers);
        markerDataRV.setAdapter(markerDataAdapter);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        currentMarker = (MapMarkerDataItem) marker.getTag();
        Log.i(TAG, "onMarkerClick: " + marker.getTag());
        saveCameraPosition();
        editMarker(currentMarker);
        return false;
    }

    public void onMarkerClickCalled(int position) {
        Log.i(TAG, "Marker selected: " + position);
        saveCameraPosition();
        currentMarker = visibleMarkers.get(position);
        editMarker(currentMarker);
    }
}