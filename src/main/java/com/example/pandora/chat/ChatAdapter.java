package com.example.pandora.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<ChatMessage> messages;
    private final long currentUserId;

    private static final int VIEW_TYPE_LEFT = 0;
    private static final int VIEW_TYPE_RIGHT = 1;

    public ChatAdapter(Context context, List<ChatMessage> messages, long currentUserId) {
        this.context = context;
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getSenderId() == currentUserId ? VIEW_TYPE_RIGHT : VIEW_TYPE_LEFT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == VIEW_TYPE_RIGHT) {
            View view = inflater.inflate(R.layout.item_message_right, parent, false);
            return new MessageRightViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_message_left, parent, false);
            return new MessageLeftViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage msg = messages.get(position);
        if (holder instanceof MessageRightViewHolder) {
            ((MessageRightViewHolder) holder).textView.setText(msg.getMessage());
        } else if (holder instanceof MessageLeftViewHolder) {
            ((MessageLeftViewHolder) holder).textView.setText(msg.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageLeftViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        MessageLeftViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.tvMessageLeft);
        }
    }

    static class MessageRightViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        MessageRightViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.tvMessageRight);
        }
    }
}
