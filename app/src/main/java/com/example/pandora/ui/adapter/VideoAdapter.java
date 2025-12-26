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
import com.example.pandora.ui.model.VideoItem;
import com.example.pandora.ui.user.PlayerActivity;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    Context context;
    List<VideoItem> list;

    public VideoAdapter(Context context, List<VideoItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_video, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        VideoItem v = list.get(position);

        h.txtTitle.setText(v.title);
        h.txtAuthor.setText(v.author);
        h.imgThumb.setImageResource(v.thumbRes);

        h.itemView.setOnClickListener(view -> {
            Intent i = new Intent(context, PlayerActivity.class);
            i.putExtra("VIDEO_ID", v.id);       // ID DB
            i.putExtra("VIDEO_RES", v.videoRes);
            i.putExtra("TITLE", v.title);

            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView txtTitle, txtAuthor;

        ViewHolder(View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgThumb);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtAuthor = itemView.findViewById(R.id.txtAuthor);
        }
    }
}
