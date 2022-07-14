package com.alexsykes.mapmonster.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Map;

public class MarkerRepository {
    private MarkerDao markerDao;
    private LiveData<List<MMarker>> allMarkers;
    private List<Integer> allMarkersByLayer;
    private List<MMarker> markerList;
    private Map<String, List<MMarker>> map;

    MarkerRepository(Application application) {
        MMDatabase db = MMDatabase.getDatabase(application);

        markerDao = db.markerDao();
        allMarkers = markerDao.allMarkers();
        markerList = markerDao.getMarkerList();
        allMarkersByLayer = markerDao.allMarkersByLayer();
        map = markerDao.getMarkersByLayer();
    }

    LiveData<List<MMarker>> getAllMarkers() {
        return allMarkers;
    }
    List<Integer> getAllMarkersByLayer() {
        return allMarkersByLayer;
    }
    public List<MMarker> getMarkerList() { return markerList; }

    void insert(MMarker marker) {
        MMDatabase.databaseWriteExecutor.execute(()  -> {
            markerDao.insertMarker(marker);
        });
    }

    public void deleteMarker(int markerID) {
        markerDao.deleteMarker(markerID);
    }

    public void updateMarker(int marker_id, double lat, double lng, boolean isUpdated) {
        markerDao.updateMarker(marker_id, lat, lng, isUpdated);
    }
}
