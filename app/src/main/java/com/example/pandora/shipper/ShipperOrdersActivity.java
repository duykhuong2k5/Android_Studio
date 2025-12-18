package com.example.pandora.shipper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.Order;
import com.example.pandora.data.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipperOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_orders);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerShipperOrders);
        progressBar = findViewById(R.id.progressLoading);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadOrdersForShipper();
    }

    private void loadOrdersForShipper() {
        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance().getApi().getAllOrders()
                .enqueue(new Callback<List<Order>>() {
                    @Override
                    public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            List<Order> orders = response.body();

                            // SHIPPER thấy: WAITING_SHIPPER + DELIVERING
                            orders.removeIf(o ->
                                    !("WAITING_SHIPPER".equals(o.getStatus())
                                            || "DELIVERING".equals(o.getStatus()))
                            );

                            recyclerView.setAdapter(new ShipperOrderAdapter(
                                    ShipperOrdersActivity.this,
                                    orders,
                                    new ShipperOrderAdapter.OnOrderActionListener() {
                                        @Override
                                        public void onViewDetail(Order order) {
                                            // chỉ gọi khi status = DELIVERING
                                            openOrderDetail(order);
                                        }

                                        @Override
                                        public void onAcceptOrder(Order order) {
                                            // WAITING_SHIPPER → shipper nhận đơn rồi mới cho vào chi tiết
                                            acceptAndOpen(order);
                                        }
                                    }
                            ));

                        } else {
                            Toast.makeText(ShipperOrdersActivity.this,
                                    "Không tải được dữ liệu!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Order>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ShipperOrdersActivity.this,
                                "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /** Mở màn chi tiết đơn hàng */
    private void openOrderDetail(Order order) {
        Intent intent = new Intent(this, ShipperOrderDetailActivity.class);

        intent.putExtra("orderId", order.getId());
        intent.putExtra("status", order.getStatus());
        intent.putExtra("total", order.getTotalPrice());

        // danh sách sản phẩm
        intent.putExtra("items", new java.util.ArrayList<>(order.getItems()));

        // ====== TÊN KHÁCH HÀNG ======
        String customerName = "Khách chưa rõ tên";
        if (order.getAddress().getFullName() != null &&
                !order.getAddress().getFullName().isEmpty()) {
            customerName = order.getAddress().getFullName();
        }
        intent.putExtra("customerName", customerName);

        // ====== ĐỊA CHỈ & SĐT TỪ ORDER.ADDRESS ======
        String address;
        String phone;

        if (order.getAddress() != null) {
            address = order.getAddress().getFullAddress();
            phone = order.getAddress().getPhone() != null
                    ? order.getAddress().getPhone()
                    : "Không rõ SĐT";
        } else {
            address = "Không rõ địa chỉ";
            phone   = "Không rõ SĐT";
        }

        intent.putExtra("address", address);
        intent.putExtra("phone", phone);

        startActivity(intent);
    }
    private void acceptAndOpen(Order order) {
        RetrofitClient.getInstance()
                .getApi()
                .shipperAccept(order.getId())
                .enqueue(new retrofit2.Callback<java.util.Map<String, String>>() {
                    @Override
                    public void onResponse(Call<java.util.Map<String, String>> call,
                                           Response<java.util.Map<String, String>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ShipperOrdersActivity.this,
                                    "Đã nhận đơn!", Toast.LENGTH_SHORT).show();

                            // cập nhật trạng thái local → để Detail nhận đúng status
                            order.setStatus("DELIVERING");

                            // mở màn chi tiết
                            openOrderDetail(order);
                        } else {
                            Toast.makeText(ShipperOrdersActivity.this,
                                    "Không thể nhận đơn!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<java.util.Map<String, String>> call, Throwable t) {
                        Toast.makeText(ShipperOrdersActivity.this,
                                "Lỗi mạng khi nhận đơn!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
