package com.alexsykes.mapmonster.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.CheckBoxPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.Layer;
import com.alexsykes.mapmonster.data.LayerViewModel;
import com.alexsykes.mapmonster.data.MarkerViewModel;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    // Navigation
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.email_menu_item:
                emailData();
                return true;

            case R.id.export_menu_item:
                exportData();
                return true;
            default:
        }
        return false;
    }

    private void exportData() {
        String data = getExportData();
    }

    private String getExportData() {
        String data = "";

        return data;
    }

    private void emailData() {
        String data = getExportData();

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private List<Layer> layerList;
        private LayerViewModel layerViewModel;
        private MarkerViewModel markerViewModel;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

//            MultiSelectListPreference layer_visibility = findPreference("layer_visibility");
            SwitchPreference destroy_switch = findPreference("destroy_switch");
            CheckBoxPreference destroy_confirm = findPreference("destroy_confirm");
            SwitchPreference restore_switch = findPreference("restore_switch");
            CheckBoxPreference restore_confirm = findPreference("restore_confirm");

            destroy_switch.setChecked(false);
            destroy_confirm.setVisible(false);
            destroy_confirm.setChecked(false);

            restore_switch.setChecked(false);
            restore_confirm.setVisible(false);
            restore_confirm.setChecked(false);

            destroy_switch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    destroy_confirm.setVisible((Boolean) newValue);
                    destroy_switch.setChecked((Boolean) newValue);
                    return true;
                }
            });

            destroy_confirm.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    destroy_confirm.setChecked((Boolean) newValue);
                    if((Boolean) newValue) {
                        destroyData();
                    }
                    return true;
                }
            });

            restore_switch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    restore_confirm.setVisible((Boolean) newValue);
                    restore_switch.setChecked((Boolean) newValue);
                    return true;
                }
            });

            restore_confirm.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    restore_confirm.setChecked((Boolean) newValue);
                    if((Boolean) newValue) {
                        restoreData();
                    }
                    return false;
                }
            });

//            layerViewModel = new ViewModelProvider(this).get(LayerViewModel.class);
            markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);

            // Get lists of layers and visibleLayers
//            layerList = layerViewModel.getLayerList();
//            List<String> visibleLayerList = layerViewModel.getVisibleLayerList();

//            String[] options = new String[layerList.size()];
//
//            for (int i = 0; i < layerList.size(); i++){
//                options[i] = layerList.get(i).getLayername();
//            }

//            Set<String> values = new HashSet<String>(visibleLayerList);
//            layer_visibility.setEntries(options);
//            layer_visibility.setEntryValues(options);
//            layer_visibility.setValues(values);

            // Set listener for changes
//            layer_visibility.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    // newValue is A HashSet containing selected values
//                    Log.i(TAG, "onPreferenceChange: " + newValue);
//                    layerViewModel.updateLayerVisibility((Set<String>) newValue);
//                    layer_visibility.setValues((Set<String>) newValue);
//                    return false; // Saves in prefs if set to true
//                }
//            });
        }

        private void destroyData() {
            markerViewModel.archiveAll();
            layerViewModel.archiveAll();
        }

        private void restoreData() {
            markerViewModel.restoreAll();
        }
    }
}