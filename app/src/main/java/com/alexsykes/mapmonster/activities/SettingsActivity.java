package com.alexsykes.mapmonster.activities;


import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

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
import com.alexsykes.mapmonster.data.IconViewModel;
import com.alexsykes.mapmonster.data.Layer;
import com.alexsykes.mapmonster.data.LayerViewModel;
import com.alexsykes.mapmonster.data.MMDatabase;
import com.alexsykes.mapmonster.data.MarkerViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.opencsv.*;


// https://developer.android.com/reference/android/os/Environment
public class SettingsActivity extends AppCompatActivity {
    public static final String TAG = "Info";
    public static final int PICKFILE_RESULT_CODE = 1;
    CSVWriter csvWriter;
    Cursor markerDataForExport;
    MarkerViewModel markerViewModel;

    private Uri fileUri;
    private String filePath;
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
        MMDatabase db = MMDatabase.getDatabase(this);
        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);
//        layerViewModel = new ViewModelProvider(this).get(LayerViewModel.class);
        markerDataForExport = markerViewModel.getMarkerDataForExport();

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
                fileDemo();
//                exportData();
                return true;
            default:
        }
        return false;
    }

    private void fileDemo() {

        Intent chooseFile = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        chooseFile.setType("text/csv");
//        chooseFile = Intent.createChooser(chooseFile,"Choose a file");
        startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (requestCode == -1) {
                    fileUri = data.getData();
                    filePath = fileUri.getPath();
//                    tvItemPath.setText(filePath);
                }

                break;
        }
    }

    private void exportData() {
        Cursor exportData = getExportData();

        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, "Waypoints.csv");
        try {
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

            csvWrite.writeNext(exportData.getColumnNames());
            while (exportData.moveToNext()) {
                String arrStr[] = new String[exportData.getColumnCount()];
                for (int i = 0; i < exportData.getColumnCount(); i++)
                    arrStr[i] = exportData.getString(i);
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            exportData.close();

            Log.i(TAG, "Success");
        } catch (Exception sqlEx) {
            Log.e(TAG, sqlEx.getMessage(), sqlEx);
        }
    }

    private void emailData() {
        Cursor exportData = getExportData();


            String DatabaseName = "mm_database";
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            FileChannel source=null;
            FileChannel destination=null;
            String currentDBPath = "/data/"+ "com.alexsykes.mapmonster" +"/databases/"+DatabaseName ;
            String backupDBPath =  "Documents/back.sql";
            File currentDB = new File(data, currentDBPath);
            File backupDB = new File(sd, backupDBPath);
            try {
                source = new FileInputStream(currentDB).getChannel();
                destination = new FileOutputStream(backupDB).getChannel();
                destination.transferFrom(source, 0, source.size());
                source.close();
                destination.close();
                Toast.makeText(this, "Your Database is Exported !!", Toast.LENGTH_LONG).show();
            } catch(IOException e) {
                e.printStackTrace();
            }
    }

    private Cursor getExportData() {
        MMDatabase db = MMDatabase.getDatabase(this);
        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);
        return markerViewModel.getMarkerDataForExport();
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