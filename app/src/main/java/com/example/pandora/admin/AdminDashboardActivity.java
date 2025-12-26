package com.example.pandora.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pandora.HomeActivity;
import com.example.pandora.R;
import com.example.pandora.chat.ChatListActivity;
import com.example.pandora.data.entity.Order;
import com.example.pandora.data.entity.Product;
import com.example.pandora.data.entity.Review;
import com.example.pandora.data.entity.User;
import com.example.pandora.data.network.RetrofitClient;
import com.example.pandora.ui.login.LoginActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity {

    // TextView hiển thị số lượng
    private TextView tvProductCount, tvUserCount, tvOrderCount, tvReviewCount;

    // Card
    private MaterialCardView cardProducts, cardUsers, cardOrders,
            cardReviews, cardRevenue, cardBackHome;

    private MaterialButton btnLogout;
    private MaterialCardView cardChats;

    // Email admin (nếu có truyền khi login)
    private String adminEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manager_dashboard);

        // Nếu lúc login bạn có gửi kèm email admin thì lấy ra
        adminEmail = getIntent().getStringExtra("email");
        if (adminEmail == null) adminEmail = "";

        initViews();
        initClicks();
        loadDashboardCounts();
    }

    private void initViews() {
        // TextView số lượng
        tvProductCount = findViewById(R.id.tvProductCount);
        tvUserCount    = findViewById(R.id.tvUserCount);
        tvOrderCount   = findViewById(R.id.tvOrderCount);
        tvReviewCount  = findViewById(R.id.tvReviewCount);

        // Card
        cardProducts = findViewById(R.id.cardProducts);
        cardUsers    = findViewById(R.id.cardUsers);
        cardOrders   = findViewById(R.id.cardOrders);
        cardReviews  = findViewById(R.id.cardReviews);
        cardRevenue  = findViewById(R.id.cardRevenue);
        cardBackHome = findViewById(R.id.cardBackHome);

        btnLogout = findViewById(R.id.btnLogout);
        cardChats = findViewById(R.id.cardChats);
    }

    private void initClicks() {
        // Product Management
        cardProducts.setOnClickListener(v ->
                startActivity(new Intent(this, ManageProductsActivity.class)));

        // User Management
        cardUsers.setOnClickListener(v ->
                startActivity(new Intent(this, ManageUsersActivity.class)));

        // Order Management
        cardOrders.setOnClickListener(v ->
                startActivity(new Intent(this, ManageOrdersActivity.class)));

        // Review Management
        cardReviews.setOnClickListener(v ->
                startActivity(new Intent(this, ManageReviewsActivity.class)));
        cardChats.setOnClickListener(v ->
                startActivity(new Intent(this, ChatListActivity.class))
        );

        // Revenue Management
        cardRevenue.setOnClickListener(v ->
                startActivity(new Intent(this, ManageRevenueActivity.class)));

        // Back to Home
        cardBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        });

        // Đăng xuất: quay về Home (hoặc màn Login nếu bạn có)
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    /** Gọi API để lấy số lượng từng loại và hiển thị trên các card */
    private void loadDashboardCounts() {
        // 1. Đếm sản phẩm
        RetrofitClient.getInstance().getApi()
                .getAllProducts()
                .enqueue(new Callback<List<Product>>() {
                    @Override
                    public void onResponse(Call<List<Product>> call,
                                           Response<List<Product>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            int count = response.body().size();
                            tvProductCount.setText(count + " products");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Product>> call, Throwable t) {
                        Toast.makeText(AdminDashboardActivity.this,
                                "Lỗi tải số lượng sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                });

        // 2. Đếm user (API yêu cầu email – tạm truyền adminEmail hoặc chuỗi rỗng)
        RetrofitClient.getInstance().getApi()
                .getAllUsers(adminEmail)
                .enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> call,
                                           Response<List<User>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            int count = response.body().size();
                            tvUserCount.setText(count + " users");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<User>> call, Throwable t) {
                        Toast.makeText(AdminDashboardActivity.this,
                                "Lỗi tải số lượng người dùng", Toast.LENGTH_SHORT).show();
                    }
                });

        // 3. Đếm đơn hàng
        RetrofitClient.getInstance().getApi()
                .getAllOrders()
                .enqueue(new Callback<List<Order>>() {
                    @Override
                    public void onResponse(Call<List<Order>> call,
                                           Response<List<Order>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            int count = response.body().size();
                            tvOrderCount.setText(count + " orders");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Order>> call, Throwable t) {
                        Toast.makeText(AdminDashboardActivity.this,
                                "Lỗi tải số lượng đơn hàng", Toast.LENGTH_SHORT).show();
                    }
                });

        // 4. Đếm review
        RetrofitClient.getInstance().getApi()
                .getAllReviews()
                .enqueue(new Callback<List<Review>>() {
                    @Override
                    public void onResponse(Call<List<Review>> call,
                                           Response<List<Review>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            int count = response.body().size();
                            tvReviewCount.setText(count + " reviews");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Review>> call, Throwable t) {
                        Toast.makeText(AdminDashboardActivity.this,
                                "Lỗi tải số lượng review", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
