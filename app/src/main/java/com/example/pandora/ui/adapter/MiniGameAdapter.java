package com.example.pandora.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.MiniGameItem;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class MiniGameAdapter extends RecyclerView.Adapter<MiniGameAdapter.VH> {

    private final List<MiniGameItem> data = new ArrayList<>();

    public void setData(List<MiniGameItem> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mini_game_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        MiniGameItem item = data.get(position);

        h.tvTag.setText(item.tag);
        h.tvTitle.setText(item.title);
        h.tvDesc.setText(item.desc);
        h.tvAction.setText(item.actionText);

        h.leftBlock.setBackgroundResource(item.leftBgRes);
        h.icon.setImageResource(item.iconRes);

        h.tvAction.setTextColor(ContextCompat.getColor(h.itemView.getContext(), item.actionColor));

        h.card.setOnClickListener(v -> {
            Context ctx = v.getContext();
            if (item.targetActivity != null) {
                ctx.startActivity(new Intent(ctx, item.targetActivity));
            }
        });
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        MaterialCardView card;
        FrameLayout leftBlock;
        ImageView icon;
        TextView tvTag, tvTitle, tvDesc, tvAction;

        VH(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardMiniGame);
            leftBlock = itemView.findViewById(R.id.leftBlock);
            icon = itemView.findViewById(R.id.imgIcon);
            tvTag = itemView.findViewById(R.id.tvTag);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvAction = itemView.findViewById(R.id.tvAction);
        }
    }
}
