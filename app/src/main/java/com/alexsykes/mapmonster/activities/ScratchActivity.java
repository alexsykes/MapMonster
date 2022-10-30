package com.alexsykes.mapmonster.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.IconViewModel;

public class ScratchActivity extends AppCompatActivity {
    public static final String TAG = "Info";
    private IconViewModel iconViewModel;
    RecyclerView iconImageRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch);

//        iconImageRV = findViewById(R.id.iconRV);
//        final LiveIconListAdapter adapter = new LiveIconListAdapter(new LiveIconListAdapter.IconDiff());
//        iconImageRV.setAdapter(adapter);
//        iconImageRV.setVisibility(View.VISIBLE);
//        iconImageRV.setLayoutManager(new GridLayoutManager(this, 6));
//
//        iconViewModel = new ViewModelProvider(this).get(IconViewModel.class);
//
//        iconViewModel.getIconList().observe(this, icons -> {
////            Log.i(TAG, "onCreate:  "  );
//            adapter.submitList(icons);
//        });
    }
}