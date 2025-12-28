package com.example.pandora.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.StoryResponse;
import ui.story.StoryDetailActivity;

// Import các thành phần của Coil
import coil.ComponentRegistry;
import coil.ImageLoader;
import coil.decode.SvgDecoder;
import coil.request.ImageRequest;

import java.util.List;

public class StoryLibraryAdapter extends RecyclerView.Adapter<StoryLibraryAdapter.ViewHolder> {
    private List<StoryResponse> storyList;
    private Context context;

    public StoryLibraryAdapter(List<StoryResponse> storyList) {
        this.storyList = storyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_story_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StoryResponse story = storyList.get(position);
        holder.tvTitle.setText(story.titleEn);

        // SỬA LỖI TẠI ĐÂY: Sử dụng ComponentRegistry.Builder thay vì Lambda
        ComponentRegistry registry = new ComponentRegistry.Builder()
                .add(new SvgDecoder.Factory())
                .build();

        ImageLoader imageLoader = new ImageLoader.Builder(context)
                .components(registry)
                .build();

        ImageRequest request = new ImageRequest.Builder(context)
                .data(story.thumbnailUrl)
                .target(holder.imgCover)
                .crossfade(true)
                .placeholder(android.R.drawable.ic_menu_gallery) // Ảnh mặc định khi chờ
                .error(android.R.drawable.stat_notify_error)      // Ảnh khi lỗi
                .build();

        imageLoader.enqueue(request);

        // Sự kiện click để xem chi tiết truyện
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, StoryDetailActivity.class);
            intent.putExtra("STORY_ID", story.id);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return storyList != null ? storyList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView tvTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgCardCover);
            tvTitle = itemView.findViewById(R.id.tvCardTitle);
        }
    }
}