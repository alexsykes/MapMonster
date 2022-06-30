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

    public int getLayer_id() {
        return layer_id;
    }

    public void setLayer_id(int layer_id) {
        this.layer_id = layer_id;
    }
}
