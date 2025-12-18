package com.example.pandora.admin;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.data.entity.Order;
import com.example.pandora.R;
import com.example.pandora.admin.adapter.OrderAdminAdapter;
import com.example.pandora.data.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerOrders;
    private OrderAdminAdapter adapter;
    private List<Order> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_orders);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        recyclerOrders = findViewById(R.id.recyclerOrders);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));

        loadOrders();
    }

    private void loadOrders() {
        RetrofitClient.getInstance().getApi().getAllOrders().enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orders = response.body();
                    adapter = new OrderAdminAdapter(ManageOrdersActivity.this, orders);
                    recyclerOrders.setAdapter(adapter);
                } else {
                    Toast.makeText(ManageOrdersActivity.this, "Không thể tải danh sách đơn hàng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(ManageOrdersActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}