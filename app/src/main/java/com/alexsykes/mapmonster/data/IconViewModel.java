package com.alexsykes.mapmonster.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

public class IconViewModel extends AndroidViewModel {
    private IconRepository iconRepository;

    private List<Icon> iconList;

    public IconViewModel(@NonNull Application application) {
        super(application);
        iconRepository = new IconRepository(application);
        iconList = iconRepository.getIconList();
    }

    // Mutators
    public void insert(Icon icon) {
        iconRepository.insert(icon);
    }
}
