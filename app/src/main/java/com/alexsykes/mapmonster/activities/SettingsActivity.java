package com.alexsykes.mapmonster.activities;



import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.Layer;
import com.alexsykes.mapmonster.data.LayerViewModel;
import com.alexsykes.mapmonster.data.MMDatabase;
import com.alexsykes.mapmonster.data.MarkerViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.file.Path;
import java.util.List;

import com.opencsv.*;


// https://developer.android.com/reference/android/os/Environment
public class SettingsActivity extends AppCompatActivity {
    public static final String TAG = "Info";
    public static final int PICKFILE_RESULT_CODE = 1;
    public static final int EMAIL_RESULT_CODE = 2;
    public static final int GETFILE_RESULT_CODE = 3;
    CSVWriter csvWriter;
    CSVReader csvReader;
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
//                csvEmail();
                return true;

            case R.id.export_menu_item:
                csvExport();
                return true;
                
            case R.id.import_menu_item:
                importMarkers();
                return true;
            default:
        }
        return false;
    }

    private void csvEmail() {
//        File exportDir = new File(Environment.getExternalStoragePublicDirectory("Documents/Scoremonster"), "");
        File exportDir = getFilesDir();
        String filename = "Markers.csv";
        File exportFile = writeToInternal(exportDir, filename);

        String path = getFilesDir().getAbsolutePath() + "/" + filename;
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
// The intent does not have a URI, so declare the "text/plain" MIME type
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"jan@example.com"}); // recipients
        emailIntent.putExtra(Intent.EXTRA_BCC, new String[]{"alex@alexsykes.net"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Markers from MapMonster");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Please find current marker list attached");
        emailIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse(path));
// You can also attach multiple items by passing an ArrayList of Uris
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(SettingsActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private File writeToInternal(File exportDir, String filename) {
        Cursor exportData = markerViewModel.getMarkerDataForExport();
        try {
            exportDir = new File(getFilesDir(), filename);
            exportDir.createNewFile();
            CSVWriter csvWriter = new CSVWriter(new FileWriter(exportDir));

            // Get current data
            while (exportData.moveToNext()) {
                String arrStr[] = new String[exportData.getColumnCount()];
                for (int i = 0; i < exportData.getColumnCount(); i++)
                    arrStr[i] = exportData.getString(i);
                csvWriter.writeNext(arrStr);
            }

            csvWriter.close();
        } catch (IOException e) {
            Log.e("Child", e.getMessage(), e);
        }
        return exportDir;
    }

    private void csvExport() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("text/comma-separated-values");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_TITLE, "data.csv");
        startActivityForResult(intent, PICKFILE_RESULT_CODE);
//        finish();
    }


    private void importMarkers() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/comma-separated-values");
        startActivityForResult(Intent.createChooser(intent, "Open CSV"), GETFILE_RESULT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == -1) {
                    fileUri = data.getData();
                    filePath = fileUri.getPath();
                }

                try {
                    OutputStream os = getContentResolver().openOutputStream(data.getData());
                    Writer writer = new OutputStreamWriter(os);
                    csvWriter = new CSVWriter(writer);
                    Cursor exportData = getMarkersForExport();
                    while (exportData.moveToNext()) {
                        String arrStr[] = new String[exportData.getColumnCount()];
                        for (int i = 0; i < exportData.getColumnCount(); i++)
                            arrStr[i] = exportData.getString(i);
                        csvWriter.writeNext(arrStr);
                    }

                    csvWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;

            case EMAIL_RESULT_CODE:
                Log.i(TAG, "onActivityResult: ");
                break;

            case GETFILE_RESULT_CODE:
                Log.i(TAG, "GETFILE_RESULT_CODE: " + resultCode);
                if (resultCode == -1) {
//                    importCSV(new File(data.getData().getPath()));


                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(data.getData());
                        Reader reader = new InputStreamReader(inputStream);
//                        CSVReader csvReader1 = new CSVReader(reader);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                }
        }
    }

    private void importCSV(File file) {
        try {
            ContentValues cv = new ContentValues();
            // reading CSV and writing table
            CSVReader dataRead = new CSVReader(new FileReader(file));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Cursor getMarkersForExport() {
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