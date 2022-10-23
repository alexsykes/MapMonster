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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.IconImageAdapter;
import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.Icon;
import com.alexsykes.mapmonster.data.IconViewModel;
import com.alexsykes.mapmonster.data.Layer;
import com.alexsykes.mapmonster.data.LayerDataItem;
import com.alexsykes.mapmonster.data.LayerViewModel;
import com.alexsykes.mapmonster.data.MapMarkerDataItem;
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
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class LayerEditActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "Info";

    //  Data
    private LayerViewModel layerViewModel;
    private IconViewModel iconViewModel;

    Icon currentIcon;
    private boolean compassEnabled, mapToolbarEnabled, zoomControlsEnabled;

    List<Icon> allIcons;
    List<LayerDataItem> allLayers;
    LiveData<List<Layer>> layerLiveData;
    int[] iconIds;
    private MarkerViewModel markerViewModel;
    LayerDataItem currentLayerDataItem;

    // UIComponents
    LinearLayout buttonLinearLayout, layerDetailLinearList;
    RecyclerView layerDataRV, iconImageRV;
    TextView iconNameTextView, listTitleView;
    TextInputEditText layerNameTextInput, layerCodeTextInput;
    Button dismissButton, saveChangesButton;
    FloatingActionButton newLayerFAB;
    ImageButton iconImageButton;

    // General
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layer_list);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.layerMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        getData();
        setupUI();
        setupIconImageRV();

        layerDataRV = findViewById(R.id.layerDataRecyclerView);
        layerDataRV.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        final LiveLayerListAdapter adapter = new LiveLayerListAdapter(new LiveLayerListAdapter.LiveLayerDiff());
        layerDataRV.setAdapter(adapter);
        layerDataRV.setLayoutManager(new LinearLayoutManager(this));

        layerViewModel = new ViewModelProvider(this).get(LayerViewModel.class);
        layerViewModel.getLiveLayers().observe(this, layers -> {
            Log.i(TAG, "onCreate: ");
            adapter.submitList(layers);
        });
