package com.alexsykes.mapmonster.data;

public class SpinnerData {
    private int layerID;
    private String layername;

    public SpinnerData(int layerID, String layername) {
        this.layerID = layerID;
        this.layername = layername;
    }

    public int getLayerID() {
        return layerID;
    }

    public void setLayerID(int layerID) {
        this.layerID = layerID;
    }

    public String getLayername() {
        return layername;
    }

    public void setLayername(String layername) {
        this.layername = layername;
    }
}
