package com.alexsykes.mapmonster.data;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "icons")
public class Icon {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "iconID")
    private int icon_id;

    private String name;
    private String iconFilename;

    // String is icon filename with lower case, underscore and ".xml"
    public Icon(String iconFilename) {
        this.iconFilename = iconFilename.replace(".xml", "");
        name = iconFilename.replace(".xml", "");
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        name = name.replace("_", " ");
        this.name = name;
    }

    @Ignore
    public Icon(int icon_id, String name, String iconFilename) {
        this.icon_id = icon_id;
        this.name = name;
        this.iconFilename = iconFilename;
    }

    @Ignore
    public Icon(String name, String iconFilename) {
        this.name = name;
        this.iconFilename = iconFilename;
    }

    public void setIcon_id(int icon_id) {
        this.icon_id = icon_id;
    }

    public int getIcon_id() {
        return icon_id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconFilename() {
        return iconFilename;
    }

    public void setIconFilename(String iconFilename) {
        this.iconFilename = iconFilename;
    }
}
