package com.alexsykes.mapmonster.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MarkerRepository {
    private MarkerDao markerDao;
    private LiveData<List<Marker>> allMarkers;
    private List<Marker> markerList;

    MarkerRepository(Application application) {
        MMDatabase db = MMDatabase.getDatabase(application);

        markerDao = db.markerDao();
        allMarkers = markerDao.allMarkers();
        markerList = markerDao.getMarkerList();
    }

    LiveData<List<Marker>> getAllMarkers() {
        return allMarkers;
    }
    public List<Marker> getMarkerList() { return markerList; }

    void insert(Marker marker) {
        MMDatabase.databaseWriteExecutor.execute(()  -> {
            markerDao.insertMarker(marker);
        });
    }
}
