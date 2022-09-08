package com.alexsykes.mapmonster.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import com.alexsykes.mapmonster.LayerDataAdapter;
import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.Icon;
import com.alexsykes.mapmonster.data.IconViewModel;
import com.alexsykes.mapmonster.data.LayerDao;
import com.alexsykes.mapmonster.data.LayerDataItem;
import com.alexsykes.mapmonster.data.LayerViewModel;
import com.alexsykes.mapmonster.data.MMDatabase;
import com.alexsykes.mapmonster.data.MarkerViewModel;

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
    RecyclerView layerDataRV;
    TextView  layernameTextView, layerIdTextView, layerIconTextView, layerCodeTextView;
    SwitchCompat visibilitySwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layer_list);

        setupUI();
        getData();
        setupLayerRV();

    }

    private void setupUI() {
        layernameTextView = findViewById(R.id.layernameTextView);
        layerIconTextView = findViewById(R.id.layerIconTextView);
        layerCodeTextView = findViewById(R.id.layerCodeTextView);

        visibilitySwitch = findViewById(R.id.visibilitySwitch);
    }

    private void setupLayerRV() {
        layerDataRV = findViewById(R.id.layerDataRecyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        layerDataRV.setLayoutManager(llm);
        layerDataRV.setHasFixedSize(true);
        layerDataRV.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
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

    public void onClickCalled(int position) {
        Log.i(TAG, "Layer selected: " + position);
        LayerDataItem layerDataItem = layerViewModel.getLayerDataItem(position);

//        layerIdTextView.setText(String.valueOf(layerDataItem.getLayer_id()));
        layernameTextView.setText(layerDataItem.layername);
        layerCodeTextView.setText(layerDataItem.code);
        layerIconTextView.setText(layerDataItem.filename);

        boolean showOnMap = layerDataItem.isVisible;

        visibilitySwitch.setChecked(showOnMap);
    }
}