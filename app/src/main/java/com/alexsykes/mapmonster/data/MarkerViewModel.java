package com.alexsykes.mapmonster.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Map;

public class MarkerViewModel extends AndroidViewModel {
    private MarkerRepository markerRepository;

    private final LiveData<List<MMarker>> allMarkers;
    private final List<Integer> markerCountByLayer;
    private final List<MMarker>  markerList;
   // private final List<MMarker>  visibleMarkerList;

    public MarkerViewModel(@NonNull Application application) {
        super(application);
        markerRepository = new MarkerRepository(application);
        allMarkers = markerRepository.getAllMarkers();
        markerCountByLayer = markerRepository.getAllMarkersByLayer();
        markerList = markerRepository.getMarkerList();
       // visibleMarkerList = markerRepository.getVisibleMarkerList();
    }

    public LiveData<List<MMarker>> getAllMarkers() { return  allMarkers; }
    public List<Integer> getAllMarkersByLayer() { return  markerCountByLayer; }
    public void insert(MMarker marker) { markerRepository.insert(marker);}
    public List<MMarker> getMarkerList() { return markerRepository.getMarkerList(); }
    public List<MMarker> getVisibleMarkerList() { return markerRepository.getVisibleMarkerList(); }
    public void deleteMarker(int markerID) { markerRepository.deleteMarker(markerID); }
    public void updateMarker(int marker_id, double lat, double lng, boolean isUpdated) {markerRepository.updateMarker(marker_id,  lat,  lng,  isUpdated); }

    public Map<String, List<MMarker>> getMarkersByLayer() {
        return markerRepository.getMarkersByLayer();
    }
}

