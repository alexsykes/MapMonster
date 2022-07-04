package com.alexsykes.mapmonster.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MarkerViewModel extends AndroidViewModel {
    private MarkerRepository markerRepository;

    private final LiveData<List<Marker>> allMarkers;
    private final List<Marker>  markerList;

    public MarkerViewModel(@NonNull Application application) {
        super(application);
        markerRepository = new MarkerRepository(application);
        allMarkers = markerRepository.getAllMarkers();
        markerList = markerRepository.getMarkerList();
    }

    public LiveData<List<Marker>> getAllMarkers() { return  allMarkers; }
    public void insert(Marker marker) { markerRepository.insert(marker);}
    public List<Marker> getMarkerList() { return markerRepository.getMarkerList(); }
    public void deleteMarker(int markerID) { markerRepository.deleteMarker(markerID); }
}
