package com.alexsykes.mapmonster.data;

public class LayerDataItem {
    public int layerID;
    public int icon_id;
    public String layername;
    public String code;
    public String filename;
    public String iconName;

    public String getName() {  return iconName;  }

    public void setName(String name) {  this.iconName = name;   }

    public boolean isVisible, isArchived;

    public int getLayer_id() {
        return layerID;
    }

    public void setLayer_id(int layer_id) {
        this.layerID = layer_id;
    }

    public int getIcon_id() {
        return icon_id;
    }

    public void setIcon_id(int icon_id) {
        this.icon_id = icon_id;
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }
}

