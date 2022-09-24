package com.alexsykes.mapmonster.data;

import android.app.Application;

import java.util.List;

public class IconRepository {
    private IconDao iconDao;
    private List<Icon> iconList;

    public IconRepository(Application application) {
        MMDatabase db = MMDatabase.getDatabase(application);

        iconDao = db.iconDao();
        iconList = iconDao.getIconList();
    }

    public List<Icon> getIconList() {
        return iconDao.getIconList();
    }

    public Icon getIconByFilename(String filename) { return iconDao.getIconByFilename(filename); }

    public void insert(Icon icon) {
        MMDatabase.databaseWriteExecutor.execute(() -> {
            iconDao.insertIcon(icon);
        });
    }
}
