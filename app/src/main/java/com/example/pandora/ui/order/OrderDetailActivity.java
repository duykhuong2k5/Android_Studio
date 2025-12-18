package com.example.pandora.ui.order;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.Order;
import com.example.pandora.data.entity.OrderItem;
import com.example.pandora.data.network.RetrofitClient;
import com.example.pandora.ui.adapter.OrderItemAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView tvOrderInfo, tvDeliveryDate, tvTotal, tvAddress;
    private RecyclerView recyclerView;
    private Button btnCancelOrder;

    private long orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        orderId = getIntent().getLongExtra("orderId", 0);

        tvOrderInfo = findViewById(R.id.tvOrderInfo);
        tvDeliveryDate = findViewById(R.id.tvDeliveryDate);
        tvTotal = findViewById(R.id.tvTotal);
        tvAddress = findViewById(R.id.tvAddress);
        recyclerView = findViewById(R.id.recyclerOrderItems);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadOrderDetail();

        btnCancelOrder.setOnClickListener(v -> showCancelReasonPopup());
    }


    // ================================
    // 1) LOAD API CHI TIẾT ĐƠN
    // ================================
    private void loadOrderDetail() {
        RetrofitClient.getInstance().getApi()
                .getOrderDetail(orderId)
                .enqueue(new Callback<Order>() {
                    @Override
                    public void onResponse(Call<Order> call, Response<Order> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(OrderDetailActivity.this, "Không tải được đơn hàng!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Order order = response.body();
                        bindOrderData(order);
                    }

                    @Override
                    public void onFailure(Call<Order> call, Throwable t) {
                        Toast.makeText(OrderDetailActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // ================================
    // 2) HIỂN THỊ DỮ LIỆU LÊN UI
    // ================================
    private void bindOrderData(Order order) {

        // Thông tin đơn
        String info = "Đơn hàng #" + order.getId()
                + "\nTrạng thái: " + order.getStatus()
                + "\nNgày đặt: " + formatDate(order.getOrderDate());

        tvOrderInfo.setText(info);

        // Ngày giao dự kiến
        tvDeliveryDate.setText("🚚 Ngày giao dự kiến: " + calculateDeliveryDate(order.getOrderDate()));

        // Tổng tiền
        tvTotal.setText("Tổng cộng: " + String.format("%,.0f₫", order.getTotalPrice()));

        // Địa chỉ
        if (order.getAddress() != null) {
            String addr = order.getAddress().getFullName() + "\n"
                    + order.getAddress().getPhone() + "\n"
                    + order.getAddress().getHouse() + " " + order.getAddress().getStreet() + ", "
                    + order.getAddress().getWard() + ", "
                    + order.getAddress().getDistrict() + ", "
                    + order.getAddress().getProvince();
            tvAddress.setText(addr);
        }

        // Danh sách sản phẩm
        List<OrderItem> safeItems = order.getItems();
        if (safeItems == null) safeItems = new ArrayList<>();

        if (safeItems == null) safeItems = new ArrayList<>();

        recyclerView.setAdapter(new OrderItemAdapter(this, safeItems));


        // Nếu không phải PENDING → ẩn nút hủy
        if (!order.getStatus().equals("PENDING")) {
            btnCancelOrder.setVisibility(View.GONE);
        }
    }


    // ================================
    // 3) POPUP CHỌN LÝ DO HỦY ĐƠN
    // ================================
    private void showCancelReasonPopup() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.dialog_cancel_reason);

        String[] reasons = {
                "Tôi muốn cập nhật địa chỉ/sđt nhận hàng",
                "Tôi muốn thêm / thay đổi mã giảm giá",
                "Tôi muốn thay đổi sản phẩm (số lượng,...)",
                "Tôi không có nhu cầu mua nữa"
        };

        RecyclerView recycler = dialog.findViewById(R.id.recyclerReasons);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        recycler.setAdapter(new CancelReasonAdapter(reasons, reason -> {
            dialog.dismiss();
            cancelOrder(reason);
        }));

        dialog.show();
    }


    // ================================
    // 4) GỌI API HỦY ĐƠN
    // ================================
    private void cancelOrder(String reason) {
        Map<String, String> req = new HashMap<>();
        req.put("reason", reason);

        RetrofitClient.getInstance().getApi()
                .cancelOrder(orderId, req)
                .enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                        Toast.makeText(OrderDetailActivity.this, "Đã hủy đơn hàng!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
                        Toast.makeText(OrderDetailActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // ================================
    // 5) FORMAT DATE
    // ================================
    private String formatDate(String input) {
        try {
            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            return out.format(in.parse(input));
        } catch (Exception e) {
            return input;
        }
    }

    private String calculateDeliveryDate(String input) {
        try {
            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date d = in.parse(input);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            c.add(Calendar.DAY_OF_YEAR, 3);

            SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return out.format(c.getTime());
        } catch (Exception e) {
            return "Không xác định";
        }
    }
}
