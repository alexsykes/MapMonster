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

//    private final LiveData<List<MMarker>> allMarkers;
    private final List<Integer> markerCountByLayer;
    private final List<MapMarkerDataItem>  markerList;

    public MarkerViewModel(@NonNull Application application) {
        super(application);
        markerRepository = new MarkerRepository(application);
//        allMarkers = markerRepository.getAllMarkers();
        markerCountByLayer = markerRepository.getAllMarkersByLayer();
        markerList = markerRepository.getMarkerList();
       // visibleMarkerList = markerRepository.getVisibleMarkerList();
    }

    // Mutators
    public void insert(MMarker marker) { markerRepository.insert(marker);}
    public void deleteMarker(int markerID) { markerRepository.deleteMarker(markerID); }
    public void updateMarker(int marker_id, double lat, double lng, boolean isUpdated) {markerRepository.updateMarker(marker_id,  lat,  lng,  isUpdated); }
    public void updateMarker(int markerId, String markerCode, String markerNotes, String markerName, double lat, double lng) { markerRepository.updateMarker(markerId, markerCode, markerNotes, markerName, lat, lng); }
    public void archiveAll() {
        markerRepository.archiveAllMarkers();
    }

    // Accessors
//    public LiveData<List<MMarker>> getAllMarkers() { return  allMarkers; }
    public List<Integer> getAllMarkersByLayer() { return  markerCountByLayer; }
    public List<MapMarkerDataItem> getMarkerList() { return markerRepository.getMarkerList(); }
    public List<MMarker> getVisibleMarkerList(ArrayList<String> visibleLayerList) { return markerRepository.getVisibleMarkerList(visibleLayerList); }
    public Map<String, List<MMarker>> getMarkersByLayer() {  return markerRepository.getMarkersByLayer();    }
    public MMarker getMarker(int markerId) {
        return markerRepository.getMarker(markerId);
    }

    public void restoreAll()  {
        markerRepository.restoreAllMarkers();
    }

    public List<MapMarkerDataItem> getVisibleMarkerDataList() {
        return markerRepository.getVisibleMarkerDataList();
    }

    public void deleteAll() {
        markerRepository.deleteAll();
    }

    public List<MapMarkerDataItem> getMarkerListByLayer() {
        return markerRepository.getMarkerListByLayer();

    }

    public void saveCurrentMarker(MapMarkerDataItem currentMarker) {
        markerRepository.saveCurrentMarker(currentMarker);
    }
    public Cursor getMarkerDataForExport() {
        return markerRepository.getMarkerDataForExport();
    }

    public void toggle(int marker_id) {
        markerRepository.toggle(marker_id);
    }

    public List<MapMarkerDataItem> getActiveMarkers() {
        return markerRepository.getActiveMarkers();
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
}


