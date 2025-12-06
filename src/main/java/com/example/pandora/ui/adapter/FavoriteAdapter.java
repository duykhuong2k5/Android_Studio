package com.example.pandora.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pandora.R;
import com.example.pandora.data.entity.FavoriteDTO;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private final List<FavoriteDTO> favoriteList;
    private final Context context;

    public FavoriteAdapter(Context context, List<FavoriteDTO> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.favorite_item, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FavoriteDTO item = favoriteList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct, btnFav;
        TextView tvName, tvPrice;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct = itemView.findViewById(R.id.imageFavoriteItem);
            tvName = itemView.findViewById(R.id.textFavoriteItem);
            tvPrice = itemView.findViewById(R.id.textFavoritePrice);
            btnFav = itemView.findViewById(R.id.btnFavRemove);
        }

        public void bind(FavoriteDTO item) {

            // 📝 Tên sản phẩm
            tvName.setText(item.getProductName());

            // 📝 Giá sản phẩm
            tvPrice.setText(String.format("%,.0f₫", item.getPriceNew()));

            // 📝 Ảnh sản phẩm
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.sample_image)
                    .error(R.drawable.sample_image)
                    .into(imgProduct);

            // ❤️ Icon yêu thích đỏ (chỉ hiển thị)
            btnFav.setImageResource(R.drawable.ic_favorite_red);
        }
    }
}
