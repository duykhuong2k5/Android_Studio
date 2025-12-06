package com.example.pandora.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pandora.R;
import com.example.pandora.data.entity.FavoriteDTO;
import com.example.pandora.data.entity.FavoriteRequest;
import com.example.pandora.data.entity.Product;
import com.example.pandora.data.entity.User;
import com.example.pandora.data.network.RetrofitClient;
import com.example.pandora.ui.product.ProductDetailActivity;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context context;
    private final List<Product> products;

    private boolean isCombo = false;
    private String comboGift = "";
    private String comboRule = "";


    private boolean isCompact;


    private final Set<Long> favoriteIds = new HashSet<>();
    private final Map<Long, Long> soldCache = new HashMap<>();
    private Long currentUserId = null;
    private boolean favoritesLoaded = false;


    // ⭐ CONSTRUCTOR MỚI — HỖ TRỢ COMBO
    // Constructor chuẩn
    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products != null ? products : new ArrayList<>();
        this.isCompact = false;
        this.isCombo = false;
        initFavorites();
    }

    // Constructor compact (HomeActivity, SearchActivity)
    public ProductAdapter(Context context, List<Product> products, boolean isCompact) {
        this.context = context;
        this.products = products != null ? products : new ArrayList<>();
        this.isCompact = isCompact;
        this.isCombo = false;
        initFavorites();
    }

    // Constructor combo (ProductListActivity)
    public ProductAdapter(Context context, List<Product> products, boolean isCombo, String comboGift, String comboRule) {
        this.context = context;
        this.products = products != null ? products : new ArrayList<>();
        this.isCompact = false; // combo luôn full size
        this.isCombo = isCombo;
        this.comboGift = comboGift;
        this.comboRule = comboRule;
        initFavorites();
    }




    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        TextView tvName, tvNewPrice, tvOldPrice, tvPercent, tvSold;

        // 🔥 BANNER + COMBO RULE
        TextView tvGiftBanner, tvComboBanner, tvComboRule;

        ImageButton btnFav;

        public ProductViewHolder(View view) {
            super(view);

            imgProduct = view.findViewById(R.id.imgProduct);
            tvName = view.findViewById(R.id.tvProductName);
            tvNewPrice = view.findViewById(R.id.tvDiscountPrice);
            tvOldPrice = view.findViewById(R.id.tvOldPrice);
            tvPercent = view.findViewById(R.id.tvDiscountPercent);
            tvSold = view.findViewById(R.id.tvSoldCount);

            tvGiftBanner = view.findViewById(R.id.tvGiftBanner);       // 🔥 Đã thêm
            tvComboBanner = view.findViewById(R.id.tvComboBanner);     // 🔥 Đã thêm
            tvComboRule = view.findViewById(R.id.tvComboRule);         // 🔥 Đã thêm

            btnFav = view.findViewById(R.id.btnFavorite);
        }
    }


    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }
    // ⭐ Cập nhật lại danh sách sản phẩm (Home, Favorite, Search)
    public void updateList(List<Product> newList) {
        products.clear();
        products.addAll(newList);
        notifyDataSetChanged();
    }



    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {

        Product product = products.get(position);

        boolean isGift = "QUÀ TẶNG".equalsIgnoreCase(product.getCategory());

        // ============ ⭐ HIỆN BANNER ============

        if (isGift) {
            holder.tvGiftBanner.setVisibility(View.VISIBLE);
            holder.tvComboBanner.setVisibility(View.GONE);

            holder.tvComboRule.setVisibility(View.VISIBLE);
            holder.tvComboRule.setText(comboRule);     // 🔥 ĐÃ SỬA: hiển thị mô tả combo
        }
        else if (isCombo) {
            holder.tvGiftBanner.setVisibility(View.GONE);
            holder.tvComboBanner.setVisibility(View.VISIBLE);

            holder.tvComboRule.setVisibility(View.VISIBLE);
            holder.tvComboRule.setText(comboRule);
        }
        else {
            holder.tvGiftBanner.setVisibility(View.GONE);
            holder.tvComboBanner.setVisibility(View.GONE);
            holder.tvComboRule.setVisibility(View.GONE);
        }


        // ============ ⭐ 1. HIỂN THỊ QUÀ TẶNG ============

        if (isGift) {

            Glide.with(context)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imgProduct);

            holder.tvName.setText(product.getName());

            holder.tvNewPrice.setText("0₫ (Quà tặng)");
            holder.tvOldPrice.setVisibility(View.GONE);
            holder.tvPercent.setVisibility(View.GONE);
            holder.tvSold.setVisibility(View.GONE);
            holder.btnFav.setVisibility(View.GONE);

            // ❌ Không cho click vào quà tặng
            holder.itemView.setOnClickListener(v ->
                    Toast.makeText(context, "Đây là sản phẩm quà tặng Combo!", Toast.LENGTH_SHORT).show()
            );

            return;
        }


        // ============ ⭐ 2. HIỂN THỊ SẢN PHẨM BÌNH THƯỜNG ============

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .into(holder.imgProduct);

        holder.tvName.setText(product.getName());
        holder.tvNewPrice.setText(formatPrice(product.getPriceNew()));

        holder.tvOldPrice.setText(formatPrice(product.getPriceOld()));
        holder.tvOldPrice.setPaintFlags(holder.tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.tvPercent.setText(product.getDiscountPercent());

        loadSoldCount(holder, product);


        // ⭐ YÊU THÍCH
        if (favoritesLoaded && product.getId() != null && favoriteIds.contains(product.getId())) {
            holder.btnFav.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite));
            holder.btnFav.setTag("liked");
        } else {
            holder.btnFav.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_border));
            holder.btnFav.setTag("unliked");
        }

        holder.btnFav.setOnClickListener(v -> toggleFavorite(holder, product));


        // ⭐ CLICK PRODUCT → Details
        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("productId", product.getId());
            intent.putExtra("productName", product.getName());
            intent.putExtra("productPrice", product.getPriceNew());
            intent.putExtra("productImageUrl", product.getImageUrl());
            intent.putExtra("productCategory", product.getCategory());
            context.startActivity(intent);
        });
    }


    // ============ ⭐ LOAD SOLD COUNT ============

    private void loadSoldCount(ProductViewHolder holder, Product product) {

        if (product.getId() == null) { // 🔥 QUÀ TẶNG KHÔNG CÓ ID
            holder.tvSold.setText("");
            return;
        }

        Long id = product.getId();

        if (soldCache.containsKey(id)) {
            holder.tvSold.setText("Đã bán " + soldCache.get(id));
            return;
        }

        holder.tvSold.setText("Đang tải...");

        RetrofitClient.getInstance().getApi().getProductSold(id)
                .enqueue(new Callback<Long>() {
                    @Override
                    public void onResponse(Call<Long> call, Response<Long> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            soldCache.put(id, response.body());
                            holder.tvSold.setText("Đã bán " + response.body());
                        }
                        else holder.tvSold.setText("Đã bán 0");
                    }

                    @Override
                    public void onFailure(Call<Long> call, Throwable t) {
                        holder.tvSold.setText("Đã bán 0");
                    }
                });
    }


    // ============ ⭐ FAVORITES ============

    private void initFavorites() {
        String email = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                .getString("email", null);

        if (email == null) return;

        RetrofitClient.getInstance().getApi().getUserByEmail(email)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            currentUserId = response.body().getId();
                            loadFavoriteList();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {}
                });
    }

    private void loadFavoriteList() {

        if (currentUserId == null) return;

        RetrofitClient.getInstance().getApi().getFavoritesByUser(currentUserId)
                .enqueue(new Callback<List<FavoriteDTO>>() {

                    @Override
                    public void onResponse(Call<List<FavoriteDTO>> call, Response<List<FavoriteDTO>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            favoriteIds.clear();
                            for (FavoriteDTO f : response.body()) {
                                favoriteIds.add(f.getProductId());
                            }

                            favoritesLoaded = true;
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<FavoriteDTO>> call, Throwable t) {}
                });
    }

    private void toggleFavorite(ProductViewHolder holder, Product product) {

        if (product.getId() == null) { // QUÀ TẶNG
            Toast.makeText(context, "Không thể yêu thích quà tặng!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUserId == null) {
            Toast.makeText(context, "Bạn cần đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isLiked = "liked".equals(holder.btnFav.getTag());

        if (isLiked) {

            RetrofitClient.getInstance().getApi().getFavoritesByUser(currentUserId)
                    .enqueue(new Callback<List<FavoriteDTO>>() {

                        @Override
                        public void onResponse(Call<List<FavoriteDTO>> call, Response<List<FavoriteDTO>> response) {

                            if (response.isSuccessful() && response.body() != null) {

                                for (FavoriteDTO f : response.body()) {

                                    if (f.getProductId().equals(product.getId())) {

                                        RetrofitClient.getInstance().getApi().deleteFavorite(f.getId())
                                                .enqueue(new Callback<Void>() {

                                                    @Override
                                                    public void onResponse(Call<Void> call, Response<Void> resp) {

                                                        if (resp.isSuccessful()) {
                                                            favoriteIds.remove(product.getId());
                                                            holder.btnFav.setImageResource(R.drawable.ic_favorite_border);
                                                            holder.btnFav.setTag("unliked");
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Void> call, Throwable t) {}
                                                });

                                        break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<FavoriteDTO>> call, Throwable t) {}
                    });

        } else {

            FavoriteRequest req = new FavoriteRequest(currentUserId, product.getId());

            RetrofitClient.getInstance().getApi().addFavorite(req)
                    .enqueue(new Callback<FavoriteDTO>() {
                        @Override
                        public void onResponse(Call<FavoriteDTO> call, Response<FavoriteDTO> response) {

                            if (response.isSuccessful()) {
                                favoriteIds.add(product.getId());
                                holder.btnFav.setImageResource(R.drawable.ic_favorite);
                                holder.btnFav.setTag("liked");
                            }
                        }

                        @Override
                        public void onFailure(Call<FavoriteDTO> call, Throwable t) {}
                    });
        }
    }


    private String formatPrice(Object price) {
        if (price == null) return "";
        try {
            double number = Double.parseDouble(price.toString());
            return String.format("%,.0f₫", number);
        } catch (Exception e) {
            return price.toString();
        }
    }


    @Override
    public int getItemCount() {
        return products.size();
    }
}
