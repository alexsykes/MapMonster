package com.alexsykes.mapmonster.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface IconDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertIcon(Icon icon);

    @Query("SELECT * FROM icons ORDER BY iconID")
    LiveData<List<Icon>> getIconList();

    @Query("DELETE FROM icons")
    void deleteAllIcons();

    @Query("SELECT * FROM icons WHERE iconFilename = :filename")
    Icon getIconByFilename(String filename);
}
