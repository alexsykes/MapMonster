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

                Marker marker = new Marker(53.59, -2.61,"Place 1", "P1");
                markerDao.insertMarker(marker);
                marker = new Marker(53.6687, -2.509,"Place 2", "P2");
                markerDao.insertMarker(marker);
                marker = new Marker(53.6897, -2.535568,"Place 3", "P3");
                markerDao.insertMarker(marker);
                marker = new Marker(53.6701, -2.5789,"Place 4", "P4");
                markerDao.insertMarker(marker);
                marker = new Marker(53.66, -2.579,"Place 6", "P6");
                markerDao.insertMarker(marker);
                marker = new Marker(53.6087, -2.556699,"Place 5", "P5");
                markerDao.insertMarker(marker);
                marker = new Marker(53.64257, -2.5789,"Place 7", "P7");
                markerDao.insertMarker(marker);
                marker = new Marker(53.6889, -2.5245,"Place 8", "P8");
                markerDao.insertMarker(marker);
                marker = new Marker(53.616737, -2.3889,"Place 9", "P0");
                markerDao.insertMarker(marker);
                marker = new Marker(53.6877, -2.59,"Place 10", "P10");
                markerDao.insertMarker(marker);
            });
        }
    };
}
