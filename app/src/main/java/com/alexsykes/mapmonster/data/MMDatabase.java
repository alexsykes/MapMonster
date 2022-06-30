package com.alexsykes.mapmonster.data;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Marker.class, Layer.class}, version = 1, exportSchema = false)

public abstract class MMDatabase extends RoomDatabase{
    public abstract MarkerDao markerDao();

    private static volatile MMDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static MMDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (MMDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    MMDatabase.class, "mm_database")
                            .allowMainThreadQueries()
                            .addCallback(roomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback()
    {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                MarkerDao markerDao = INSTANCE.markerDao();
                markerDao.deleteAllMarkers();

                Marker marker = new Marker(-2.59, 52.69,"Place 1", "P1");
                markerDao.insertMarker(marker);
                marker = new Marker(-2.61, 52.74,"Place 2", "P2");
                markerDao.insertMarker(marker);
                marker = new Marker(-2.59, 52.69,"Place 1", "P1");
                markerDao.insertMarker(marker);
                marker = new Marker(-2.61, 52.74,"Place 2", "P2");
                markerDao.insertMarker(marker);
                marker = new Marker(-2.59, 52.69,"Place 1", "P1");
                markerDao.insertMarker(marker);
                marker = new Marker(-2.61, 52.74,"Place 2", "P2");
                markerDao.insertMarker(marker);
                marker = new Marker(-2.61, 52.74,"Place 2", "P2");
                markerDao.insertMarker(marker);
                marker = new Marker(-2.59, 52.69,"Place 1", "P1");
                markerDao.insertMarker(marker);
                marker = new Marker(-2.61, 52.74,"Place 2", "P2");
                markerDao.insertMarker(marker);
                marker = new Marker(-2.59, 52.69,"Place 1", "P1");
                markerDao.insertMarker(marker);
                marker = new Marker(-2.61, 52.74,"Place 2", "P2");
                markerDao.insertMarker(marker);
            });
        }
    };
}
