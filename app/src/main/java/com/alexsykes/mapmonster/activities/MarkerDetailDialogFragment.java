package com.alexsykes.mapmonster.activities;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.alexsykes.mapmonster.R;

public class MarkerDetailDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {
    EditText markerNameEditText, markerCodeEditText;
    RadioGroup layerRadioGroup;

    public interface MarkerDetailDialogListener {
        void onFinishEditDialog(String text);
    }

    public static MarkerDetailDialogFragment newInstance(String title) {
        MarkerDetailDialogFragment frag = new MarkerDetailDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_marker_detail, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // markerNameEditText = view.findViewById(R.id.markerNameEditText).getText();
        String name = getArguments().getString("title", "Enter name");
        getDialog().setTitle(name);
     //   markerNameEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        markerCodeEditText.setOnEditorActionListener(this);
//        markerNameEditText.setOnEditorActionListener(this);
    }


    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if(EditorInfo.IME_ACTION_DONE == actionId) {
            MarkerDetailDialogListener listener = (MarkerDetailDialogListener) getActivity();
            listener.onFinishEditDialog(markerNameEditText.getText().toString());
            dismiss();
            return true;
        }
        return false;
    }
}
