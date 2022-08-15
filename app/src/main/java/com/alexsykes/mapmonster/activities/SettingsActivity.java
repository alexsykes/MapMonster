package com.alexsykes.mapmonster.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.Layer;
import com.alexsykes.mapmonster.data.LayerViewModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {
    public static final String TAG = "Info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private List<Layer> layerList;
        private LayerViewModel layerViewModel;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);


            MultiSelectListPreference layer_visibility = findPreference("layer_visibility");
            layerViewModel = new ViewModelProvider(this).get(LayerViewModel.class);

            // Get lists of layers and visibleLayers
            layerList = layerViewModel.getLayerList();
            List<String> visibleLayerList = layerViewModel.getVisibleLayerList();

            String[] options = new String[layerList.size()];

            for (int i = 0; i < layerList.size(); i++){
                options[i] = layerList.get(i).getLayername();
            }

            Set<String> values = new HashSet<String>(visibleLayerList);
            layer_visibility.setEntries(options);
            layer_visibility.setEntryValues(options);
            layer_visibility.setValues(values);

            // Set listener for changes
            layer_visibility.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // newValue is A HashSet containing selected values
                    Log.i(TAG, "onPreferenceChange: " + newValue);
                    layerViewModel.updateLayerVisibility((Set<String>) newValue);
                    layer_visibility.setValues((Set<String>) newValue);
                    return false; // Saves in prefs if set to true
                }
            });
        }
    }
}