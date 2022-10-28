package com.alexsykes.mapmonster.data;

import android.app.Application;
import android.database.Cursor;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Set;

public class LayerRepository {
    private final LayerDao layerDao;
    private final LiveData<List<Layer>> allLabels;
    private final LiveData<List<LiveLayerItem>> liveLayers;
    private final List<Layer> layerList;
    private final List<String> visibleLayerList;
    private final List<SpinnerData> layerListForSpinner;
    private final List<String> layernamesForSpinner;
    LiveData<List<Layer>>  liveLayerList;
    Cursor layerDataForExport;

    LayerRepository(Application application) {
        MMDatabase db = MMDatabase.getDatabase(application);
        layerDao = db.layerDao();
        allLabels = layerDao.allLayers();
        layerList = layerDao.getLayerList();
        visibleLayerList = layerDao.getVisibleLayerList();
        layerListForSpinner = layerDao.getLayerListForSpinner();
        layernamesForSpinner = layerDao.getLayernamesForSpinner();
        liveLayers = layerDao.getLiveLayers();
        liveLayerList = layerDao.getLiveLayerList();
    }

    LiveData<List<Layer>> getAllLayers() {
        return allLabels;
    }

    public List<Layer> getLayerList() {
        return layerList;
    }

    List<String> getVisibleLayerList() {
        return visibleLayerList;
    }

    LiveData<List<LiveLayerItem>> getLiveLayers() {
        return liveLayers;
    }


    List<LayerDataItem> getLayerData() {
        return layerDao.getLayerData();
    }

    LayerDataItem getLayerDataItem(int position) {
        return layerDao.getLayerDataItem(position);
    }

    List<LiveMarkerItem> getMapMarkerItems(int position) {
        return layerDao.getMapMarkerItems(position);
    }

    void insert(Layer layer) {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            layerDao.insertLayer(layer);
        });
    }

    public void setVisibility(boolean isVisible, int layerID) {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            layerDao.setVisibility(isVisible, layerID);
        });
    }

    public void setVisibility(String layerName, boolean visibility) {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            layerDao.setVisibility(layerName, visibility);
        });
    }

    public void setVisibilityForAll(boolean b) {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            layerDao.setVisibilityForAll(b);
        });
    }

    public void updateLayerVisibility(Set<String> newValues) {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            layerDao.setVisibilityForAll(false);
            String[] values = newValues.toArray(new String[newValues.size()]);
            layerDao.updateLayerVisibility(values);
        });
    }

    public void archiveAllLayers() {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            layerDao.archiveAllLayers();
        });
    }

    public void updateLayer(LayerDataItem currentLayerDataItem) {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            layerDao.updateLayerData(currentLayerDataItem.layerID, currentLayerDataItem.icon_id, currentLayerDataItem.layername, currentLayerDataItem.code, currentLayerDataItem.isVisible);
        });
    }

    public LiveData<List<Layer>> getLiveLayerList() {
        return liveLayerList;
    }


    public void insertLayer(LayerDataItem currentLayerDataItem) {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            Layer layer = new Layer(currentLayerDataItem.layername, currentLayerDataItem.code, currentLayerDataItem.icon_id, currentLayerDataItem.isVisible);
            layerDao.insertLayer(layer);
        });

    }

    public void deleteAllLayers() {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            layerDao.deleteAllLayers();
        });
    }

    public List<SpinnerData> getLayerListForSpinner() {
            return layerListForSpinner;
    }

    public List<String> getLayernamesForSpinner() {
            return layernamesForSpinner;
    };

    public void toggle(int layer_id) {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            layerDao.toggle(layer_id);
        });
    }

    public Cursor getLayerDataForExport() {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            layerDataForExport   = layerDao.getLayerDataForExport();
        });
        return layerDataForExport;
    }
}
