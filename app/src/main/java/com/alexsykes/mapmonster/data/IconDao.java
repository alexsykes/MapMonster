package com.alexsykes.mapmonster.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface IconDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertIcon(Icon icon);

    @Query("SELECT * FROM icons ORDER BY name")
    List<Icon> getIconList();

    @Query("DELETE FROM icons")
    void deleteAllIcons();
}