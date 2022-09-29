package com.alexsykes.mapmonster.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alexsykes.mapmonster.IconImageAdapter;
import com.alexsykes.mapmonster.LayerDataAdapter;
import com.alexsykes.mapmonster.MarkerDataAdapter;
import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.Icon;
import com.alexsykes.mapmonster.data.IconViewModel;
import com.alexsykes.mapmonster.data.Layer;
import com.alexsykes.mapmonster.data.LayerDataItem;
import com.alexsykes.mapmonster.data.LayerViewModel;
import com.alexsykes.mapmonster.data.MMDatabase;
import com.alexsykes.mapmonster.data.MapMarkerDataItem;
import com.alexsykes.mapmonster.data.MarkerViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.Objects;

public class LayerListActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "Info";

    //  Data
    private MarkerViewModel markerViewModel;
    private LayerViewModel layerViewModel;
    private IconViewModel iconViewModel;
    Icon currentIcon;
    List<Icon> allIcons;
    List<LayerDataItem> allLayers;
    LiveData<List<Layer>> layerLiveData;
    int[] iconIds;
    LayerDataItem currentLayerDataItem;

    // UIComponents
    LinearLayout buttonLinearLayout, layerDetailLinearList, markerDetailLinearList;
    RecyclerView layerDataRV, markerListRV, iconImageRV;
    TextView iconNameTextView;
    SwitchCompat visibilitySwitch;
    TextInputEditText layerNameTextInput, layerCodeTextInput;
    Button dismissButton, saveChangesButton;
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
        setupLayerRV();
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
        editor.apply();
        saveCameraPosition();
    }

    private void setupUI() {
        iconImageButton = findViewById(R.id.iconImageButton);
        layerDetailLinearList = findViewById(R.id.layerDetailsLL);
        markerDetailLinearList = findViewById(R.id.markerListLL);
        iconImageButton.setOnClickListener(v -> {
            Log.i(TAG, "iconImageButton Clicked: ");
            displayIconImages();
        });
        iconNameTextView = findViewById(R.id.iconNameTextView);
        buttonLinearLayout = findViewById(R.id.buttonLinearLayout);
        buttonLinearLayout.setVisibility(View.GONE);
        layerNameTextInput = findViewById(R.id.layerNameTextInput);
        layerCodeTextInput = findViewById(R.id.layerCodeTextInput);
        visibilitySwitch = findViewById(R.id.visibilitySwitch);
        layerDataRV = findViewById(R.id.layerDataRecyclerView);
        layerDataRV.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        markerListRV = findViewById(R.id.markerListRV);
        markerListRV.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        markerListRV.setLayoutManager(linearLayoutManager);
        markerListRV.setHasFixedSize(true);

        iconImageRV = findViewById(R.id.iconImageRV);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 6);
        iconImageRV.setLayoutManager(gridLayoutManager);

        final IconImageAdapter iconImageAdapter = new IconImageAdapter(iconIds);
        iconImageRV.setAdapter(iconImageAdapter);
        iconImageRV.setVisibility(View.GONE);

        saveChangesButton = findViewById(R.id.saveChangesButton);
        dismissButton = findViewById(R.id.dismissButton);

        saveChangesButton.setOnClickListener(v -> {

            // Get values and update currentLayerDataItem
            currentLayerDataItem.setLayername(Objects.requireNonNull(layerNameTextInput.getText()).toString());
            currentLayerDataItem.setCode(Objects.requireNonNull(layerCodeTextInput.getText()).toString());
            currentLayerDataItem.setVisible(visibilitySwitch.isChecked());
            currentLayerDataItem.icon_id = currentIcon.getIcon_id();

//              Update database
            layerViewModel.updateLayer(currentLayerDataItem);

            allLayers = layerViewModel.getLayerData();
            setupLayerRV();
            showButtons(false);
        });
        dismissButton.setOnClickListener(v -> showButtons(false));
    }

    private void displayIconImages() {
        // iconIds - array of identifies for resources
        iconImageRV.setVisibility(View.VISIBLE);
        buttonLinearLayout.setVisibility(View.VISIBLE);
    }

    private void setupLayerRV() {
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        layerDataRV.setLayoutManager(llm);
        layerDataRV.setHasFixedSize(true);
        final LayerDataAdapter layerDataAdapter = new LayerDataAdapter(allLayers);
        layerDataRV.setAdapter(layerDataAdapter);
    }

    private void getData() {
        Log.i(TAG, "getData: ");

        layerViewModel = new ViewModelProvider(this).get(LayerViewModel.class);
        iconViewModel = new ViewModelProvider(this).get(IconViewModel.class);
        allIcons = iconViewModel.getIconList();
        allLayers = layerViewModel.getLayerData();
        layerLiveData = layerViewModel.getAllLayers();

        // Populate array of icon IDs
        iconIds = new int[allIcons.size()];
        for (int i = 0; i < allIcons.size(); i++) {
            iconIds[i] = getResources().getIdentifier(allIcons.get(i).getFilename(), "drawable", getPackageName());
        }
    }

    public void onMarkerClickCalled(int position ) {

        Log.i(TAG, "onMarkerClickCalled: " + position);
    }

    public void onLayerClickCalled(int position) {
        // Display Marker detail linear list
        layerDetailLinearList.setVisibility(View.VISIBLE);
        markerDetailLinearList.setVisibility(View.VISIBLE);

        // get layerData and markerData for layer
        currentLayerDataItem = layerViewModel.getLayerDataItem(position);
        List<MapMarkerDataItem> mapMarkerDataItems = layerViewModel.getMapMarkerItems(position);

        int resID = getResources().getIdentifier(currentLayerDataItem.filename, "drawable", getPackageName());

        iconImageButton.setImageResource(resID);
        Log.i(TAG, "iconID: " + resID);

//         Show existing layer data in UI
        layerNameTextInput.setText(currentLayerDataItem.layername);
        iconNameTextView.setText(currentLayerDataItem.iconName);
        currentIcon = iconViewModel.getIconByFilename(currentLayerDataItem.filename);
        layerCodeTextInput.setText(currentLayerDataItem.code);
        visibilitySwitch.setChecked(currentLayerDataItem.isVisible);

        final MarkerDataAdapter markerDataAdapter = new MarkerDataAdapter(mapMarkerDataItems);
        markerListRV.setAdapter(markerDataAdapter);

        // Setup UI
        layerNameTextInput.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                showButtons(true);
            }
        });

        layerCodeTextInput.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                showButtons(true);
            }
        });

        visibilitySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> showButtons(true));

        Log.i(TAG, "Layer selected: " + position + " (" + mapMarkerDataItems.size() + ") markers");
    }

    private void showButtons(boolean hasFocus) {
        if(hasFocus) {
            buttonLinearLayout.setVisibility(View.VISIBLE);
        } else {
            buttonLinearLayout.setVisibility(View.GONE);
        }
    }

    public void onIconClicked(int resid) {
//      Change icon on button
        iconImageButton.setImageResource(resid);

//      Get icon name from
        String filename = getResources().getResourceEntryName(resid);
        String label = filename.replace("_", " ");
        label = label.substring(0, 1).toUpperCase() + label.substring(1);
        iconNameTextView.setText(label);
        currentLayerDataItem.setFilename(filename);
        currentLayerDataItem.setName(label);
        currentIcon = iconViewModel.getIconByFilename(filename);
        iconImageRV.setVisibility(View.GONE);
    }


    CameraPosition getSavedCameraPosition() {
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


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        CameraPosition cameraPosition = getSavedCameraPosition();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }
}