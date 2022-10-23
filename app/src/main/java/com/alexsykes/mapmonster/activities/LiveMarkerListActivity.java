package com.alexsykes.mapmonster.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.LiveMarkerListAdapter;
import com.alexsykes.mapmonster.data.MarkerViewModel;

public class LiveMarkerListActivity extends AppCompatActivity {
    public static final String TAG = "Info";
    MarkerViewModel markerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_marker_list);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        final LiveMarkerListAdapter adapter = new LiveMarkerListAdapter(new LiveMarkerListAdapter.MMarkerDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);
        markerViewModel.getLiveMarkers().observe(this, mMarkers -> {
            adapter.submitList(mMarkers);
        });
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
    }
    private void emptyTrash() {
        markerViewModel.deleteArchived();
    }
    private void archiveSelected() {
        markerViewModel.archiveSelected();
        selectNone();
    }
    private void selectNone() {
        markerViewModel.deselectAll();
    }
    private void selectAll() {
        markerViewModel.selectAll();
    }


    public void visibilityToggled(int markerID) {
        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);
        markerViewModel.toggleVisibility(markerID);
    }
    public void selectionToggled(int markerID) {
        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);
        markerViewModel.toggleSelected(markerID);
    }
}