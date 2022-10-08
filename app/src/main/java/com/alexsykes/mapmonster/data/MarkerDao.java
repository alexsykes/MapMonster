package com.alexsykes.mapmonster.data;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.MapInfo;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Dao
public interface MarkerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMarker(MMarker marker);

    @Query("DELETE FROM markers")
    void deleteAllMarkers();

    @Query("UPDATE markers SET isArchived = 1")
    void archiveAllMarkers();

    @Query("DELETE FROM markers WHERE markerID = :markerID")
    void deleteMarker(int markerID);

    @Query("SELECT * FROM markers ORDER BY placename")
    LiveData<List<MMarker>> allMarkers();

    @Query("SELECT COUNT(*) FROM markers GROUP BY layer_id ORDER BY layer_id, placename")
    List<Integer> markerCountByLayer();

    @Query("SELECT * FROM markers WHERE layer_id IN (:layerNames) AND isArchived = 0 ORDER BY layer_id, placename")
    List<MMarker> getVisibleMarkerList(ArrayList<String> layerNames);

    @Query("UPDATE markers SET latitude = :lat, longitude = :lng, isUpdated = :isUpdated WHERE markerID = :marker_id  ")
    void updateMarker(int marker_id, double lat, double lng, boolean isUpdated);

    @MapInfo(keyColumn = "layername", valueColumn = "placename")
    @Query("SELECT layers.layername AS layername, layers.isVisible AS isVisible, markers.* FROM layers JOIN markers ON layers.layername = markers.layer_id WHERE markers.isArchived = 0 ORDER BY layername, placename")
    Map<String, List<MMarker>> getMarkersByLayer();

    @Query("SELECT * FROM markers WHERE markerID = :tag")
    MMarker getMarker(int tag);

    @Query("UPDATE markers SET code = :markerCode, placename = :markerName, notes = :markerNotes, latitude = :lat, longitude = :lng WHERE markerID = :markerId")
    void update(int markerId, String markerCode, String markerNotes, String markerName, double lat, double lng);

    @Query("UPDATE markers SET isArchived = 0")
    void restoreAllMarkers();

    @Query("SELECT markers.*, layers.layername, icons.iconFilename AS filename FROM markers JOIN layers ON markers.layer_id = layers.layerID JOIN icons ON layers.icon_id = icons.iconID WHERE markers.isArchived = 0 ORDER BY placename")
    List<MapMarkerDataItem> getMarkerData();

    @Query("SELECT markers.*, layers.layername, icons.iconFilename AS filename FROM markers JOIN layers ON markers.layer_id = layers.layerID JOIN icons ON layers.icon_id = icons.iconID WHERE markers.isArchived = 0 AND layers.isVisible ORDER BY placename")
    List<MapMarkerDataItem> getVisibleMarkerDataList();

    @Query("SELECT markers.*, layers.layername, icons.iconFilename AS filename FROM markers JOIN layers ON markers.layer_id = layers.layerID JOIN icons ON layers.icon_id = icons.iconID ORDER BY layers.layername, placename")
    List<MapMarkerDataItem> getMarkerListByLayer();

    @Query("UPDATE markers SET latitude=:latitude, longitude=:longitude, placename=:placename, code=:code, layer_id =:layer_id, notes=:notes WHERE markerID = :markerID")
    void saveCurrentMarker(int markerID, double latitude, double longitude, String placename, String code, int layer_id, String notes);

    @Query("SELECT markerID, placename, code, notes, latitude, longitude FROM markers")
    Cursor getMarkerDataForExport();

}



