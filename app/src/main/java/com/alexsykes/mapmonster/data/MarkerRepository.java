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
    }

    LiveData<List<MMarker>> getAllMarkers() {
        return allMarkers;
    }
    List<Integer> getAllMarkersByLayer() {
        return markerCountByLayer;
    }
    public List<MMarker> getMarkerList() { return markerList; }
    public List<MMarker> getVisibleMarkerList() { return visibleMarkerList; }
    public Map<String, List<MMarker>> getMarkersByLayer() {
        markerMap = markerDao.getMarkersByLayer();
        return markerMap;  }

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

    public void updateMarker(int markerId, String markerCode, String markerNotes, String markerName, double lat, double lng) {
        markerDao.update(markerId, markerCode, markerNotes, markerName, lat, lng);
    }

    public MMarker getMarker(int markerId) {
        return markerDao.getMarker(markerId);
    }

    public List<MMarker> getVisibleMarkerList(ArrayList<String> visibleLayerList) {
        visibleMarkerList = markerDao.getVisibleMarkerList(visibleLayerList);
        return visibleMarkerList;
    }

    public void archiveAllMarkers() {
        markerDao.archiveAllMarkers();
    }

    public void restoreAllMarkers() {
        markerDao.restoreAllMarkers();
    }
}
