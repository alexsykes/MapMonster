package com.alexsykes.mapmonster.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class LayerViewModel extends AndroidViewModel {
    private LayerRepository layerRepository;
    private final LiveData<List<Layer>> allLayers;
    private final List<Layer> layerList;
    private final List<String> visibleLayerList;

    public LayerViewModel(@NonNull Application application) {
        super(application);
        layerRepository = new LayerRepository(application);
        allLayers = layerRepository.getAllLabels();
        layerList = layerRepository.getLayerList();
        visibleLayerList = layerRepository.getVisibleLayerList();
    }

    public LiveData<List<Layer>> getAllLayers() { return allLayers; }
    public void insert(Layer layer) {layerRepository.insert(layer); }
    public List<Layer> getLayerList() { return layerRepository.getLayerList();  }
    public void setVisibility(boolean isVisible, int layerID) { layerRepository.setVisibility(isVisible,layerID); }
    public List<String> getVisibleLayerList() { return  layerRepository.getVisibleLayerList(); }

    public void setVisibility(String layerName, boolean visibility) {
        layerRepository.setVisibility(layerName, visibility);
    }

    public void setVisibilityForAll(boolean b) {
        layerRepository.setVisibilityForAll(b);
    }
}
