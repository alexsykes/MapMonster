package com.alexsykes.mapmonster.activities;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.Layer;
import com.alexsykes.mapmonster.data.LayerViewModel;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {

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
            layerViewModel = new ViewModelProvider(this).get(LayerViewModel.class);
            layerList = layerViewModel.getLayerList();

            String[] options = new String[layerList.size()];

            for (int i = 0; i < layerList.size(); i++){
                options[i] = layerList.get(i).getLayername();
            }
//            MultiSelectListPreference customListPref = new MultiSelectListPreference(getActivity());
            MultiSelectListPreference layer_visibility = findPreference("layer_visibility");

            // Get the Preference Category which we want to add the ListPreference to
//            PreferenceCategory targetCategory = (PreferenceCategory) findPreference("TARGET_CATEGORY");

            CharSequence[] entries = new CharSequence[]{"One", "Two", "Three"};
            CharSequence[] entryValues = new CharSequence[]{ "1", "2", "3" };

            // IMPORTANT - This is where set entries...looks OK to me
            layer_visibility.setEntries(options);
            layer_visibility.setEntryValues(options);

            layer_visibility.setPersistent(true);

            // Add the ListPref to the Pref category
            //targetCategory.addPreference(customListPref);
        }
    }
}