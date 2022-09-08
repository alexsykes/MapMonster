package com.alexsykes.mapmonster.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Set;

public class LayerViewModel extends AndroidViewModel {
    private LayerRepository layerRepository;
    private final LiveData<List<Layer>> allLayers;
    private final List<Layer> layerList;
    private final List<String> visibleLayerList;
    private final List<LayerDataItem> layerDataItems;
    private  LayerDataItem layerDataItem;

    public LayerViewModel(@NonNull Application application) {
        super(application);
        layerRepository = new LayerRepository(application);
        layerDataItems = layerRepository.getLayerData();
        allLayers = layerRepository.getAllLabels();
        layerList = layerRepository.getLayerList();
        visibleLayerList = layerRepository.getVisibleLayerList();

    }

    public LiveData<List<Layer>> getAllLayers() { return allLayers; }
    public List<Layer> getLayerList() { return layerRepository.getLayerList();  }
    public List<LayerDataItem> getLayerData() { return layerRepository.getLayerData();  }
    public List<String> getVisibleLayerList() { return  layerRepository.getVisibleLayerList(); }

    public void insert(Layer layer) {layerRepository.insert(layer); }
    public void setVisibility(boolean isVisible, int layerID) { layerRepository.setVisibility(isVisible,layerID); }

    public void setVisibility(String layerName, boolean visibility) {
        layerRepository.setVisibility(layerName, visibility);
    }

    public void setVisibilityForAll(boolean b) {
        layerRepository.setVisibilityForAll(b);
    }

    public void updateLayerVisibility(Set<String> newValue) {
        layerRepository.updateLayerVisibility(newValue);
    }

    public void archiveAll() {
        layerRepository.archiveAllLayers();
    }

    public LayerDataItem getLayerDataItem(int position) {
        return layerRepository.getLayerDataItem(position);
    }

    public List<MapMarkerDataItem> getMapMarkerItems(int position) {
        return layerRepository.getMapMarkerItems(position);
    }
}
