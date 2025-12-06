package com.example.pandora.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.ProductSize;

import java.util.List;

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.SizeViewHolder> {

    public interface OnSizeClickListener {
        // size = null khi user bỏ chọn
        void onSizeClick(ProductSize size);
    }

    private final List<ProductSize> sizes;
    private final OnSizeClickListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public SizeAdapter(List<ProductSize> sizes, OnSizeClickListener listener) {
        this.sizes = sizes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_size, parent, false);
        return new SizeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SizeViewHolder holder, int position) {
        ProductSize size = sizes.get(position);
        holder.tvLabel.setText(size.getSizeLabel());

        // set selected state cho background selector
        holder.tvLabel.setSelected(position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {

            // 🔁 Nếu bấm lại đúng ô đang được chọn → bỏ chọn
            if (selectedPosition == position) {
                int old = selectedPosition;
                selectedPosition = RecyclerView.NO_POSITION;

                if (old != RecyclerView.NO_POSITION) {
                    notifyItemChanged(old);
                }

                if (listener != null) listener.onSizeClick(null); // báo là không chọn size
                return;
            }

            // ✅ Chọn size mới
            int old = selectedPosition;
            selectedPosition = position;

            if (old != RecyclerView.NO_POSITION) {
                notifyItemChanged(old);
            }
            notifyItemChanged(selectedPosition);

            if (listener != null) listener.onSizeClick(size);
        });
    }

    @Override
    public int getItemCount() {
        return sizes != null ? sizes.size() : 0;
    }

    static class SizeViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel;

        public SizeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tvSizeLabel);
        }
    }
}
