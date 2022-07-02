package com.alexsykes.mapmonster.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "layers")
public class Layer {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name="layerID")
    private int layer_id;
    private String layername, code;
    private boolean isVisible;

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public Layer(String layername, String code) {
        this.layername = layername;
        this.code = code;
        isVisible = true;
    }


    public String getLayername() {
        return layername;
    }

    public void setLayername(String layername) {
        this.layername = layername;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getLayer_id() {
        return layer_id;
    }

    public void setLayer_id(int layer_id) {
        this.layer_id = layer_id;
    }
}
