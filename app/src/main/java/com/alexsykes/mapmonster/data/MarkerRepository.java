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
    LiveData<List<MMarker>> liveMarkerData;
    private Map<String, List<MMarker>> markerMap;
    public static final String TAG = "Info";

    MarkerRepository(Application application) {
        MMDatabase db = MMDatabase.getDatabase(application);

        markerDao = db.markerDao();
        markerMap = markerDao.getMarkersByLayer();
        liveMarkerData = markerDao.getLiveMarkerData();
    }

    void insert(MMarker marker) {
        MMDatabase.databaseWriteExecutor.execute(()  -> {
            markerDao.insertMarker(marker);
        });
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

    public List<MapMarkerDataItem> getVisibleMarkerDataList() {
        return markerDao.getVisibleMarkerDataList();
    }
    public Cursor getMarkerDataForExport() {
        return markerDao.getMarkerDataForExport();
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
    public List<MapMarkerDataItem> getMarkerList() { return markerDao.getMarkerData(); }
    public Map<String, List<MMarker>> getMarkersByLayer() {
        markerMap = markerDao.getMarkersByLayer();
        return markerMap;  }
    LiveData<List<MMarker>> getLiveMarkerData() {
        return liveMarkerData;
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
    public void archiveAllMarkers() {
        markerDao.archiveAllMarkers();
    }
    public void restoreAllMarkers() {
        markerDao.restoreAllMarkers();
    }

    public void toggle(int marker_id) {
        markerDao.toggle(marker_id);
    }
}
