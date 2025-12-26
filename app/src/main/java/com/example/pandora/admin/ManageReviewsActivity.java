package com.example.pandora.admin;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.Review;
import com.example.pandora.admin.adapter.ReviewAdminAdapter;
import com.example.pandora.data.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageReviewsActivity extends AppCompatActivity {

    private RecyclerView recyclerReviews;
    private ReviewAdminAdapter adapter;
    private List<Review> reviews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_reviews);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        recyclerReviews = findViewById(R.id.recyclerReviews);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));

        loadReviews();
    }

    private void loadReviews() {
        // 👉 DÙNG API /reviews/admin
        RetrofitClient.getInstance().getApi()
                .getAllReviewsForAdmin()
                .enqueue(new Callback<List<Review>>() {
                    @Override
                    public void onResponse(Call<List<Review>> call,
                                           Response<List<Review>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            reviews = response.body();
                            adapter = new ReviewAdminAdapter(ManageReviewsActivity.this, reviews);
                            recyclerReviews.setAdapter(adapter);
                        } else {
                            Toast.makeText(ManageReviewsActivity.this,
                                    "Không thể tải đánh giá!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Review>> call, Throwable t) {
                        Toast.makeText(ManageReviewsActivity.this,
                                "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void deleteReview(Review review, int position) {
        String productName = review.getProductName() != null
                ? review.getProductName()
                : "sản phẩm";

        new AlertDialog.Builder(this)
                .setTitle("Xóa đánh giá")
                .setMessage("Xóa đánh giá của '" + review.getUsername()
                        + "' cho " + productName + " ?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (review.getId() == null) return;

                    RetrofitClient.getInstance().getApi()
                            .deleteReview(review.getId())
                            .enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        reviews.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        Toast.makeText(ManageReviewsActivity.this,
                                                "Đã xóa đánh giá!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ManageReviewsActivity.this,
                                                "Không thể xóa đánh giá!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(ManageReviewsActivity.this,
                                            "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