//        setupLayerRV();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
        editor.apply();
        saveCameraPosition();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
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
//        mMap.setOnMapLoadedCallback(this);
        mMap.setMinZoomPreference(4);
        mMap.setMaxZoomPreference(20);
        mMap.getUiSettings().setZoomControlsEnabled(zoomControlsEnabled);
        mMap.getUiSettings().setMapToolbarEnabled(mapToolbarEnabled);
        mMap.getUiSettings().setCompassEnabled(compassEnabled);
        CameraPosition cameraPosition = getSavedCameraPosition();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        addVisibleMarkersToMap();
    }

    // Setup
    private void getData() {
        iconViewModel = new ViewModelProvider(this).get(IconViewModel.class);
        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);
        allIcons = iconViewModel.getIconList();

        // Populate array of icon IDs
        iconIds = new int[allIcons.size()];
        for (int i = 0; i < allIcons.size(); i++) {
            iconIds[i] = getResources().getIdentifier(allIcons.get(i).getIconFilename(), "drawable", getPackageName());
        }
    }

    private void setupUI() {
        iconImageButton = findViewById(R.id.iconImageButton);
        newLayerFAB = findViewById(R.id.newLayerFAB);
        listTitleView = findViewById(R.id.listTitleView);
        layerDetailLinearList = findViewById(R.id.layerDetailsLL);
        iconImageButton.setOnClickListener(v -> {
            Log.i(TAG, "iconImageButton Clicked: ");
            displayIconImages();
        });
        iconNameTextView = findViewById(R.id.layerameTextView);
        buttonLinearLayout = findViewById(R.id.buttonLL);
        saveChangesButton = findViewById(R.id.saveChangesButton);
        dismissButton = findViewById(R.id.dismissButton);
        buttonLinearLayout.setVisibility(View.GONE);
        saveChangesButton.setVisibility(View.VISIBLE);
        dismissButton.setVisibility(View.VISIBLE);

        layerNameTextInput = findViewById(R.id.layerNameTextInput);
        layerCodeTextInput = findViewById(R.id.layerCodeTextInput);

        // Setup LayerList UI components
        layerNameTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveChangesButton.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        layerCodeTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveChangesButton.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

//        visibilitySwitch = findViewById(R.id.visibilitySwitch);
//        visibilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                saveChangesButton.setEnabled(true);
//            }
//        });


//        layerDataRV = findViewById(R.id.layerDataRecyclerView);
//        layerDataRV.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        newLayerFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, "onClick: new Layer");
                currentLayerDataItem = new LayerDataItem();
                currentLayerDataItem.layerID = 0;
                currentLayerDataItem.icon_id = 1;
                currentLayerDataItem.iconFilename = "map_marker";
                showLayerDetailList(currentLayerDataItem);
            }
        });
    }



    private void setupIconImageRV() {
        iconImageRV = findViewById(R.id.iconImageRV);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 6);
        iconImageRV.setLayoutManager(gridLayoutManager);

        final IconImageAdapter iconImageAdapter = new IconImageAdapter(iconIds);
        iconImageRV.setAdapter(iconImageAdapter);
        iconImageRV.setVisibility(View.GONE);
    }

    //  Event handling
    public void onLayerClickCalled(int position) {
        // Display details
//       get layerData and markerData for layer
        currentLayerDataItem = layerViewModel.getLayerDataItem(position);
        List<MapMarkerDataItem> mapMarkerDataItems = layerViewModel.getMapMarkerItems(position);

//      Get icon resource from resources
        int resID = getResources().getIdentifier(currentLayerDataItem.iconFilename, "drawable", getPackageName());
        Log.i(TAG, "iconID: " + resID);

//      Add markers to map
        addMarkersToMap(mapMarkerDataItems, resID);
        updateCamera(mapMarkerDataItems);

//         Display layer details for editing
        showLayerDetailList(currentLayerDataItem);
        saveChangesButton.setEnabled(false);
        dismissButton.setEnabled(true);
    }
    public void onIconClicked(int resid) {
        Log.i(TAG, "onIconClicked: ");
//      Change icon on button
        iconImageButton.setImageResource(resid);
        saveChangesButton.setEnabled(true);

//      Get icon name from
        String filename = getResources().getResourceEntryName(resid);
        String label = filename.replace("_", " ");
        label = label.substring(0, 1).toUpperCase() + label.substring(1);
        iconNameTextView.setText(label);
        currentIcon = iconViewModel.getIconByFilename(filename);
        iconImageRV.setVisibility(View.GONE);

//        if(currentLayerDataItem != null) {
        currentLayerDataItem.setIconFilename(filename);
        currentLayerDataItem.setName(label);
//        }
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

        vectorDrawable.setBounds(0,0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);

        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //  Display
    private void displayIconImages() {
        // iconIds - array of identifies for resources
        iconImageRV.setVisibility(View.VISIBLE);
        buttonLinearLayout.setVisibility(View.VISIBLE);
        saveChangesButton.setEnabled(false);
    }
    private void showButtons(boolean hasFocus) {
        if(hasFocus) {
            buttonLinearLayout.setVisibility(View.VISIBLE);
        } else {
            buttonLinearLayout.setVisibility(View.GONE);
        }
    }

    private void addMarkersToMap(List<MapMarkerDataItem> mapMarkerDataItems, int resID) {
        mMap.clear();
        if (mapMarkerDataItems.size() == 0) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No saved markers",
                    Toast.LENGTH_LONG);

            toast.show();
            return;
        }
        String marker_title, code;
        LatLng latLng;

        for (MapMarkerDataItem marker : mapMarkerDataItems) {
            latLng = new LatLng(marker.getLatitude(), marker.getLongitude());
            code = marker.getCode();

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

    private void addVisibleMarkersToMap() {
        List<MapMarkerDataItem> mapMarkerDataItems = markerViewModel.getVisibleMarkerDataList();

        mMap.clear();
        if (mapMarkerDataItems.size() == 0) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No saved markers",
                    Toast.LENGTH_LONG);

            toast.show();
            return;
        }
        String marker_title, code, filename;
        LatLng latLng;

        for (MapMarkerDataItem marker : mapMarkerDataItems) {
            latLng = new LatLng(marker.getLatitude(), marker.getLongitude());
            code = marker.getCode();
            filename = marker.filename;

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
            marker1.setTag(marker.getMarkerID());
        }
        updateCamera(mapMarkerDataItems);
    }

    private void showLayerDetailList(LayerDataItem currentLayerDataItem) {
        listTitleView.setVisibility(View.GONE);
        layerDataRV.setVisibility(View.GONE);
        layerDetailLinearList.setVisibility(View.VISIBLE);
        buttonLinearLayout.setVisibility(View.VISIBLE);
        newLayerFAB.setVisibility(View.GONE);

        dismissButton.setOnClickListener(v -> {
            showButtons(false);
            toggleEditView(false);
            addVisibleMarkersToMap();
        });

        // New layer - set initial values
        if(currentLayerDataItem.layerID == 0) {
            int resID = getResources().getIdentifier(currentLayerDataItem.iconFilename, "drawable", getPackageName());
            iconImageButton.setImageResource(resID);
            iconNameTextView.setText("Map marker");
            layerNameTextInput.setText("New Layer");
            layerCodeTextInput.setText("XX");
//            visibilitySwitch.setChecked(true);
            currentIcon = iconViewModel.getIconByFilename("map_marker");
        } else {
            layerNameTextInput.setText(currentLayerDataItem.layername);
            int resID = getResources().getIdentifier(currentLayerDataItem.iconFilename, "drawable", getPackageName());
            iconNameTextView.setText(currentLayerDataItem.iconName);
            currentIcon = iconViewModel.getIconByFilename(currentLayerDataItem.iconFilename);
            iconImageButton.setImageResource(resID);
            layerCodeTextInput.setText(currentLayerDataItem.code);
//            visibilitySwitch.setChecked(currentLayerDataItem.isVisible);
        }

        saveChangesButton.setEnabled(false);
        saveChangesButton.setOnClickListener(v -> {
            // Get values and update currentLayerDataItem
            currentLayerDataItem.setLayername(layerNameTextInput.getText().toString());
            currentLayerDataItem.setCode(layerCodeTextInput.getText().toString());
            currentLayerDataItem.icon_id = currentIcon.getIcon_id();

//          Update database
            saveLayerDataItem(currentLayerDataItem);
            showButtons(false);
            toggleEditView(false);
            addVisibleMarkersToMap();
        });

    }
    private void saveLayerDataItem(LayerDataItem currentLayerDataItem) {
        if (currentLayerDataItem.layerID == 0) {
            currentLayerDataItem.isVisible = true;
            layerViewModel.insertLayer(currentLayerDataItem);
        } else {
            layerViewModel.updateLayer(currentLayerDataItem);
        }
    }

    private void toggleEditView(boolean b) {
        if (b) {
            listTitleView.setVisibility(View.GONE);
            layerDataRV.setVisibility(View.GONE);
            layerDetailLinearList.setVisibility(View.VISIBLE);
            iconImageRV.setVisibility(View.VISIBLE);
            layerDetailLinearList.setVisibility(View.VISIBLE);
            newLayerFAB.setVisibility(View.GONE);
        } else {
            listTitleView.setVisibility(View.VISIBLE);
            layerDataRV.setVisibility(View.VISIBLE);
            newLayerFAB.setVisibility(View.VISIBLE);
            layerDetailLinearList.setVisibility(View.GONE);
            iconImageRV.setVisibility(View.GONE);
            layerDetailLinearList.setVisibility(View.GONE);
            iconImageRV.setVisibility(View.GONE);
        }
    }

    public void visibilityToggle(int layer_id) {
        Log.i(TAG, "visibilityToggle: " + layer_id);
        layerViewModel.toggle(layer_id);
        addVisibleMarkersToMap();
//        setupLayerRV();
    }
}