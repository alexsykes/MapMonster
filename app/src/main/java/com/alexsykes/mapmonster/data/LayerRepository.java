package com.alexsykes.mapmonster.data;

import android.app.Application;
import androidx.lifecycle.LiveData;

import java.util.List;

public class LayerRepository {
    private LayerDao layerDao;
    private LiveData<List<Layer>> allLabels;
    private List<Layer> layerList;

    LayerRepository(Application application) {
        MMDatabase db = MMDatabase.getDatabase(application);

        layerDao = db.layerDao();
        allLabels = layerDao.allLayers();
        layerList = layerDao.getLayerList();
    }

    LiveData<List<Layer>> getAllLabels() { return allLabels; }
    public List<Layer> getLayerList() { return layerList; }

    void insert(Layer layer) {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            layerDao.insertLayer(layer);
        });
    }

    public void setVisibility(boolean isVisible, int layerID) {
        layerDao.setVisibility(isVisible,layerID);
    }
}
