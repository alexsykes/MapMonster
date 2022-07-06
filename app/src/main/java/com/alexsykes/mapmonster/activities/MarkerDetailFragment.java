package com.alexsykes.mapmonster.activities;
// https://guides.codepath.com/android/using-dialogfragment

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.alexsykes.mapmonster.R;
import com.google.android.material.textfield.TextInputLayout;


public class MarkerDetailFragment extends DialogFragment {
    private TextInputLayout markerNameInputLayout;

    public MarkerDetailFragment() {
    }

        public static MarkerDetailFragment newInstance(String title){
            MarkerDetailFragment frag = new MarkerDetailFragment();
            Bundle args = new Bundle();
            args.putString("title", title);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState){
            return inflater.inflate(R.layout.fragment_marker_detail, container);
        }

        @Override

        public void onViewCreated (View view, @Nullable Bundle savedInstanceState){

            super.onViewCreated(view, savedInstanceState);

            // Get field from view

            markerNameInputLayout =  view.findViewById(R.id.markerNameEditText);

            // Fetch arguments from bundle and set title

            String title = getArguments().getString("title", "Enter Name");

            getDialog().setTitle(title);

            // Show soft keyboard automatically and request focus to field

            markerNameInputLayout.requestFocus();

            getDialog().getWindow().setSoftInputMode(

                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        }
    }
