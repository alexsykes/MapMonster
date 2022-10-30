package com.alexsykes.mapmonster.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class IconViewModel extends AndroidViewModel {
    private final IconRepository iconRepository;

    private final LiveData<List<Icon>> iconList;

    public IconViewModel(@NonNull Application application) {
        super(application);
        iconRepository = new IconRepository(application);
        iconList = iconRepository.getIconList();
    }


    public Icon getIconByFilename(String filename) {
        return iconRepository.getIconByFilename(filename);
    }

    // Mutators
    public void insert(Icon icon) {
        iconRepository.insert(icon);
    }

    public LiveData<List<Icon>> getIconList() {
        return iconList;
    }
}
