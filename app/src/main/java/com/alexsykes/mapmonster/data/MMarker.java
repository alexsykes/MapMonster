package com.alexsykes.mapmonster.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "markers")
public class MMarker {
    public void setMarker_id(int marker_id) {
        this.marker_id = marker_id;
    }

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name="markerID")
    private int marker_id;

    public boolean isVisible() {
        return isVisible;
    }

    private double latitude, longitude;
    private String placename;
    private String code;
    private String type;
    private boolean isUpdated;
    private boolean isNew;
    private String snippet;

    public MMarker(double latitude, double longitude, String placename, String code, String type, String snippet) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.placename = placename;
        this.code = code;
        this.snippet = snippet;
        this.type = type;
        isVisible = true;
        isUpdated = false;
        isNew = true;

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    private enum type {
        WAYPOINT,
        JUNCTION,
        FUEL,
        ACCOMMODATION
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    private boolean isVisible;

    public int getMarker_id() {
        return marker_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPlacename() {
        return placename;
    }

    public void setPlacename(String placename) {
        this.placename = placename;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}