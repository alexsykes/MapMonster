package com.alexsykes.mapmonster.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LayerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLayer(Layer layer);

    @Query("DELETE FROM layers")
    void deleteAllLayers();

    @Query("SELECT * FROM layers ORDER BY layername")
    LiveData<List<Layer>> allLayers();


    @Query("SELECT * FROM layers ORDER BY layername")
    List<Layer> getLayerList();
}
