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
import com.example.pandora.ui.user.SongListActivity;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {

    private List<String> topics;
    private Context context;

    public TopicAdapter(List<String> topics, Context context) {
        this.topics = topics;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_music_topic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String topic = topics.get(position);
        holder.txtTopicName.setText(topic);

        // SET ẢNH NỀN THEO CHỦ ĐỀ
        switch (topic) {
            case "Animals":
                holder.imgTopic.setImageResource(R.drawable.bg_animals);
                break;
            case "Colors":
                holder.imgTopic.setImageResource(R.drawable.bg_colors);
                break;
            case "Alphabet":
                holder.imgTopic.setImageResource(R.drawable.bg_alphabet);
                break;
            case "Numbers":
                holder.imgTopic.setImageResource(R.drawable.bg_numbers);
                break;
            default:
                holder.imgTopic.setImageResource(R.drawable.bg_default);
                break;
        }

        // CLICK → VÀO DANH SÁCH BÀI HÁT
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SongListActivity.class);
            intent.putExtra("TOPIC_NAME", topic);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    // ================= VIEW HOLDER =================
    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgTopic;
        TextView txtTopicName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgTopic = itemView.findViewById(R.id.imgTopic);
            txtTopicName = itemView.findViewById(R.id.txtTopicName);
        }
    }
}
