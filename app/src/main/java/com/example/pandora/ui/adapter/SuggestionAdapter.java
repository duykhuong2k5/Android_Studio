package com.example.pandora.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionVH> {

    // Listener để handle khi user chọn 1 gợi ý
    public interface OnSuggestionClickListener {
        void onSuggestionClick(String keyword);
    }

    private List<String> data;
    private final OnSuggestionClickListener listener;

    public SuggestionAdapter(List<String> data, OnSuggestionClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    // Set danh sách mới & refresh
    public void setData(List<String> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SuggestionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new SuggestionVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionVH holder, int position) {
        String keyword = data.get(position);
        holder.tv.setText(keyword);

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) listener.onSuggestionClick(keyword);
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    // ViewHolder
    static class SuggestionVH extends RecyclerView.ViewHolder {
        TextView tv;

        public SuggestionVH(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(android.R.id.text1);
        }
    }
}
