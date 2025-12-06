package com.example.pandora.shipper;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.Order;
import com.example.pandora.data.network.RetrofitClient;
import com.example.pandora.ui.order.OrderHistoryAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipperHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerHistory;
    private ProgressBar progressLoading;
    private TextView tvEmpty;

    private OrderHistoryAdapter adapter;
    private final List<Order> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_history);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerHistory = findViewById(R.id.recyclerHistory);
        progressLoading = findViewById(R.id.progressLoading);
        tvEmpty = findViewById(R.id.tvEmpty);

        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderHistoryAdapter(this, orders); // dùng adapter có sẵn cho lịch sử đơn
        recyclerHistory.setAdapter(adapter);

        loadHistory();
    }

    private void loadHistory() {
        progressLoading.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        // Ở đây mình gọi /orders/all rồi lọc.
        // Nếu bạn có API riêng cho shipper thì thay cho phù hợp.
        RetrofitClient.getInstance()
                .getApi()
                .getAllOrders()
                .enqueue(new Callback<List<Order>>() {
                    @Override
                    public void onResponse(Call<List<Order>> call,
                                           Response<List<Order>> response) {
                        progressLoading.setVisibility(View.GONE);

                        if (!response.isSuccessful() || response.body() == null) {
                            showEmpty();
                            return;
                        }

                        List<Order> all = response.body();
                        orders.clear();

                        // Lọc những đơn đã giao hoặc giao thất bại
                        for (Order o : all) {
                            String st = o.getStatus();
                            if ("COMPLETED".equalsIgnoreCase(st)
                                    || "FAILED".equalsIgnoreCase(st)) {
                                orders.add(o);
                            }
                        }

                        if (orders.isEmpty()) {
                            showEmpty();
                        } else {
                            tvEmpty.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Order>> call, Throwable t) {
                        progressLoading.setVisibility(View.GONE);
                        showEmpty();
                    }
                });
    }

    private void showEmpty() {
        tvEmpty.setVisibility(View.VISIBLE);
        orders.clear();
        adapter.notifyDataSetChanged();
    }
}
