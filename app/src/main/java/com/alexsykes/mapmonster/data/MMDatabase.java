package com.alexsykes.mapmonster.data;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {MMarker.class, Layer.class, Icon.class}, version = 1, exportSchema = false)

public abstract class MMDatabase extends RoomDatabase{
    public abstract MarkerDao markerDao();
    public abstract LayerDao layerDao();
    public abstract IconDao iconDao();

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

    private static final RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback()
    {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                MarkerDao markerDao = INSTANCE.markerDao();
                markerDao.deleteAllMarkers();
//
//                MMarker marker = new MMarker(53.5947, -2.5611, "Home", "P1", 1, "16, Avonhead Close, BL6 5QD");
//                markerDao.insertMarker(marker);
//                marker = new MMarker(53.6687, -2.509, "Place 2", "P2", 2, "");
//                markerDao.insertMarker(marker);
//                marker = new MMarker(53.6897, -2.535568, "Place 3", "P3", 3, "");
//                markerDao.insertMarker(marker);
//                marker = new MMarker(53.6701, -2.5789, "Place 4", "P4", 4, "");
//                markerDao.insertMarker(marker);
//                marker = new MMarker(53.6087, -2.556699, "Place 5", "P5", 3, "");
//                markerDao.insertMarker(marker);
//                marker = new MMarker(53.66, -2.579, "Place 6", "P6", 2, "");
//                markerDao.insertMarker(marker);
//                marker = new MMarker(53.64257, -2.5789, "Place 7", "P7", 3, "");
//                markerDao.insertMarker(marker);
//                marker = new MMarker(53.6889, -2.5245, "Place 8", "P8", 5, "");
//                markerDao.insertMarker(marker);
//                marker = new MMarker(53.616737, -2.3889, "Place 9", "P0", 2, "");
//                markerDao.insertMarker(marker);
//                marker = new MMarker(53.6877, -2.59, "Place 10", "P10", 2, "");
//                markerDao.insertMarker(marker);
//                marker = new MMarker(53.55535, -2.48963, "M61 J6", "J6", 2, "");
//                markerDao.insertMarker(marker);


                LayerDao layerDao = INSTANCE.layerDao();
                layerDao.deleteAllLayers();

                Layer layer = new Layer("Waypoints", "WP", 1);
                layerDao.insertLayer(layer);
//                layer = new Layer("Waypoint", "WP", 12);
//                layerDao.insertLayer(layer);
//                layer = new Layer("Fuel", "FL", 11);
//                layerDao.insertLayer(layer);
//                layer = new Layer("Junction", "JCT", 8);
//                layerDao.insertLayer(layer);
//                layer = new Layer("Food", "FD", 7);
//                layerDao.insertLayer(layer);
//                layer = new Layer("Accommodation", "ACC", 5);
//                layerDao.insertLayer(layer);

                IconDao iconDao = INSTANCE.iconDao();
                iconDao.deleteAllIcons();

                // Default icon should be first on list - iconID = 1
                Icon icon = new Icon("map_marker.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("map_marker_red.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("map_marker_blue.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("map_marker_green.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("map_marker_yellow.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("map_marker_white.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("home.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("airplane.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("alert.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("atm.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("bed.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("camera_outline.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("compass_rose.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("car.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("car_outline.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("car_park.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("car_wrench.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("cell_tower.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("tower.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("coffee.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("knife_fork.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("fastfood.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("crosshairs.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("crosshairs_gps.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("crosshairs_off.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("flag_checkered.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("photography.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("gas_station.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("hospital_box_outline.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("cart_outline.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("tent.xml");
                iconDao.insertIcon(icon);
                icon = new Icon("warning_24.xml");
                iconDao.insertIcon(icon);
            });
        }
    };
}
