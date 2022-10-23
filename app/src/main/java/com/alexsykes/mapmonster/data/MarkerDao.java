package com.alexsykes.mapmonster.data;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.MapInfo;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
import java.util.Map;

@Dao
public interface MarkerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMarker(MMarker marker);

    @Query("DELETE FROM markers")
    void deleteAllMarkers();

    @Query("DELETE FROM markers WHERE isArchived = 1")
    void deleteArchived();

    @MapInfo(keyColumn = "layername", valueColumn = "placename")
    @Query("SELECT layers.layername AS layername, layers.isVisible AS isVisible, markers.* FROM layers JOIN markers ON layers.layername = markers.layer_id WHERE markers.isArchived = 0 ORDER BY layername, placename")
    Map<String, List<MMarker>> getMarkersByLayer();

    @Query("SELECT markers.*, icons.iconFilename FROM markers, layers, icons  WHERE markers.layer_id = layers.layerID AND icons.iconID = layers.icon_id")
    LiveData<List<LiveMarkerItem>> getLiveMarkers();

    @Query("SELECT markers.*, layers.layername, icons.iconFilename AS filename FROM markers JOIN layers ON markers.layer_id = layers.layerID JOIN icons ON layers.icon_id = icons.iconID WHERE markers.isArchived = 0 ORDER BY placename")
    List<MapMarkerDataItem> getMarkerData();

    @Query("SELECT markers.*, layers.layername, icons.iconFilename AS filename FROM markers JOIN layers ON markers.layer_id = layers.layerID JOIN icons ON layers.icon_id = icons.iconID ORDER BY placename")
    LiveData<List<MMarker>>getLiveMarkerData();

    @Query("SELECT markers.*, layers.layername, icons.iconFilename AS filename FROM markers JOIN layers ON markers.layer_id = layers.layerID JOIN icons ON layers.icon_id = icons.iconID WHERE markers.isVisible AND layers.isVisible ORDER BY placename")
    List<MapMarkerDataItem> getVisibleMarkerDataList();

    @Query("SELECT markerID, placename, code, notes, latitude, longitude, layer_id FROM markers")
    Cursor getMarkerDataForExport();

    @Query("UPDATE markers SET latitude=:latitude, longitude=:longitude, placename=:placename, code=:code, layer_id =:layer_id, notes=:notes WHERE markerID = :markerID")
    void saveCurrentMarker(int markerID, double latitude, double longitude, String placename, String code, int layer_id, String notes);

    @Query("UPDATE markers SET isVisible = NOT isVisible WHERE markerID = :marker_id ")
    void toggle(int marker_id);

    @Query("SELECT markers.*, layers.layername, icons.iconFilename AS filename FROM markers JOIN layers ON markers.layer_id = layers.layerID JOIN icons ON layers.icon_id = icons.iconID ORDER BY markerID")
    List<MapMarkerDataItem> getAllMarkers();

    @Query("SELECT markers.*, layers.layername, icons.iconFilename AS filename FROM markers JOIN layers ON markers.layer_id = layers.layerID JOIN icons ON layers.icon_id = icons.iconID WHERE markers.markerID = :markerID ")
    MapMarkerDataItem getMMarker(int markerID);

    @Query("SELECT markers.*, layers.layername, icons.iconFilename AS filename FROM markers JOIN layers ON markers.layer_id = layers.layerID JOIN icons ON layers.icon_id = icons.iconID WHERE markers.isArchived = 0 AND layers.isVisible ORDER BY placename")
    List<MapMarkerDataItem> getMarkersFromVisibleLayers();

    @Query("UPDATE markers SET isVisible = :isVisible WHERE markerID = :marker_id")
    void setVisibility(int marker_id, boolean isVisible);

    @Query("UPDATE markers SET isVisible = NOT isVisible WHERE markerID = :marker_id")
    void toggleVisibility(int marker_id);

    @Query("UPDATE markers SET isArchived = 1 WHERE isSelected = 1 ")
    void archiveSelected();

    @Query("UPDATE markers SET isArchived = 0")
    void unarchiveAll();

    @Query("UPDATE markers SET isSelected = 0")
    void deselectAll();

    @Query("UPDATE markers SET isSelected = NOT isSelected WHERE markerID = :markerID")
    void toggleSelected(int markerID);

    @Query("UPDATE markers SET isSelected = 1")
    void selectAll();

    @Query("UPDATE markers SET isArchived = 1")
    void archiveAllMarkers();

    @Query("UPDATE markers SET code = :markerCode, placename = :markerName, notes = :markerNotes, latitude = :lat, longitude = :lng WHERE markerID = :markerId")
    void update(int markerId, String markerCode, String markerNotes, String markerName, double lat, double lng);

    @Query("UPDATE markers SET isArchived = 0")
    void restoreAllMarkers();
}