package com.example.pandora.ui.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;

public class CancelReasonAdapter extends RecyclerView.Adapter<CancelReasonAdapter.ViewHolder> {

    private final String[] reasons;
    private final OnReasonSelected listener;

    public interface OnReasonSelected {
        void onSelected(String reason);
    }

    public CancelReasonAdapter(String[] reasons, OnReasonSelected listener) {
        this.reasons = reasons;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvReason;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReason = itemView.findViewById(R.id.tvReason);
        }
    }

    @NonNull
    @Override
    public CancelReasonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cancel_reason, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CancelReasonAdapter.ViewHolder holder, int position) {
        String reason = reasons[position];
        holder.tvReason.setText(reason);

        holder.itemView.setOnClickListener(v -> listener.onSelected(reason));
    }

    @Override
    public int getItemCount() {
        return reasons.length;
    }
}
