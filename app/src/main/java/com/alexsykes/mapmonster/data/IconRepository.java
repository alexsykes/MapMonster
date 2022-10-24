package com.alexsykes.mapmonster.data;

import android.app.Application;

import java.util.List;

public class IconRepository {
    private final IconDao iconDao;
    private Icon icon;
    private List<Icon> iconList;

    public IconRepository(Application application) {
        MMDatabase db = MMDatabase.getDatabase(application);
        iconDao = db.iconDao();
        iconList = iconDao.getIconList();
    }

    public List<Icon> getIconList() {
        return iconList;
    }

    public Icon getIconByFilename(String filename) {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            icon = iconDao.getIconByFilename(filename);
        });
        return icon;
    }

    public void insert(Icon icon) {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            iconDao.insertIcon(icon);
        });
    }
}
