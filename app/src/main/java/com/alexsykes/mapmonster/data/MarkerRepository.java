package com.alexsykes.mapmonster.data;

import android.app.Application;
import android.database.Cursor;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarkerRepository {
    private final MarkerDao markerDao;
//    private final LiveData<List<MMarker>> allMarkers;
    private final List<Integer> markerCountByLayer;
    private List<MapMarkerDataItem> markerList;
    private List<MMarker> visibleMarkerList;
    private Map<String, List<MMarker>> markerMap;
    private ArrayList<String> layerNames;
    LiveData<List<MMarker>> liveMarkers;
    public static final String TAG = "Info";

    MarkerRepository(Application application) {
        MMDatabase db = MMDatabase.getDatabase(application);

        markerDao = db.markerDao();
//        allMarkers = markerDao.allMarkers();
        markerList = markerDao.getMarkerData();
        markerCountByLayer = markerDao.markerCountByLayer();
        markerMap = markerDao.getMarkersByLayer();
        liveMarkers = markerDao.getLiveMarkerData();
        Log.i(TAG, "MarkerRepository: " + liveMarkers);
    }

    LiveData<List<MMarker>> getLiveMarkers() {
        return liveMarkers;
    }

    List<Integer> getAllMarkersByLayer() {
        return markerCountByLayer;
    }
    public List<MapMarkerDataItem> getMarkerList() { return markerDao.getMarkerData(); }
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

    public List<MapMarkerDataItem> getVisibleMarkerDataList() {
        return markerDao.getVisibleMarkerDataList();
    }

    public void deleteAll() {
        markerDao.deleteAllMarkers();
    }

    public List<MapMarkerDataItem> getMarkerListByLayer() {
        return markerDao.getMarkerListByLayer();
    }

    public void saveCurrentMarker(MapMarkerDataItem currentMarker) {
        int layerID = currentMarker.layer_id;
        double latitude = currentMarker.latitude;

        if (currentMarker.markerID != 0) {
            markerDao.saveCurrentMarker(currentMarker.markerID, currentMarker.latitude, currentMarker.longitude, currentMarker.placename, currentMarker.code, currentMarker.layer_id, currentMarker.getNotes());
        } else {
            markerDao.insertMarker(new MMarker(currentMarker.latitude, currentMarker.longitude, currentMarker.placename, currentMarker.code, currentMarker.layer_id, currentMarker.getNotes()));
        }
    }

    public Cursor getMarkerDataForExport() {
        return markerDao.getMarkerDataForExport();
    }

    public void toggle(int marker_id) {
        markerDao.toggle(marker_id);
    }

    public List<MapMarkerDataItem> getActiveMarkers() {
        return markerDao.getActiveMarkers();
    }

    public List<MapMarkerDataItem> getMarkersFromVisibleLayers() {
        return markerDao.getMarkersFromVisibleLayers();
    }

    public MapMarkerDataItem getMMarker(int markerID) {
        return markerDao.getMMarker(markerID);
    }

    public List<MapMarkerDataItem> getAllMarkers() {
        return markerDao.getAllMarkers();
    }

    public void archive(int marker_id, boolean isArchived) {
        markerDao.archive(marker_id, isArchived);
    }

    public void setVisibility(int marker_id, boolean isVisible) {
        markerDao.setVisibility(marker_id, isVisible);
    }

    public void archiveSelected() {
        markerDao.archiveSelected();
    }

    public void deleteArchived() {
        markerDao.deleteArchived();
    }

    public void unarchiveAll() {
        markerDao.unarchiveAll();
    }

    public void setSelected(int markerID, boolean isSelected) {
        markerDao.setSelected(markerID, isSelected);
    }

    public void deselectAll() {
        markerDao.deselectAll();
    }

    public void selectAll() {
        markerDao.selectAll();
    }
}
