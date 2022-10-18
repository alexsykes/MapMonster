package com.alexsykes.mapmonster.data;

public class MapMarkerDataItem {
    public int markerID, layer_id;
    public double latitude, longitude;
    public String placename, code;
    public boolean isUpdated, isNew,  isArchived;
    public boolean isVisible;


    public boolean isSelected;
    public String notes;
    public String filename, layername;

    public String getLayername() {  return layername;   }
    public void setLayername(String layername) { this.layername = layername;  }
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

    public boolean isVisible() {  return isVisible; }

    public void setVisible(boolean visible) { isVisible = visible; }

    public int getMarkerID() {
        return markerID;
    }

    public void setMarkerID(int markerID) {
        this.markerID = markerID;
    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
