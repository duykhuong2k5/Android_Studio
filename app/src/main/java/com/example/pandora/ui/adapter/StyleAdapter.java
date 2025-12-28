package com.example.pandora.ui.adapter;

import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pandora.R;
import com.example.pandora.data.entity.StyleItem;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class StyleAdapter extends RecyclerView.Adapter<StyleAdapter.ViewHolder> {
    private List<StyleItem> styles;
    private int selectedPosition = 0;
    private OnStyleSelectedListener listener;

    public interface OnStyleSelectedListener {
        void onSelected(StyleItem style);
    }

    public StyleAdapter(List<StyleItem> styles, OnStyleSelectedListener listener) {
        this.styles = styles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_style, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StyleItem item = styles.get(position);
        holder.tvName.setText(item.getName());
        holder.layoutBg.setBackgroundResource(item.getBackgroundRes());

        // Hiệu ứng chọn
        if (selectedPosition == position) {
            holder.cardStyle.setStrokeWidth(6);
            holder.cardStyle.setStrokeColor(Color.parseColor("#FFD700")); // Màu vàng Gold
        } else {
            holder.cardStyle.setStrokeWidth(0);
        }

        holder.itemView.setOnClickListener(v -> {
            int oldPos = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPosition);
            listener.onSelected(item);
        });
    }

    @Override
    public int getItemCount() { return styles.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName; RelativeLayout layoutBg; MaterialCardView cardStyle;
        public ViewHolder(View v) { super(v);
            tvName = v.findViewById(R.id.tvStyleName);
            layoutBg = v.findViewById(R.id.layoutBackground);
            cardStyle = v.findViewById(R.id.cardStyle);
        }
    }
}