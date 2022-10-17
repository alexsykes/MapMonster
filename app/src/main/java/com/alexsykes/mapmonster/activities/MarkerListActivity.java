package com.alexsykes.mapmonster.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.alexsykes.mapmonster.MarkerDataAdapter;
import com.alexsykes.mapmonster.MarkerListAdapter;
import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.Icon;
import com.alexsykes.mapmonster.data.IconViewModel;
import com.alexsykes.mapmonster.data.LayerDataItem;
import com.alexsykes.mapmonster.data.LayerViewModel;
import com.alexsykes.mapmonster.data.MMDatabase;
import com.alexsykes.mapmonster.data.MapMarkerDataItem;
import com.alexsykes.mapmonster.data.MarkerViewModel;

import java.util.List;

public class MarkerListActivity extends AppCompatActivity {
    //  Data
    private MarkerViewModel markerViewModel;
    private LayerViewModel layerViewModel;
    private IconViewModel iconViewModel;
    List<Icon> allIcons;
    List<LayerDataItem> allLayers;
    List<MapMarkerDataItem> allMarkers;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_list);
        getData();
        setupUI();
    }

    private void setupUI() {

        setupMarkerRV();
    }

    private void setupMarkerRV() {
        allMarkers = markerViewModel.getAllMarkers();
        recyclerView = findViewById(R.id.markerListRV);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        final MarkerListAdapter markerListAdapter = new MarkerListAdapter(allMarkers);
        recyclerView.setAdapter(markerListAdapter);
    }

    // Setup methods
    private void getData() {
        MMDatabase db = MMDatabase.getDatabase(this);
        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);
        layerViewModel = new ViewModelProvider(this).get(LayerViewModel.class);
        iconViewModel = new ViewModelProvider(this).get(IconViewModel.class);
        allIcons = iconViewModel.getIconList();
        allLayers = layerViewModel.getLayerData();

        allMarkers = markerViewModel.getAllMarkers();
    }

}