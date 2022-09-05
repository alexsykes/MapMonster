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

    @Query("SELECT COUNT(*) FROM markers GROUP BY layer_id ORDER BY layer_id, placename")
    List<Integer> markerCountByLayer();

    @Query("SELECT * FROM markers WHERE layer_id IN (:layerNames) AND isArchived = 0 ORDER BY layer_id, placename")
    List<MMarker> getVisibleMarkerList(ArrayList<String> layerNames);

    @Query("UPDATE markers SET latitude = :lat, longitude = :lng, isUpdated = :isUpdated WHERE markerID = :marker_id  ")
    void updateMarker(int marker_id, double lat, double lng, boolean isUpdated);

    @MapInfo(keyColumn = "layername", valueColumn = "placename")
    @Query("SELECT layers.layername AS layername, layers.isVisible AS isVisible, markers.* FROM layers JOIN markers ON layers.layername = markers.layer_id WHERE markers.isArchived = 0 ORDER BY layername, placename")
    public Map<String, List<MMarker>> getMarkersByLayer();

    @Query("SELECT * FROM markers WHERE markerID = :tag")
    MMarker getMarker(int tag);

    @Query("UPDATE markers SET code = :markerCode, placename = :markerName, notes = :markerNotes, latitude = :lat, longitude = :lng WHERE markerID = :markerId")
    void update(int markerId, String markerCode, String markerNotes, String markerName, double lat, double lng);

    @Query("UPDATE markers SET isArchived = 0")
    void restoreAllMarkers();




    @Query("SELECT markers.*, icons.filename AS filename FROM markers JOIN layers ON markers.layer_id = layers.layerID JOIN icons ON layers.icon_id = icons.iconID WHERE markers.isArchived = 0 ORDER BY placename")
    List<MapMarkerItem> getMarkerList();

    static class MapMarkerItem {
        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public String getPlacename() {
            return placename;
        }

        public String getCode() {
            return code;
        }

        public int getLayer_id() {
            return layer_id;
        }

        public boolean isUpdated() {
            return isUpdated;
        }

        public boolean isNew() {
            return isNew;
        }

        public boolean isArchived() {
            return isArchived;
        }

        public String getNotes() {
            return notes;
        }

        public String getFilename() {
            return filename;
        }


        public int getMarkerID() {
            return markerID;
        }

        public void setMarkerID(int markerID) {
            this.markerID = markerID;
        }

        public int markerID;
        public double latitude, longitude;
        public String placename;
        public String code;

        public int layer_id;
        public boolean isUpdated;
        public boolean isNew;
        public boolean isArchived;
        public String notes;
        public String filename;
    }
}



