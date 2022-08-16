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

    @Query("UPDATE layers SET isArchived = 1")
    void archiveAllLayers();

    @Query("SELECT * FROM layers ORDER BY layername")
    LiveData<List<Layer>> allLayers();

    @Query("SELECT * FROM layers ORDER BY layername")
    List<Layer> getLayerList();

    @Query("UPDATE layers SET isVisible = :visibility WHERE layerID = :layerID")
    void setVisibility (boolean visibility, int layerID);

    @Query("UPDATE layers SET isVisible = :visibility WHERE layername = :layer")
    void setVisibility (String layer, boolean visibility);

    @Query("SELECT layername FROM layers WHERE isVisible = '1' AND isArchived = 0" )
    List<String> getVisibleLayerList();

    @Query("UPDATE layers SET isVisible = :b")
    void setVisibilityForAll(boolean b);

    @Query("UPDATE layers SET isVisible = 1 WHERE layername IN (:values) ")
    void updateLayerVisibility(String[] values);
}
