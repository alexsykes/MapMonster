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

    @Query("SELECT layers.*, icons.filename FROM layers JOIN icons ON layers.icon_id = icons.iconID WHERE isArchived = 0 ORDER BY layername" )
    List<LayerDataItem> getLayerData();

    class LayerDataItem {
        public int layerID;
        public int icon_id;
        public String layername, code, filename;
        public boolean isVisible, isArchived;


        public int getLayer_id() {
            return layerID;
        }

        public void setLayer_id(int layer_id) {
            this.layerID = layer_id;
        }

        public int getIcon_id() {
            return icon_id;
        }

        public void setIcon_id(int icon_id) {
            this.icon_id = icon_id;
        }

        public String getLayername() {
            return layername;
        }

        public void setLayername(String layername) {
            this.layername = layername;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public boolean isVisible() {
            return isVisible;
        }

        public void setVisible(boolean visible) {
            isVisible = visible;
        }

        public boolean isArchived() {
            return isArchived;
        }

        public void setArchived(boolean archived) {
            isArchived = archived;
        }
    }
}
