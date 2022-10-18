package com.alexsykes.mapmonster.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.alexsykes.mapmonster.MarkerListAdapter;
import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.Icon;
import com.alexsykes.mapmonster.data.IconViewModel;
import com.alexsykes.mapmonster.data.LayerDataItem;
import com.alexsykes.mapmonster.data.LayerViewModel;
import com.alexsykes.mapmonster.data.MMDatabase;
import com.alexsykes.mapmonster.data.MapMarkerDataItem;
import com.alexsykes.mapmonster.data.MarkerViewModel;

import java.util.ArrayList;
import java.util.List;

public class MarkerListActivity extends AppCompatActivity {
    //  Data
    private MarkerViewModel markerViewModel;
    private LayerViewModel layerViewModel;
    private IconViewModel iconViewModel;
    List<Icon> allIcons;
    List<LayerDataItem> allLayers;
    List<MapMarkerDataItem> allMarkers;
    public static final String TAG = "Info";
    ArrayList<Integer> theSelected;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_list);
        getData();
        setupUI();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.marker_list_menu, menu);
        return true;
    }    // Navigation

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.selectAll:
                selectAll();
                return true;

            case R.id.selectNone:
                selectNone();
                return true;

            case R.id.archiveSelected:
                archiveSelected();
                return true;

            case R.id.emptyTrash:
                emptyTrash();
                return true;

            case R.id.unarchiveAll:
                unarchiveAll();
                return true;
            default:
        }
        return false;
    }

    private void unarchiveAll() {
        markerViewModel.unarchiveAll();
        loadMarkerRV();
    }

    private void emptyTrash() {
        markerViewModel.deleteArchived();
        loadMarkerRV();
    }

    private void archiveSelected() {
        Log.i(TAG, "archiveSelected: " + theSelected);
        markerViewModel.archiveSelected();
        selectNone();
    }

    private void selectNone() {
        markerViewModel.deselectAll();
        loadMarkerRV();
    }

    private void selectAll() {
        markerViewModel.selectAll();
        loadMarkerRV();
    }

    private void setupUI() {
        recyclerView = findViewById(R.id.markerListRV);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        loadMarkerRV();
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
        theSelected = new ArrayList<Integer>();
    }

    public void onArchiveImageCalled(int marker_id, boolean isArchived) {
//        Log.i(TAG, "onArchiveImageCalled: " + marker_id);
        markerViewModel.archive(marker_id, isArchived);
//        Do not refresh RV
//        loadMarkerRV();
    }

    private void loadMarkerRV() {
        allMarkers = markerViewModel.getAllMarkers();
        final MarkerListAdapter markerListAdapter = new MarkerListAdapter(allMarkers);
        recyclerView.setAdapter(markerListAdapter);
    }

    public void onVisibleImageCalled(int markerID, boolean isVisible) {
        markerViewModel.setVisibility(markerID, isVisible);
    }

    public void onSelectedChanged(int markerID, boolean isSelected) {
//        Log.i(TAG, "onSelectedChanged: " + markerID + isChecked);
//        if(isChecked) {
//            theSelected.add(markerID);
//        } else  {
//                theSelected.remove(theSelected.indexOf(markerID));
//            }
//        Log.i(TAG, "theSelected: " + theSelected);
        markerViewModel.selected(markerID, isSelected);
    }
}