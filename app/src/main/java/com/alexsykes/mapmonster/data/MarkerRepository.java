package com.alexsykes.mapmonster.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarkerRepository {
    private MarkerDao markerDao;
    private LiveData<List<MMarker>> allMarkers;
    private List<Integer> markerCountByLayer;
    private List<MMarker> markerList;
    private List<MMarker> visibleMarkerList;
    private Map<String, List<MMarker>> markerMap;
    private ArrayList<String> layerNames;

    MarkerRepository(Application application) {
        MMDatabase db = MMDatabase.getDatabase(application);

        markerDao = db.markerDao();
        allMarkers = markerDao.allMarkers();
        markerList = markerDao.getMarkerList();
        markerCountByLayer = markerDao.markerCountByLayer();
        markerMap = markerDao.getMarkersByLayer();
//        visibleMarkerList = markerDao.getVisibleMarkerList(layerNames);

    }

    LiveData<List<MMarker>> getAllMarkers() {
        return allMarkers;
    }
    List<Integer> getAllMarkersByLayer() {
        return markerCountByLayer;
    }
    public List<MMarker> getMarkerList() { return markerList; }
    public List<MMarker> getVisibleMarkerList() { return visibleMarkerList; }
    public Map<String, List<MMarker>> getMarkersByLayer() { return markerMap;  }

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
