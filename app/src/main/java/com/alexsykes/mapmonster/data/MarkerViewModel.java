package com.alexsykes.mapmonster.data;

import android.app.Application;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarkerViewModel extends AndroidViewModel {
    private final MarkerRepository markerRepository;
    public static final String TAG = "Info";
    LiveData<List<MMarker>> liveMarkerData;

    public MarkerViewModel(@NonNull Application application) {
        super(application);
        markerRepository = new MarkerRepository(application);
        liveMarkerData = markerRepository.getLiveMarkerData();
    }

    // Mutators
    public void insert(MMarker marker) { markerRepository.insert(marker);}
    public void archiveAll() {
        markerRepository.archiveAllMarkers();
    }

    // Accessors
    public LiveData<List<MMarker>> getLiveMarkerData() { return liveMarkerData; }
    public List<MapMarkerDataItem> getMarkerList() { return markerRepository.getMarkerList(); }
    public Map<String, List<MMarker>> getMarkersByLayer() {  return markerRepository.getMarkersByLayer();    }
    public List<MapMarkerDataItem> getVisibleMarkerDataList() {
        return markerRepository.getVisibleMarkerDataList();
    }
    public void saveCurrentMarker(MapMarkerDataItem currentMarker) {
        markerRepository.saveCurrentMarker(currentMarker);
    }
    public Cursor getMarkerDataForExport() {
        return markerRepository.getMarkerDataForExport();
    }
    public List<MapMarkerDataItem> getMarkersFromVisibleLayers() {
        return markerRepository.getMarkersFromVisibleLayers();
    }
    public MapMarkerDataItem getMMarker(int markerID) {
        return markerRepository.getMMarker(markerID);
    }
    public List<MapMarkerDataItem> getAllMarkers() {
        return markerRepository.getAllMarkers();
    }

    // Utility
    public void setVisibility(int marker_id, boolean isVisible) {
        markerRepository.setVisibility(marker_id, isVisible);
    }
    public void archiveSelected() {
        markerRepository.archiveSelected();
    }
    public void deleteArchived() {
        markerRepository.deleteArchived();
    }
    public void unarchiveAll() {
        markerRepository.unarchiveAll();
    }
    public void selected(int markerID, boolean isSelected) {
        markerRepository.setSelected(markerID, isSelected);
    }
    public void deselectAll() {
        markerRepository.deselectAll();
    }
    public void selectAll() {
        markerRepository.selectAll();
    }
    public void restoreAll()  {
        markerRepository.restoreAllMarkers();
    }
    public void toggle(int marker_id) {
        markerRepository.toggle(marker_id);
    }
}


