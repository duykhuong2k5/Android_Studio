package com.example.pandora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.data.entity.FavoriteDTO;
import com.example.pandora.data.entity.Product;
import com.example.pandora.data.network.RetrofitClient;
import com.example.pandora.ui.adapter.ProductAdapter;
import com.example.pandora.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());


        recyclerView = findViewById(R.id.recyclerViewFavorite);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        loadFavoritesFromApi();
    }

    private void loadFavoritesFromApi() {

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập trước!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        RetrofitClient.getInstance().getApi().getFavoritesByUser(userId)
                .enqueue(new Callback<List<FavoriteDTO>>() {
                    @Override
                    public void onResponse(Call<List<FavoriteDTO>> call, Response<List<FavoriteDTO>> resp) {

                        if (resp.isSuccessful() && resp.body() != null) {

                            List<Product> favProducts = new ArrayList<>();

                            for (FavoriteDTO f : resp.body()) {
                                favProducts.add(new Product(
                                        f.getProductId(),
                                        f.getProductName(),
                                        String.valueOf(f.getPriceNew()),
                                        String.valueOf(f.getPriceOld()),
                                        f.getDiscountPercent(),
                                        f.getImageUrl(),
                                        f.getCategory()
                                ));
                            }

                            productAdapter = new ProductAdapter(FavoriteActivity.this, favProducts);
                            recyclerView.setAdapter(productAdapter);

                        } else {
                            Toast.makeText(FavoriteActivity.this, "Danh sách yêu thích trống!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<FavoriteDTO>> call, Throwable t) {
                        Log.e("FAVORITE_API", "Lỗi API:", t);
                        Toast.makeText(FavoriteActivity.this, "Không thể kết nối API!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
