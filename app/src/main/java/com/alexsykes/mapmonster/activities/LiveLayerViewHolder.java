package com.alexsykes.mapmonster.activities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.LiveLayerItem;

public class LiveLayerViewHolder extends RecyclerView.ViewHolder {
    public static final String TAG = "Info";
    final TextView layerNameTextView;
    final ImageView imageView, layerToggleImage;
    private final int eye_open_id;
    private final int eye_closed_id;

    public LiveLayerViewHolder(@NonNull View itemView) {
        super(itemView);

        layerNameTextView = itemView.findViewById(R.id.layerNameTextView);
        imageView = itemView.findViewById(R.id.imageView);
        layerToggleImage = itemView.findViewById(R.id.layerToggleImage);

        eye_open_id = itemView.getContext().getResources().getIdentifier("eye_outline", "drawable", itemView.getContext().getPackageName());
        eye_closed_id = itemView.getContext().getResources().getIdentifier("eye_off_outline", "drawable", itemView.getContext().getPackageName());

    }

    public static LiveLayerViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layer_data_item, parent, false);
        return new LiveLayerViewHolder(view);
    }

    public void bind(LiveLayerItem current, Context context) {
        int resID = context.getResources().getIdentifier(current.getFilename(), "drawable", context.getPackageName());
        imageView.setImageResource(resID);
        layerNameTextView.setText(current.getLayername());

        int visResID = current.isVisible() ? eye_open_id : eye_closed_id;
        layerToggleImage.setImageResource(visResID);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LayerEditActivity) context).onLayerClickCalled(current.getLayerID());
            }
        });

        layerToggleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LayerEditActivity) context).visibilityToggle(current.getLayerID());
            }
        });
    }
}
