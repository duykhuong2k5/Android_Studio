package com.example.pandora.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pandora.R;

import java.util.List;

public class ImageSlideUrlAdapter extends RecyclerView.Adapter<ImageSlideUrlAdapter.ImageViewHolder> {

    private final Context context;
    private final List<String> imageUrls;

    public ImageSlideUrlAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imgSlide);
        }
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_slide, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Glide.with(context)
                .load(imageUrls.get(position))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_image)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }
}
