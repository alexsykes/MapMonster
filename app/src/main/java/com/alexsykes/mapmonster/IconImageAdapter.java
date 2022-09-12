package com.alexsykes.mapmonster;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IconImageAdapter extends RecyclerView.Adapter<IconImageAdapter.IconImageViewHolder> {
    int[] iconIds;

    public IconImageAdapter(int[] iconIds) {
        this.iconIds = iconIds;
    }


    @NonNull
    @Override
    public IconImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.icon_image_item, parent,false);
        return new IconImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IconImageViewHolder holder, int position) {
        ImageButton button = holder.getImageButton();
        button.setImageResource(iconIds[position]);
        holder.resid = iconIds[position];

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Info", "onClick: " + holder.resid);
            }
        });

    }

    @Override
    public int getItemCount() {
        return iconIds.length;
    }

    public class IconImageViewHolder extends RecyclerView.ViewHolder {
        private final ImageButton imageButton;
        int resid;

        public IconImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.iconImageButton);
        }

        public ImageButton getImageButton() { return imageButton; }
    }
}
