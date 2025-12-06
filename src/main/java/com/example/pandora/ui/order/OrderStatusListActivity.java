package com.example.pandora.ui.order;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.Order;
import com.example.pandora.data.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatusListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvStatusTitle, tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status_list);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerStatusOrders);
        tvStatusTitle = findViewById(R.id.tvStatusTitle);
        tvEmpty = findViewById(R.id.tvEmpty);

        String statusGroup = getIntent().getStringExtra("statusGroup");
        if (statusGroup == null) statusGroup = "";

        setupTitle(statusGroup);
        loadOrdersByStatus(statusGroup);
    }

    private void setupTitle(String group) {
        switch (group) {
            case "PROCESSING":
                tvStatusTitle.setText("🟡 Đơn đang xử lý");
                break;
            case "DELIVERING":
                tvStatusTitle.setText("🔵 Đơn đang giao");
                break;
            case "COMPLETED":
                tvStatusTitle.setText("🟢 Đơn hoàn thành");
                break;
            case "FAILED":
                tvStatusTitle.setText("🔴 Đơn đã hủy / thất bại");
                break;
            default:
                tvStatusTitle.setText("Danh sách đơn hàng");
        }
    }

    private void loadOrdersByStatus(String group) {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String email = prefs.getString("email", "");

        if (email.isEmpty()) {
            tvEmpty.setText("Không tìm thấy tài khoản!");
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        }

        // gọi API: getUser -> lấy userId trước
        RetrofitClient.getInstance().getApi().getUserByEmail(email)
                .enqueue(new Callback<com.example.pandora.data.entity.User>() {
                    @Override
                    public void onResponse(Call<com.example.pandora.data.entity.User> call, Response<com.example.pandora.data.entity.User> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            tvEmpty.setText("Không tìm thấy người dùng!");
                            tvEmpty.setVisibility(View.VISIBLE);
                            return;
                        }

                        long userId = response.body().getId();
                        getOrders(userId, group);
                    }

                    @Override
                    public void onFailure(Call<com.example.pandora.data.entity.User> call, Throwable t) {
                        tvEmpty.setText("Lỗi kết nối user!");
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void getOrders(long userId, String group) {
        RetrofitClient.getInstance().getApi().getOrdersByUser(userId)
                .enqueue(new Callback<List<Order>>() {
                    @Override
                    public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            showEmpty("Không có đơn hàng!");
                            return;
                        }

                        List<Order> filtered = filterOrders(response.body(), group);
                        if (filtered.isEmpty()) {
                            showEmpty("Không có đơn thuộc trạng thái này.");
                        } else {
                            showOrders(filtered);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Order>> call, Throwable t) {
                        showEmpty("Lỗi tải đơn hàng!");
                    }
                });
    }

    private List<Order> filterOrders(List<Order> input, String group) {
        List<Order> output = new ArrayList<>();

        for (Order o : input) {
            String s = o.getStatus();

            switch (group) {
                case "PROCESSING":
                    if (s.equals("PENDING") ||
                            s.equals("CUSTOMER_PAID") ||
                            s.equals("WAITING_SHIPPER"))
                        output.add(o);
                    break;
                case "DELIVERING":
                    if (s.equals("DELIVERING"))
                        output.add(o);
                    break;
                case "COMPLETED":
                    if (s.equals("COMPLETED"))
                        output.add(o);
                    break;
                case "FAILED":
                    if (s.equals("FAILED"))
                        output.add(o);
                    break;
            }
        }
        return output;
    }

    private void showEmpty(String msg) {
        recyclerView.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
        tvEmpty.setText(msg);
    }

    private void showOrders(List<Order> orders) {
        recyclerView.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new OrderHistoryAdapter(this, orders));
    }
}
