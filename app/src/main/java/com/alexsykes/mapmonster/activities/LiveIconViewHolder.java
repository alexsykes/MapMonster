package com.alexsykes.mapmonster.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.mapmonster.R;
import com.alexsykes.mapmonster.data.Icon;
import com.alexsykes.mapmonster.data.LiveLayerItem;

public class LiveIconViewHolder extends RecyclerView.ViewHolder {
    public static final String TAG = "Info";
    final ImageButton iconImageButton;

    public LiveIconViewHolder(@NonNull View itemView) {
        super(itemView);
        iconImageButton = itemView.findViewById(R.id.iconImageButton);
    }

    public static LiveIconViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.icon_image_item, parent, false);
        return new LiveIconViewHolder(view);
    }

    public void bind(Icon current, Context context) {
        int resID = context.getResources().getIdentifier(current.getIconFilename(), "drawable", context.getPackageName());
        iconImageButton.setImageResource(resID);

        iconImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ((LayerEditActivity) context).visibilityToggle(current.getLayerID());
            }
        });
    }

}
