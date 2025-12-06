package com.example.pandora.manager;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pandora.data.network.RetrofitClient;
import com.example.pandora.databinding.ActivityManagerStatsBinding;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerStatsActivity extends AppCompatActivity {

    private ActivityManagerStatsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManagerStatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadOrderStats();
    }

    private void loadOrderStats() {
        RetrofitClient.getInstance().getApi().getOrderStats().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> data = response.body();

                    double totalRevenue = 0.0;
                    int completedOrders = 0;

                    if (data.get("totalRevenue") instanceof Number) {
                        totalRevenue = ((Number) data.get("totalRevenue")).doubleValue();
                    }
                    if (data.get("completedOrders") instanceof Number) {
                        completedOrders = ((Number) data.get("completedOrders")).intValue();
                    }

                    NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
                    binding.tvRevenue.setText("Tổng doanh thu: " + formatter.format(totalRevenue) + "₫");
                    binding.tvOrders.setText("Số đơn hoàn tất: " + completedOrders);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    binding.tvLastUpdate.setText("Cập nhật: " + sdf.format(new Date()));
                } else {
                    binding.tvRevenue.setText("Không thể tải dữ liệu!");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                binding.tvRevenue.setText("Lỗi kết nối!");
            }
        });
    }
}
