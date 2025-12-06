package com.example.pandora.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pandora.R;
import com.example.pandora.data.entity.RevenueSummaryResponse;
import com.example.pandora.data.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageRevenueActivity extends AppCompatActivity {

    private TextView tvRevenue, tvOrdersCount;
    private Spinner spinnerYear, spinnerMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_revenue);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Init views
        tvRevenue = findViewById(R.id.tvRevenue);
        tvOrdersCount = findViewById(R.id.tvOrdersCount);
        spinnerYear = findViewById(R.id.spinnerYear);
        spinnerMonth = findViewById(R.id.spinnerMonth);

        // Spinner year
        List<String> years = new ArrayList<>();
        for (int i = 2020; i <= 2025; i++) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        // Spinner month
        List<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(String.format("%02d", i));
        }
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        // Listener: đổi năm / tháng là reload
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                loadRevenueData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                loadRevenueData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });

        // Load lần đầu
        loadRevenueData();
    }

    private void loadRevenueData() {
        if (spinnerYear.getSelectedItem() == null ||
                spinnerMonth.getSelectedItem() == null) {
            return;
        }

        String selectedYear = spinnerYear.getSelectedItem().toString();
        String selectedMonth = spinnerMonth.getSelectedItem().toString();

        RetrofitClient.getInstance().getApi()
                .getRevenueSummary(selectedYear, selectedMonth)
                .enqueue(new Callback<RevenueSummaryResponse>() {
                    @Override
                    public void onResponse(Call<RevenueSummaryResponse> call,
                                           Response<RevenueSummaryResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            RevenueSummaryResponse summary = response.body();

                            // ✅ Chỉ tính đơn COMPLETED (backend đã filter)
                            double totalRevenue = summary.getTotalRevenue();
                            long totalOrders = summary.getCompletedOrders();

                            tvRevenue.setText("Doanh thu tháng: " + totalRevenue + " VNĐ");
                            tvOrdersCount.setText("Số đơn hoàn thành: " + totalOrders);
                        } else {
                            Toast.makeText(ManageRevenueActivity.this,
                                    "Không thể tải dữ liệu doanh thu!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RevenueSummaryResponse> call, Throwable t) {
                        Toast.makeText(ManageRevenueActivity.this,
                                "Lỗi: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
