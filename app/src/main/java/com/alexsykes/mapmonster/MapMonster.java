package com.alexsykes.mapmonster;

import android.app.Application;
import android.util.Log;

import com.alexsykes.mapmonster.data.MMDatabase;

public class MapMonster extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Info", "onCreateLaunch: ");
        MMDatabase db = MMDatabase.getDatabase(getApplicationContext());
        Log.i("Info", "onCreateLaunch: done ");
    }
}
