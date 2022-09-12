package com.alexsykes.mapmonster.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alexsykes.mapmonster.LayerDataAdapter;
import com.alexsykes.mapmonster.MarkerDataAdapter;
import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.Icon;
import com.alexsykes.mapmonster.data.IconViewModel;
import com.alexsykes.mapmonster.data.LayerDataItem;
import com.alexsykes.mapmonster.data.LayerViewModel;
import com.alexsykes.mapmonster.data.MMDatabase;
import com.alexsykes.mapmonster.data.MapMarkerDataItem;
import com.alexsykes.mapmonster.data.MarkerViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class LayerListActivity extends AppCompatActivity {
    private static final String TAG = "Info";
    
//  Data
    private MarkerViewModel markerViewModel;
    private LayerViewModel layerViewModel;
    private IconViewModel iconViewModel;
    List<Icon> allIcons;
    List<LayerDataItem> allLayers;

    // UIComponents
    LinearLayout buttonLinearLayout;
    RecyclerView layerDataRV, markerListRV;
    TextView  layernameTextView, layerIdTextView, layerIconTextView, layerCodeTextView, iconNameTextView;
    SwitchCompat visibilitySwitch;
    TextInputEditText layerNameTextInput, layerCodeTextInput;
    Button dismissButton, saveChangesButton;
    ImageButton iconImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layer_list);

        setupUI();
        getData();
        setupLayerRV();

    }

    private void setupUI() {
//        layernameTextView = findViewById(R.id.layernameTextView);
//        layerIconTextView = findViewById(R.id.layerIconTextView);
//        layerCodeTextView = findViewById(R.id.layerCodeTextView);
        iconImageButton = findViewById(R.id.iconImageButton);
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

        saveChangesButton = findViewById(R.id.saveChangesButton);
        dismissButton = findViewById(R.id.dismissButton);

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showButtons(false);
            }
        });

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showButtons(false);
            }
        });

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
        MMDatabase db = MMDatabase.getDatabase(this);
        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);
        layerViewModel = new ViewModelProvider(this).get(LayerViewModel.class);
        iconViewModel = new ViewModelProvider(this).get(IconViewModel.class);
        allIcons = iconViewModel.getIconList();
        allLayers = layerViewModel.getLayerData();
    }
    
    public void onMarkerClickCalled(int position ) {

    }

    public void onLayerClickCalled(int position) {

        // get layerData and markerData for layer
        LayerDataItem layerDataItem = layerViewModel.getLayerDataItem(position);
        List<MapMarkerDataItem> mapMarkerDataItems = layerViewModel.getMapMarkerItems(position);

        int resID = getResources().getIdentifier(layerDataItem.filename, "drawable", getPackageName());

        iconImageButton.setImageResource(resID);
        Log.i(TAG, "iconID: " + resID);
//         Show existing layer data in UI
        layerNameTextInput.setText(layerDataItem.layername);
        iconNameTextView.setText(layerDataItem.iconName);
        layerCodeTextInput.setText(layerDataItem.code);
        boolean showOnMap = layerDataItem.isVisible;
        visibilitySwitch.setChecked(showOnMap);
        final MarkerDataAdapter markerDataAdapter = new MarkerDataAdapter(mapMarkerDataItems);
        markerListRV.setAdapter(markerDataAdapter);

        // Setup UI
        layerNameTextInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    showButtons(hasFocus);
                }
            }
        });

        layerCodeTextInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    showButtons(hasFocus);
                }
            }
        });

        visibilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showButtons(true);
            }
        });

        Log.i(TAG, "Layer selected: " + position + " (" + mapMarkerDataItems.size() + ") markers");
    }

    private void showButtons(boolean hasFocus) {
        if(hasFocus) {
            buttonLinearLayout.setVisibility(View.VISIBLE);
        } else {
            buttonLinearLayout.setVisibility(View.GONE);
        }
    }
}