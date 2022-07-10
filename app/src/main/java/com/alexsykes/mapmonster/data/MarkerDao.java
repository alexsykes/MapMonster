package com.alexsykes.mapmonster.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MarkerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMarker(MMarker marker);

    @Query("DELETE FROM markers")
    void deleteAllMarkers();

    @Query("DELETE FROM markers WHERE markerID = :markerID")
    void deleteMarker(int markerID);

    @Query("SELECT * FROM markers ORDER BY placename")
    LiveData<List<MMarker>> allMarkers();


    @Query("SELECT COUNT(*) FROM markers GROUP BY type ORDER BY type, placename")
    List<Integer> allMarkersByLayer();


    @Query("SELECT * FROM markers ORDER BY placename")
    List<MMarker> getMarkerList();

    @Query("UPDATE markers SET latitude = :lat, longitude = :lng, isUpdated = :isUpdated WHERE markerID = :marker_id  ")
    void updateMarker(int marker_id, double lat, double lng, boolean isUpdated);
}



