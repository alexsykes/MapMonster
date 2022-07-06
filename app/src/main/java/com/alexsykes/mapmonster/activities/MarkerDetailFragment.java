package com.alexsykes.mapmonster.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.alexsykes.mapmonster.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MarkerDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MarkerDetailFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MarkerDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MarkerDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MarkerDetailFragment newInstance() {
        MarkerDetailFragment fragment = new MarkerDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_marker_detail, container, false);
    }

    @Override

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // Get field from view

//        mEditText = (EditText) view.findViewById(R.id.txt_your_name);

        // Fetch arguments from bundle and set title

        String title = getArguments().getString("title", "Enter Name");

        getDialog().setTitle(title);

        // Show soft keyboard automatically and request focus to field

//        mEditText.requestFocus();

        getDialog().getWindow().setSoftInputMode(

                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }
}