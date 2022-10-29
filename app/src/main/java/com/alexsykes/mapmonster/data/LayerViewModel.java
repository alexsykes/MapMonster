package com.alexsykes.mapmonster.data;

import android.app.Application;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Set;

public class LayerViewModel extends AndroidViewModel {
    private final LayerRepository layerRepository;
    private final LiveData<List<Layer>> allLayers;
    private final List<Layer> layerList;
    private final List<String> visibleLayerList;
    private final List<LayerDataItem> layerDataItems;
    private  LayerDataItem layerDataItem;
    private final LiveData<List<Layer>> liveLayerList;
    private final List<String> layernamesForSpinner;
    List<SpinnerData> layerListForSpinner;

    public LayerViewModel(@NonNull Application application) {
        super(application);
        layerRepository = new LayerRepository(application);
        layerDataItems = layerRepository.getLayerData();
        allLayers = layerRepository.getAllLayers();
        layerList = layerRepository.getLayerList();
        visibleLayerList = layerRepository.getVisibleLayerList();
        liveLayerList = layerRepository.getLiveLayerList();
        layernamesForSpinner = layerRepository.getLayernamesForSpinner();
        layerListForSpinner = layerRepository.getLayerListForSpinner();
    }


    public List<LayerDataItem> getLayerData() {
        return layerRepository.getLayerData();
    }

    public List<String> getLayernamesForSpinner() {
        return layernamesForSpinner;
    }


    public void insert(Layer layer) {
        layerRepository.insert(layer);
    }

    public LayerDataItem getLayerDataItem(int position) {
        return layerRepository.getLayerDataItem(position);
    }

    public List<LiveMarkerItem> getMapMarkerItems(int position) {
        return layerRepository.getMapMarkerItems(position);
    }

    public void updateLayer(LayerDataItem currentLayerDataItem) {
        layerRepository.updateLayer(currentLayerDataItem);
    }

    public void insertLayer(LayerDataItem currentLayerDataItem) {
        layerRepository.insertLayer(currentLayerDataItem);
    }

    public List<SpinnerData> getLayerListForSpinner() {
        return layerListForSpinner;
    }


    public void toggle(int layer_id) {
        layerRepository.toggle(layer_id);
    }

    public Cursor getLayerDataForExport() {
        return  layerRepository.getLayerDataForExport();
    }

    public LiveData<List<LiveLayerItem>> getLiveLayers() { return layerRepository.getLiveLayers(); }

//    public void deleteAll() {
//        layerRepository.deleteAllLayers();
//    }
//
//    public void setVisibility(boolean isVisible, int layerID) {
//        layerRepository.setVisibility(isVisible, layerID);
//    }
//
//    public void setVisibility(String layerName, boolean visibility) {
//        layerRepository.setVisibility(layerName, visibility);
//    }
//
//    public void setVisibilityForAll(boolean b) {
//        layerRepository.setVisibilityForAll(b);
//    }
//
//    public void updateLayerVisibility(Set<String> newValue) {
//        layerRepository.updateLayerVisibility(newValue);
//    }
//
//    public void archiveAll() {
//        layerRepository.archiveAllLayers();
//    }
//
//public LiveData<List<Layer>> getAllLayers() {
//    return allLayers;
//}
//
//    public List<Layer> getLayerList() {
//        return layerRepository.getLayerList();
//    }
//
//    public List<String> getVisibleLayerList() {
//        return layerRepository.getVisibleLayerList();
//    }
}
