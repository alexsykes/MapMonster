package com.alexsykes.mapmonster;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

public class SpinnerItemAdapter extends ArrayAdapter {

    public SpinnerItemAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
    }
}
