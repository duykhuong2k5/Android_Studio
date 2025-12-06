package com.example.pandora.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pandora.HomeActivity;
import com.example.pandora.address.AddressListActivity;
import com.example.pandora.data.entity.FavoriteDTO;
import com.example.pandora.ui.adapter.ProductAdapter;
import com.example.pandora.ui.login.LoginActivity;
import com.example.pandora.R;
import com.example.pandora.data.entity.Favorite;
import com.example.pandora.data.entity.Order;
import com.example.pandora.data.entity.Product;
import com.example.pandora.data.entity.User;
import com.example.pandora.data.network.RetrofitClient;
import com.example.pandora.ui.cart.CartActivity;
import com.example.pandora.ui.order.OrderHistoryAdapter;
import com.example.pandora.ui.order.OrderMenuActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvPhone, tvAddress, tvMemberRank, tvPurchaseHistory;
    private RecyclerView recyclerFavorite;
    private Button btnLogout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        TextView tvTitle = findViewById(R.id.tvTitle);

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // đóng ProfileActivity
            }
        });


        ImageButton btnCart = findViewById(R.id.btnCart);
        ImageButton btnProfile = findViewById(R.id.btnProfile);
        ImageView imgBanner = findViewById(R.id.imgProfileBanner);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvMemberRank = findViewById(R.id.tvMemberRank);
        recyclerFavorite = findViewById(R.id.recyclerFavorite);
        btnLogout = findViewById(R.id.btnLogout);

        LinearLayout btnMyOrders = findViewById(R.id.btnMyOrders);

        btnMyOrders.setOnClickListener(v -> {
            Intent i = new Intent(ProfileActivity.this, OrderMenuActivity.class);
            startActivity(i);
        });





        // 🖼 Banner
        Glide.with(this)
                .load(R.drawable.profile_banner)
                .into(imgBanner);

        btnCart.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        // 🧑‍💻 Lấy email đăng nhập
        String email = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("email", "");

        if (email != null && !email.isEmpty()) {
            RetrofitClient.getInstance().getApi().getUserByEmail(email)
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                User user = response.body();
                                tvName.setText("Tên: " + user.getFullName());
                                tvEmail.setText("Email: " + user.getEmail());
                                tvPhone.setText("Số điện thoại: " + (user.getPhone() != null ? user.getPhone() : "Chưa có"));
                                tvAddress.setText("Địa chỉ: " + (user.getAddress() != null ? user.getAddress() : "Chưa cập nhật"));
                                tvMemberRank.setText("Hạng thành viên: BRONZE");

                                loadFavoriteProducts(user.getId(), recyclerFavorite);
                            } else {
                                Toast.makeText(ProfileActivity.this, "Không tìm thấy người dùng!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast.makeText(ProfileActivity.this, "Lỗi khi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // 🚪 Đăng xuất
        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("user_prefs", MODE_PRIVATE).edit().clear().apply();
            Toast.makeText(this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void loadFavoriteProducts(Long userId, RecyclerView recyclerFavorite) {
        RetrofitClient.getInstance().getApi().getFavoritesByUser(userId)
                .enqueue(new Callback<List<FavoriteDTO>>() {
                    @Override
                    public void onResponse(Call<List<FavoriteDTO>> call, Response<List<FavoriteDTO>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<FavoriteDTO> favorites = response.body();
                            if (!favorites.isEmpty()) {
                                List<Product> favoriteProducts = new ArrayList<>();
                                for (FavoriteDTO fav : favorites) {
                                    favoriteProducts.add(new Product(
                                            fav.getProductId(),
                                            fav.getProductName(),
                                            String.valueOf(fav.getPriceNew()),
                                            String.valueOf(fav.getPriceOld()),
                                            fav.getDiscountPercent(),
                                            fav.getImageUrl(),
                                            fav.getCategory()
                                    ));
                                }

                                recyclerFavorite.setLayoutManager(new LinearLayoutManager(ProfileActivity.this, LinearLayoutManager.HORIZONTAL, false));
                                recyclerFavorite.setAdapter(new ProductAdapter(ProfileActivity.this, favoriteProducts, true));
                            } else {
                                Toast.makeText(ProfileActivity.this, "Chưa có sản phẩm yêu thích", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "Không thể tải sản phẩm yêu thích", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<FavoriteDTO>> call, Throwable t) {
                        Toast.makeText(ProfileActivity.this, "Lỗi khi tải sản phẩm yêu thích", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
