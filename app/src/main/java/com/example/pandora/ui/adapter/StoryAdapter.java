package com.example.pandora.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.example.pandora.R;
import com.example.pandora.data.entity.Story; // Đảm bảo bạn đã có class Model này

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private Context context;
    private List<Story> storyList;
    private OnStoryClickListener listener;

    // Interface để xử lý sự kiện khi bé bấm vào truyện hoặc bấm nút xóa
    public interface OnStoryClickListener {
        void onStoryClick(Story story);
        void onDeleteClick(Story story, int position);
    }

    public StoryAdapter(Context context, List<Story> storyList, OnStoryClickListener listener) {
        this.context = context;
        this.storyList = storyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Story story = storyList.get(position);

        // 1. Đổ dữ liệu Tiêu đề
        holder.txtTitle.setText(story.getContentEn());

        // 2. Đổ dữ liệu Ảnh (Sử dụng Glide để load ảnh từ URL hoặc Path)
        Glide.with(context)
                .load(story.getThumbnailUrl())
                .placeholder(R.drawable.ic_launcher_background) // Ảnh hiện khi đang load
                .error(R.drawable.ic_launcher_foreground)      // Ảnh hiện nếu lỗi
                .into(holder.imgThumbnail);

        // 3. Đổ dữ liệu Tags/Genres vào ChipGroup
        holder.chipGroupTags.removeAllViews(); // Xóa các chip cũ để không bị trùng khi scroll
        if (story.getGenres() != null) {
            for (String genre : story.getGenres()) {
                Chip chip = new Chip(context);
                chip.setText(genre);
                chip.setChipBackgroundColorResource(R.color.tertiaryContainer);
                chip.setTextColor(context.getResources().getColor(R.color.onTertiaryContainer));
                chip.setChipStrokeWidth(0);
                holder.chipGroupTags.addView(chip);
            }
        }

        // 4. Xử lý sự kiện click vào cả Card để xem chi tiết
        holder.itemView.setOnClickListener(v -> listener.onStoryClick(story));

        // 5. Xử lý sự kiện nút xóa
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(story, position));
    }

    @Override
    public int getItemCount() {
        return storyList != null ? storyList.size() : 0;
    }

    // ViewHolder để ánh xạ các thành phần từ XML
    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        ImageButton btnDelete;
        TextView txtTitle;
        ChipGroup chipGroupTags;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgStoryThumbnail);
            btnDelete = itemView.findViewById(R.id.btnDeleteStory);
            txtTitle = itemView.findViewById(R.id.txtStoryTitle);
            chipGroupTags = itemView.findViewById(R.id.chipGroupTags);
        }
    }
}