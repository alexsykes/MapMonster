package com.alexsykes.mapmonster.data;

import android.app.Application;
import android.database.Cursor;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Map;

public class MarkerRepository {
    private final MarkerDao markerDao;
    LiveData<List<MMarker>> liveMarkerData;
    private Map<String, List<MMarker>> markerMap;
    public static final String TAG = "Info";
    LiveData<List<LiveMarkerItem>> liveMarkers;
    List<LiveMarkerItem> visibleMarkerList;
    Cursor markerDataForExport;
    List<LiveMarkerItem> markersFromVisibleLayers;
    List<LiveMarkerItem> allMarkers;
    List<LiveMarkerItem> markerList;
    LiveMarkerItem markerDataItem;
    LiveData<List<LiveMarkerItem>> visibleLiveMarkerDataItems;

    MarkerRepository(Application application) {
        MMDatabase db = MMDatabase.getDatabase(application);

        markerDao = db.markerDao();
        markerMap = markerDao.getMarkersByLayer();
        liveMarkerData = markerDao.getLiveMarkerData();
        liveMarkers = markerDao.getLiveMarkers();
        visibleMarkerList = markerDao.getVisibleMarkerDataList();
        markerDataForExport = markerDao.getMarkerDataForExport();
        markersFromVisibleLayers = markerDao.getMarkersFromVisibleLayers();
        allMarkers = markerDao.getAllMarkers();
        markerList = markerDao.getMarkerData();
        markerMap = markerMap = markerDao.getMarkersByLayer();
        visibleLiveMarkerDataItems = markerDao.getVisibleLiveMarkerDataList();
    }

    void insert(MMarker marker) {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            markerDao.insertMarker(marker);
        });
    }

    public void saveCurrentMarker(LiveMarkerItem currentMarker) {
        int layerID = currentMarker.getLayer_id();
        double latitude = currentMarker.getLatitude();

        if (currentMarker.getMarkerID() != 0) {
            markerDao.saveCurrentMarker(currentMarker.getMarkerID(), currentMarker.getLatitude(),
                    currentMarker.getLongitude(), currentMarker.getPlacename(), currentMarker.getCode(),
                    currentMarker.getLayer_id(), currentMarker.getNotes());
        } else {
            markerDao.insertMarker(new MMarker(currentMarker.getLatitude(), currentMarker.getLongitude(),
                    currentMarker.getPlacename(), currentMarker.getCode(), currentMarker.getLayer_id(),
                    currentMarker.getNotes()));
        }
    }

    public LiveData<List<LiveMarkerItem>> getLiveMarkers() {
        return liveMarkers;
    }
    public List<LiveMarkerItem> getVisibleMarkerDataList() {
        return visibleMarkerList;
    }
    public Cursor getMarkerDataForExport() {
        return markerDataForExport;
    }
    public List<LiveMarkerItem> getMarkersFromVisibleLayers() {
        return markersFromVisibleLayers;
    }
    public List<LiveMarkerItem> getAllMarkers() {
        return allMarkers;
    }
    public List<LiveMarkerItem> getMarkerList() {
        return markerList;
    }
    public Map<String, List<MMarker>> getMarkersByLayer() {
        return markerMap;
    }
    LiveData<List<MMarker>> getLiveMarkerData() {
        return liveMarkerData;
    }

    public LiveMarkerItem getCurrentMarker(int markerID) {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            markerDataItem = markerDao.getCurrentMarker(markerID);
        });
        return markerDataItem;
    }

    public void toggleVisibility(int marker_id) {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            markerDao.toggleVisibility(marker_id);
        });

    }

    public void archiveSelected() {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            markerDao.archiveSelected();
        });
    }

    public void deleteArchived() {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            markerDao.deleteArchived();
        });
    }

    public void unarchiveAll() {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            markerDao.unarchiveAll();
        });
    }

    public void toggleSelected(int markerID) {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            markerDao.toggleSelected(markerID);
        });
    }

    public void deselectAll() {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            markerDao.deselectAll();
        });
    }

    public void selectAll() {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            markerDao.selectAll();
        });
    }

    public void archiveAllMarkers() {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            markerDao.archiveAllMarkers();
        });
    }

    public void restoreAllMarkers() {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            markerDao.restoreAllMarkers();
        });
    }

    public void toggle(int marker_id) {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            markerDao.toggle(marker_id);
        });
    }

    public LiveData<List<LiveMarkerItem>>  getLiveMarkerDataItems() { return visibleLiveMarkerDataItems;
    }
}

