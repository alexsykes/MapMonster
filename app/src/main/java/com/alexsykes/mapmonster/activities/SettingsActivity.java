package com.alexsykes.mapmonster.activities;



import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.alexsykes.mapmonster.data.MMarker;
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
import java.io.Writer;
import java.util.List;

import com.opencsv.*;


// https://developer.android.com/reference/android/os/Environment
public class SettingsActivity extends AppCompatActivity {
    public static final String TAG = "Info";
    public static final int PICKFILE_RESULT_CODE = 1;
    public static final int EMAIL_RESULT_CODE = 2;
    public static final int GETFILE_RESULT_CODE = 3;
    public static final int KML_RESULT_CODE = 4;
    private static final int GPX_RESULT_CODE = 5;
    private static final String[] END_OF_MARKERS = new String[] {"End of markers"};
    private static final String[] END_OF_FILE = new String[] {"End of file"};
    CSVWriter csvWriter;
    CSVReader csvReader;
    Cursor markerDataForExport;
    MarkerViewModel markerViewModel;
    LayerViewModel layerViewModel;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

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
        layerViewModel = new ViewModelProvider(this).get(LayerViewModel.class);
        markerDataForExport = markerViewModel.getMarkerDataForExport();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
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
                String file_format = preferences.getString("export_format", "CSV");
                switch (file_format) {
                    case "CSV" :
                        csvExport();
                        break;

                    case "KML" :
                        kmlExport();
                        break;

                    case "GPX" :
                        gpxExport();
                        break;

                    default:
                        break;
                }
                return true;

            case R.id.import_menu_item:
                importMarkers();
                return true;
            default:
        }
        return false;
    }

    private void gpxExport() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/gpx+xml");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_TITLE, "data.gpx");
        startActivityForResult(intent, GPX_RESULT_CODE);
    }

    private void csvExport() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("text/comma-separated-values");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_TITLE, "data.csv");
        startActivityForResult(intent, PICKFILE_RESULT_CODE);
