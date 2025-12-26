package com.example.pandora.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;

import java.util.List;

public class ImageSlideAdapter extends RecyclerView.Adapter<ImageSlideAdapter.ImageViewHolder> {

    private final Context context;
    private final List<Integer> imageList;

    public ImageSlideAdapter(Context context, List<Integer> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSlide;

        public ImageViewHolder(View view) {
            super(view);
            imgSlide = view.findViewById(R.id.imgSlide);
        }
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_slide, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.imgSlide.setImageResource(imageList.get(position));
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}
