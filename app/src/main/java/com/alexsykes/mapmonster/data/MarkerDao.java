package com.alexsykes.mapmonster.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.lang.annotation.Annotation;
import java.util.List;

@Dao
public interface MarkerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMarker(Marker marker);

    @Query("DELETE FROM markers")
    void deleteAllMarkers();

    @Query("SELECT * FROM markers ORDER BY placename")
    LiveData<List<Marker>> allMarkers();


    @Query("SELECT * FROM markers ORDER BY placename")
    List<Marker> getMarkerList();







}