//        finish();
    }

    private void kmlExport() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/vnd.google-earth.kml+xml");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_TITLE, "data.kml");
        startActivityForResult(intent, KML_RESULT_CODE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == -1) {
                    writeCSVFile(data);
                }
                break;

            case EMAIL_RESULT_CODE:
                Log.i(TAG, "onActivityResult: ");
                break;

            case GETFILE_RESULT_CODE:
                Log.i(TAG, "GETFILE_RESULT_CODE: " + resultCode);
                if (resultCode == -1) {
                    readCSVFile(data);
                }

            case KML_RESULT_CODE:
                Log.i(TAG, "KML_RESULT_CODE: " + resultCode);
                if (resultCode == -1) {
                    writeKMLFile(data);
                }

            case GPX_RESULT_CODE:
                Log.i(TAG, "GPX_RESULT_CODE: " + resultCode);
                if (resultCode == -1) {
                    writeGPXFile(data);
                }
        }
    }

    private void writeGPXFile(Intent data) {
        fileUri = data.getData();
        filePath = fileUri.getPath();
        Cursor markersForExport = getMarkersForExport();

        Uri uri = data.getData();
        try {
            String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n" +
                    "<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" " +
                    "\nxmlns:gpxx=\"http://www.garmin.com/xmlschemas/GpxExtensions/v3\" " +
                    "\nxmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" " +
                    "\ncreator=\"Oregon 400t\" version=\"1.1\" " +
                    "\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                    "\nxsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 " +
                    "\nhttp://www.topografix.com/GPX/1/1/gpx.xsd \nhttp://www.garmin.com/xmlschemas/GpxExtensions/v3 \nhttp://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd " +
                    "\nhttp://www.garmin.com/xmlschemas/TrackPointExtension/v1 " +
                    "\nhttp://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd\">";
            OutputStream outputStream = getContentResolver().openOutputStream(uri);



            outputStream.write(header.getBytes());

            while (markersForExport.moveToNext()) {
                String name = markersForExport.getString(1);
                String description = markersForExport.getString(3);
                String latitude = markersForExport.getString(4);
                String longitude = markersForExport.getString(5);

                String curline = "\n<wpt lat=\"" + latitude + "\" lon=\"" + longitude + "\">";
                String nameLine = "\n\t<name>" + name + "</name>";
                String descLine = "\n\t<desc>" + description + "</desc>";

                String wptLine = curline + nameLine + descLine;
                outputStream.write(wptLine.getBytes());

                outputStream.write("\n</wpt>".getBytes());
            }

            outputStream.write("</gpx>".getBytes());


            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeCSVFile(Intent data) {
        fileUri = data.getData();
        filePath = fileUri.getPath();
        Cursor markersForExport = getMarkersForExport();
        Cursor layersForExport = getLayersForExport();

        try {
            OutputStream os = getContentResolver().openOutputStream(data.getData());
            Writer writer = new OutputStreamWriter(os);
            csvWriter = new CSVWriter(writer);
            while (markersForExport.moveToNext()) {
                String arrStr[] = new String[markersForExport.getColumnCount()];
                for (int i = 0; i < markersForExport.getColumnCount(); i++)
                    arrStr[i] = markersForExport.getString(i);
                csvWriter.writeNext(arrStr);
            }

            csvWriter.writeNext(END_OF_MARKERS);

            while (layersForExport.moveToNext()) {
                String arrStr[] = new String[layersForExport.getColumnCount()];
                for (int i = 0; i < layersForExport.getColumnCount(); i++)
                    arrStr[i] = layersForExport.getString(i);
                csvWriter.writeNext(arrStr);
            }


            csvWriter.writeNext(END_OF_FILE);

            csvWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeKMLFile(Intent data) {
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<kml xmlns=\"http://www.opengis.net/kml/2.2\">";
        fileUri = data.getData();
        filePath = fileUri.getPath();
        Cursor markersForExport = getMarkersForExport();

        try {
            OutputStream os = getContentResolver().openOutputStream(data.getData());
            Writer writer = new OutputStreamWriter(os);

            writer.flush();
            writer.write(header);
            writer.write("\n<Document>");

            while (markersForExport.moveToNext()) {
                String name = markersForExport.getString(1);
                String description = markersForExport.getString(3);
                String latitude = markersForExport.getString(5);
                String longitude = markersForExport.getString(4);

                writer.write("\n<Placemark>");
                writer.write("\n\t<name>" + name + "</name>");
                writer.write("\n\t<description>" + description + "</description>");
                writer.write("\n\t<Point>");
                writer.write("\n\t\t<coordinates>" + latitude + "," + longitude + "</coordinates>");
                writer.write("\n\t</Point>");
                writer.write("\n</Placemark>");
            }
            writer.write("\n</Document>");
            writer.write("\n</kml>");
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readCSVFile(Intent data) {
        try {
            InputStream  inputStream = getContentResolver().openInputStream(data.getData());
            CSVReader reader = new CSVReader(new InputStreamReader(inputStream));

            // read line by line
            String[] record = null;

            while ((record = reader.readNext()) != null) {
                if (record.length == 1) {
                    break;
                }
                String placename = record[1];
                String code = record[2];
                String notes = record[3];
                double latitude = Double.parseDouble(record[4]);
                double longitude = Double.parseDouble(record[5]);
                int layer_id = Integer.parseInt(record[6]);
                MMarker marker = new MMarker(latitude, longitude, placename, code, layer_id, notes);
                markerViewModel.insert(marker);
            }


            while ((record = reader.readNext()) != null) {
                if (record.length == 1) {
                    break;
                }
                int layer_id = Integer.parseInt(record[0]);
                int icon_id = Integer.parseInt(record[1]);
                String layername = record[2];
                String code = record[3];
                boolean isVisible = Boolean.getBoolean(record[4]);
                boolean isArchived = Boolean.getBoolean(record[5]);
                Layer layer = new Layer(layer_id, icon_id, layername, code, isVisible, isArchived);
                layerViewModel.insert(layer);
            }
            reader.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Cursor getMarkersForExport() {
        MMDatabase db = MMDatabase.getDatabase(this);
        markerViewModel = new ViewModelProvider(this).get(MarkerViewModel.class);
        return markerViewModel.getMarkerDataForExport();
    }

    private Cursor getLayersForExport() {
        MMDatabase db = MMDatabase.getDatabase(this);
        layerViewModel = new ViewModelProvider(this).get(LayerViewModel.class);
        return layerViewModel.getLayerDataForExport();
    }

    private void importMarkers() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/comma-separated-values");
        startActivityForResult(Intent.createChooser(intent, "Open CSV"), GETFILE_RESULT_CODE);
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
//            layerViewModel.archiveAll();
        }

        private void restoreData() {
            markerViewModel.restoreAll();
        }
    }
}