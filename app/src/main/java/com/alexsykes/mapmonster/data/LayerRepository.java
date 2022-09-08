package com.alexsykes.mapmonster.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Set;

public class LayerRepository {
    private LayerDao layerDao;
    private LiveData<List<Layer>> allLabels;
    private List<Layer> layerList;
    private List<String> visibleLayerList;
    private List<LayerDataItem> layerDataItems;
    private LayerDataItem layerDataItem;

    LayerRepository(Application application) {
        MMDatabase db = MMDatabase.getDatabase(application);
        layerDao = db.layerDao();
        allLabels = layerDao.allLayers();
        layerList = layerDao.getLayerList();
        visibleLayerList = layerDao.getVisibleLayerList();
        layerDataItems = layerDao.getLayerData();
//        layerDataItem = layerDao.getLayerDataItem();
    }

    LiveData<List<Layer>> getAllLabels() { return allLabels; }
    public List<Layer> getLayerList() { return layerList; }
    List<String> getVisibleLayerList() { return visibleLayerList; };
    List<LayerDataItem> getLayerData() { return layerDataItems; }
    LayerDataItem getLayerDataItem(int position) { return layerDataItems.get(position); }
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
}
