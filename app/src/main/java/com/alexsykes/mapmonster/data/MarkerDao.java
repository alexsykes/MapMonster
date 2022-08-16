package com.alexsykes.mapmonster.data;

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

    @Query("SELECT COUNT(*) FROM markers GROUP BY type ORDER BY type, placename")
    List<Integer> markerCountByLayer();


    @Query("SELECT * FROM markers WHERE isArchived = 0 ORDER BY placename")
    List<MMarker> getMarkerList();

    @Query("SELECT * FROM markers WHERE type IN (:layerNames) ORDER BY type, placename")
    List<MMarker> getVisibleMarkerList(ArrayList<String> layerNames);

    @Query("UPDATE markers SET latitude = :lat, longitude = :lng, isUpdated = :isUpdated WHERE markerID = :marker_id  ")
    void updateMarker(int marker_id, double lat, double lng, boolean isUpdated);

    @MapInfo(keyColumn = "layername", valueColumn = "placename")
    @Query("SELECT layers.layername AS layername, layers.isVisible AS isVisible, markers.* FROM layers JOIN markers ON layers.layername = markers.type WHERE layers.isArchived = 0 ORDER BY layername, placename")
    public Map<String, List<MMarker>> getMarkersByLayer();

    @Query("SELECT * FROM markers WHERE markerID = :tag")
    MMarker getMarker(int tag);

    @Query("UPDATE markers SET code = :markerCode, placename = :markerName, notes = :markerNotes, latitude = :lat, longitude = :lng WHERE markerID = :markerId")
    void update(int markerId, String markerCode, String markerNotes, String markerName, double lat, double lng);
}



