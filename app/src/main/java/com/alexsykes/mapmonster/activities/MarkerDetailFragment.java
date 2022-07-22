package com.alexsykes.mapmonster.activities;
// https://guides.codepath.com/android/using-dialogfragment

import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.Layer;
import com.alexsykes.mapmonster.data.LayerDao;
import com.alexsykes.mapmonster.data.MMDatabase;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;


public class MarkerDetailFragment extends DialogFragment {
    private  static  LatLng latLng;
    private static String TAG = "Info";
//    private TextInputLayout markerNameEditText, markerCodeEditText;
    private EditText markerNameTextEdit, markerCodeTextEdit, markerNotesTextEdit;
    Button saveButton, cancelButton;
    RadioGroup layerRadioGroup;
    MMDatabase db;
    LayerDao layerDao;
    List<Layer> layerList;


        public MarkerDetailFragment(LatLng latLng) {
            this.latLng = latLng;
        Log.i(TAG, "MarkerDetailFragment: ");
        Bundle args = new Bundle();
        args.putString("title", "New marker");
        this.setArguments(args);


        db = MMDatabase.getDatabase(getContext());
        layerDao = db.layerDao();
        layerList =  layerDao.getLayerList();
        Log.i(TAG, "MarkerDetailFragment: ");

    };

    public static MarkerDetailFragment newInstance(String title){
        Log.i(TAG, "newInstance: ");
        MarkerDetailFragment frag = new MarkerDetailFragment(latLng);
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onViewCreated (View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        markerNameTextEdit = view.findViewById(R.id.markerNameTextEdit);
        markerCodeTextEdit = view.findViewById(R.id.markerCodeTextEdit);
        markerNotesTextEdit = view.findViewById(R.id.markerNotesTextEdit);

        saveButton = view.findViewById(R.id.saveMarkerDetailsButton);
        layerRadioGroup = view.findViewById(R.id.layerRadioGroup);

        for(int i = 0; i< layerList.size(); i++) {
            Layer layer = layerList.get(i);
            RadioButton button = new RadioButton(getContext());
            button.setText(layer.getLayername());
            layerRadioGroup.addView(button);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkerDetailFragmentListener listener = (MarkerDetailFragmentListener) getActivity();
                Editable markerName = markerNameTextEdit.getText();
                Editable markerCode = markerCodeTextEdit.getText();
                Editable markerNotes = markerNotesTextEdit.getText();
                //Editable markerDetails =
                String layer = "Placemark";

                if(markerName.length() == 0) { markerName.append("New Placemark") ; }
                if(markerCode.length() == 0) { markerCode.append("MT") ; }
                int selectedId = layerRadioGroup.getCheckedRadioButtonId();

                if(selectedId != -1) {
                    RadioButton radioButton = view.findViewById(selectedId);
                    layer = String.valueOf(radioButton.getText());
                }

                listener.onReturn(markerName, markerCode, markerNotes,
                        layer);
                dismiss();
            }
        });

        cancelButton = view.findViewById(R.id.dismissMarkerDetailsButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dismiss();
                                            }
                                        }
        );

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        getDialog().setCancelable(true);
        // Show soft keyboard automatically and request focus to field
        // markerNameInputLayout.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public void onResume() {

        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.20), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.TOP | Gravity.LEFT);
        // Call super onResume after sizing
        super.onResume();

    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_marker_detail, container);
    }

public interface MarkerDetailFragmentListener {
        void onReturn(Editable name, Editable code, Editable markerNotes, String layer);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.i(TAG, "onDismiss: ");
    }
}
