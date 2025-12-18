package com.example.pandora.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.admin.adapter.OrderItemAdminAdapter;
import com.example.pandora.data.entity.Order;
import com.example.pandora.data.entity.OrderItem;
import com.example.pandora.data.network.RetrofitClient;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailAdminActivity extends AppCompatActivity {

    private TextView tvOrderCode, tvStatus, tvOrderDate, tvEstimate,
            tvTotal, tvCustomerName, tvCustomerPhone, tvCustomerAddress;
    private RecyclerView recyclerItems;
    private ProgressBar progressLoading;
    private long orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_admin);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        orderId = getIntent().getLongExtra("order_id", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Thiếu mã đơn hàng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        loadOrderDetail();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        tvOrderCode = findViewById(R.id.tvOrderCode);
        tvStatus = findViewById(R.id.tvStatus);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvEstimate = findViewById(R.id.tvEstimate);
        tvTotal = findViewById(R.id.tvTotal);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        tvCustomerAddress = findViewById(R.id.tvCustomerAddress);
        recyclerItems = findViewById(R.id.recyclerItems);
        progressLoading = findViewById(R.id.progressLoading);
    }

    private void setupRecyclerView() {
        recyclerItems.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadOrderDetail() {
        progressLoading.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance().getApi()
                .getOrderDetail(orderId)
                .enqueue(new Callback<Order>() {
                    @Override
                    public void onResponse(Call<Order> call, Response<Order> response) {
                        progressLoading.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            bindOrder(response.body());
                        } else {
                            Toast.makeText(OrderDetailAdminActivity.this,
                                    "Không tải được chi tiết đơn hàng!",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Order> call, Throwable t) {
                        progressLoading.setVisibility(View.GONE);
                        Toast.makeText(OrderDetailAdminActivity.this,
                                "Lỗi: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void bindOrder(Order order) {
        tvOrderCode.setText("Đơn hàng #" + order.getId());
        tvStatus.setText("Trạng thái: " + order.getStatus());

        // format ngày đặt
        // Nếu orderDate là String ISO, bạn chỉnh lại parse phù hợp
        try {
            // ví dụ orderDate dạng "2025-11-30T22:58:00"
            LocalDateTime dt = LocalDateTime.parse(order.getOrderDate());
            String formatted = dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            tvOrderDate.setText("Ngày đặt: " + formatted);

            // Ngày giao dự kiến +3 ngày
            LocalDateTime estimate = dt.plusDays(3);
            String estimateStr = estimate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            tvEstimate.setText("Ngày giao dự kiến: " + estimateStr);
        } catch (Exception e) {
            tvOrderDate.setText("Ngày đặt: " + order.getOrderDate());
            tvEstimate.setText("Ngày giao dự kiến: --/--/----");
        }

        tvTotal.setText("Tổng cộng: " + formatPrice(order.getTotalPrice()));

        // ===== THÔNG TIN KHÁCH HÀNG =====
        if (order.getAddress() != null) {
            // Lấy từ Address (theo JSON backend gửi)
            tvCustomerName.setText(order.getAddress().getFullName());
            tvCustomerPhone.setText(order.getAddress().getPhone());
            tvCustomerAddress.setText(order.getAddress().getFullAddress());
            // Nếu chưa có getFullAddress(), có thể ghép tay:
            // String addr = order.getAddress().getHouse() + ", " +
            //               order.getAddress().getStreet() + ", " +
            //               order.getAddress().getWard() + ", " +
            //               order.getAddress().getDistrict() + ", " +
            //               order.getAddress().getProvince();
            // tvCustomerAddress.setText(addr);
        } else if (order.getUser() != null) {
            // fallback: nếu sau này có user mà không có address
            tvCustomerName.setText(order.getUser().getFullName());
            tvCustomerPhone.setText(order.getUser().getPhone());
            tvCustomerAddress.setText("Không có địa chỉ");
        } else {
            tvCustomerName.setText("Không có tên khách");
            tvCustomerPhone.setText("Không có SĐT");
            tvCustomerAddress.setText("Không có địa chỉ");
        }


        List<OrderItem> items = order.getItems();
        OrderItemAdminAdapter adapter = new OrderItemAdminAdapter(this, items);
        recyclerItems.setAdapter(adapter);
    }

    private String formatPrice(double price) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(price) + "đ";
    }
}
