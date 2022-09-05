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

    public int getIcon_id() {
        return icon_id;
    }

    public void setIcon_id(int icon_id) {
        this.icon_id = icon_id;
    }

    private int icon_id;
    private String layername, code;

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    private boolean isVisible;
    private boolean isArchived;

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public Layer(String layername, String code, int icon_id) {
        this.layername = layername;
        this.code = code;
        this.icon_id = icon_id;
        isVisible = true;
        isArchived = false;
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
