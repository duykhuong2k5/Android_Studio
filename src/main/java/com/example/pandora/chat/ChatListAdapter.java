package com.example.pandora.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pandora.R;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    private final List<ChatListActivity.Triple> chatList;
    private final OnChatClickListener onClick;

    public interface OnChatClickListener {
        void onClick(long productId, long senderId, String senderName);
    }

    public ChatListAdapter(List<ChatListActivity.Triple> chatList, OnChatClickListener onClick) {
        this.chatList = chatList;
        this.onClick = onClick;
    }

    @Override
    public ChatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_list, parent, false);
        return new ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatListViewHolder holder, int position) {
        ChatListActivity.Triple triple = chatList.get(position);

        // Tên khách hàng
        holder.tvTitle.setText("💬 " + triple.senderName);

        // Tên sản phẩm + thời gian gửi gần nhất
        holder.tvSubtitle.setText(triple.productName + " • " + triple.lastTime);

        holder.itemView.setOnClickListener(v ->
                onClick.onClick(triple.productId, triple.senderId, triple.senderName)
        );
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ChatListViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle;

        ChatListViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvSubtitle = view.findViewById(R.id.tvSubtitle);
        }
    }
}
