package com.example.pandora.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.ui.user.PlayerActivity; // 🔥 DÒNG QUAN TRỌNG

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    List<String> songs;
    Context context;

    public SongAdapter(List<String> songs, Context context) {
        this.songs = songs;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_song, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtSongTitle.setText(songs.get(position));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlayerActivity.class);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtSongTitle;

        ViewHolder(View itemView) {
            super(itemView);
            txtSongTitle = itemView.findViewById(R.id.txtSongTitle);
        }
    }
}
