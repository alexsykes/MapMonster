package com.alexsykes.mapmonster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        holder.getImageView().setImageResource(iconIds[position]);

    }

    @Override
    public int getItemCount() {
        return iconIds.length;
    }

    public class IconImageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public IconImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iconImageView);
        }

        public ImageView getImageView() { return imageView; }
    }
}
