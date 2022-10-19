package com.alexsykes.mapmonster.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.Icon;
import com.alexsykes.mapmonster.data.MMDatabase;
import com.alexsykes.mapmonster.data.MapMarkerDataItem;
import com.alexsykes.mapmonster.data.MarkerViewModel;

import java.util.List;

public class LiveMarkerListActivity extends AppCompatActivity {
    public static final String TAG = "Info";
    MarkerViewModel markerViewModel;
    LiveData<List<MapMarkerDataItem>> liveData;
    List<MapMarkerDataItem> mapMarkerDataItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_marker_list);
        getData();
        Log.i(TAG, "onCreate: " + liveData.getValue());
    }


    private void getData() {
        // Get data
        MMDatabase db = MMDatabase.getDatabase(this);
        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);

        liveData = markerViewModel.getLiveMarkerList();
        mapMarkerDataItems = liveData.getValue();

    }
}