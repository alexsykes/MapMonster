package com.alexsykes.mapmonster.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "layers")
public class Layer {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name="layerID")
    private int layer_id;
    private int icon_id;
    private String layername, code;
    private boolean isVisible, isArchived;

    public Layer(int layer_id, int icon_id, String layername, String code, boolean isVisible, boolean isArchived) {
        this.layer_id = layer_id;
        this.icon_id = icon_id;
        this.layername = layername;
        this.code = code;
        this.isVisible = isVisible;
        this.isArchived = isArchived;
    }

    public int getIcon_id() {
        return icon_id;
    }

    public void setIcon_id(int icon_id) {
        this.icon_id = icon_id;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    @Ignore
    public Layer(String layername, String code, int icon_id) {
        this.layername = layername;
        this.code = code;
        this.icon_id = icon_id;
        isVisible = true;
        isArchived = false;
    }
    @Ignore
    public Layer(String layername, String code, int icon_id, boolean isVisible) {
        this.layername = layername;
        this.code = code;
        this.icon_id = icon_id;
        this.isVisible = isVisible;
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
