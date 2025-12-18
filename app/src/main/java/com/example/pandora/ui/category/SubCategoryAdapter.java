package com.example.pandora.ui.category;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pandora.R;
import java.util.List;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolder> {

    List<String> list;
    OnClick listener;

    public interface OnClick {
        void onClick(String name);
    }

    public SubCategoryAdapter(List<String> list, OnClick listener) {
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sub_category, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = list.get(position);
        holder.txt.setText(name);
        holder.itemView.setOnClickListener(v -> listener.onClick(name));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt;
        ViewHolder(View v) {
            super(v);
            txt = v.findViewById(R.id.txtSubCategory);
        }
    }
}
