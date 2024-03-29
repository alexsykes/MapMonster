package com.alexsykes.mapmonster.data;

import android.app.Application;
import android.database.Cursor;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Set;

public class LayerRepository {
    private final LayerDao layerDao;
    private final LiveData<List<Layer>> allLabels;
    private final List<Layer> layerList;
    private final List<String> visibleLayerList;
    private final List<LayerDataItem> layerDataItems;
    private LayerDataItem layerDataItem;
    private final List<SpinnerData> layerListForSpinner;
    private final List<String> layernamesForSpinner;

    LayerRepository(Application application) {
        MMDatabase db = MMDatabase.getDatabase(application);
        layerDao = db.layerDao();
        allLabels = layerDao.allLayers();
        layerList = layerDao.getLayerList();
        visibleLayerList = layerDao.getVisibleLayerList();
        layerDataItems = layerDao.getLayerData();
        layerListForSpinner = layerDao.getLayerListForSpinner();
        layernamesForSpinner = layerDao.getLayernamesForSpinner();
//        layerDataItem = layerDao.getLayerDataItem();
    }

    LiveData<List<Layer>> getAllLayers() { return allLabels; }
    public List<Layer> getLayerList() { return layerList; }
    List<String> getVisibleLayerList() { return visibleLayerList; }

    List<LayerDataItem> getLayerData() { return layerDao.getLayerData(); }
    LayerDataItem getLayerDataItem(int position) { return layerDao.getLayerDataItem(position); }
    List<MapMarkerDataItem> getMapMarkerItems(int position) {
        return layerDao.getMapMarkerItems(position);
    }
    void insert(Layer layer) {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            layerDao.insertLayer(layer);
        });
    }

    public void setVisibility(boolean isVisible, int layerID) {
        layerDao.setVisibility(isVisible,layerID);
    }

    public void setVisibility(String layerName, boolean visibility) {
        layerDao.setVisibility(layerName, visibility);
    }

    public void setVisibilityForAll(boolean b) {
        layerDao.setVisibilityForAll(b);
    }

    public void updateLayerVisibility(Set<String> newValues) {
        layerDao.setVisibilityForAll(false);
        String[] values = newValues.toArray(new String [newValues.size()]);
        layerDao.updateLayerVisibility(values);
    }

    public void archiveAllLayers() {
        layerDao.archiveAllLayers();
    }

    public void updateLayer(LayerDataItem currentLayerDataItem) {
        layerDao.updateLayerData(currentLayerDataItem.layerID, currentLayerDataItem.icon_id, currentLayerDataItem.layername, currentLayerDataItem.code, currentLayerDataItem.isVisible);
    }

    public LiveData<List<Layer>> getLiveLayerList() {
        return  layerDao.getLiveLayerList();
    }

    public void insertLayer(LayerDataItem currentLayerDataItem) {
        Layer layer = new Layer(currentLayerDataItem.layername, currentLayerDataItem.code, currentLayerDataItem.icon_id, currentLayerDataItem.isVisible);
        layerDao.insertLayer(layer);
    }

    public void deleteAllLayers() {
        layerDao.deleteAllLayers();
    }

    public List<SpinnerData> getLayerListForSpinner() {
        return layerListForSpinner;
    }

    public List<String> getLayernamesForSpinner() {
        return layernamesForSpinner;
    }

    public void toggle(int layer_id) {
        layerDao.toggle(layer_id);
    }

    public Cursor getLayerDataForExport() {
        return layerDao.getLayerDataForExport();
    }
}
