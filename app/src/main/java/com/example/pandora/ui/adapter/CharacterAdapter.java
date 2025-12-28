package com.example.pandora.ui.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pandora.R;
import com.example.pandora.data.entity.CharacterModel;

import java.util.List;

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.ViewHolder> {

    private List<CharacterModel> characterList;

    public CharacterAdapter(List<CharacterModel> characterList) {
        this.characterList = characterList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng layout item_character đã thiết kế với viền vàng lấp lánh
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_character, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CharacterModel item = characterList.get(position);

        // 1. Hiển thị Tên và Tuổi để bé học từ vựng (Ví dụ: "Leo (5)")
        if (item.name == null || item.name.isEmpty() || item.name.equals("NoName")) {
            holder.tvName.setText("Who is this?");
            holder.imgHero.setAlpha(0.2f); // Hiệu ứng mờ cho nhân vật chưa tạo
        } else {
            String displayName = item.name + " (" + item.age + ")";
            holder.tvName.setText(displayName);
            holder.imgHero.setAlpha(1.0f); // Hiện rõ khi đã có thông tin
        }

        // 2. Thiết lập hình ảnh nhân vật
        holder.imgHero.setImageResource(item.imageResId);

        // 3. (Tùy chọn) Xử lý màu sắc dựa trên giới tính để bé phân biệt
        // Bạn có thể đổi màu chữ tvName thành xanh cho Boy hoặc hồng cho Girl nếu muốn
    }

    @Override
    public int getItemCount() {
        return characterList != null ? characterList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView imgHero;

        public ViewHolder(@NonNull View v) {
            super(v);
            // Ánh xạ đúng ID từ file item_character.xml
            tvName = v.findViewById(R.id.tvHeroName);
            imgHero = v.findViewById(R.id.imgHeroSilhouette);
        }
    }
}