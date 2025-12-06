package com.example.pandora.shipper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pandora.R;
import com.example.pandora.data.network.RetrofitClient;
import com.example.pandora.ui.profile.ProfileActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipperDashboardActivity extends AppCompatActivity {

    private TextView tvDeliveredCount, tvSuccessRate, tvSuccessChange,
            tvTotalRevenue, tvThisMonthRevenue;
    private ProgressBar progressSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_dashboard);

        // Ánh xạ view
        tvDeliveredCount = findViewById(R.id.tvDeliveredCount);
        tvSuccessRate = findViewById(R.id.tvSuccessRate);
        tvSuccessChange = findViewById(R.id.tvSuccessChange);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvThisMonthRevenue = findViewById(R.id.tvThisMonthRevenue);
        progressSuccess = findViewById(R.id.progressSuccess);

        MaterialButton btnViewDelivering = findViewById(R.id.btnViewDelivering);
        MaterialButton btnProfile = findViewById(R.id.btnProfile);
        MaterialCardView cardHistory = findViewById(R.id.cardHistory);

        // Xem các đơn đang ở trạng thái DELIVERING
        btnViewDelivering.setOnClickListener(v -> {
            Intent i = new Intent(this, ShipperOrdersActivity.class);
            i.putExtra("filter_status", "DELIVERING");
            startActivity(i);
        });

        // Lịch sử giao hàng
        cardHistory.setOnClickListener(v ->
                startActivity(new Intent(this, ShipperHistoryActivity.class)));

        // Profile shipper
        btnProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        // Gọi API để load số liệu
        loadStatsFromApi();
    }

    private void loadStatsFromApi() {
        // TODO: Lấy đúng shipperId đang đăng nhập (SharedPreferences / Intent / token…)
        Long shipperId = 1L;

        // Dùng endpoint đã có: /orders/stats/{userId}
        RetrofitClient.getInstance()
                .getApi()
                .getUserStats(shipperId)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call,
                                           Response<Map<String, Object>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            applyDemoData(); // fallback demo
                            return;
                        }

                        Map<String, Object> data = response.body();

                        // 👉 BACKEND NÊN TRẢ VỀ CÁC KEY:
                        //  deliveredCount    : long / int
                        //  successRate       : double (0-100)
                        //  successChange     : double (±%)
                        //  totalRevenue      : long
                        //  thisMonthRevenue  : long
                        int delivered = getInt(data.get("deliveredCount"));
                        double successRate = getDouble(data.get("successRate"));
                        double successChange = getDouble(data.get("successChange"));
                        long totalRevenue = getLong(data.get("totalRevenue"));
                        long thisMonthRevenue = getLong(data.get("thisMonthRevenue"));

                        bindStats(delivered, successRate, successChange,
                                totalRevenue, thisMonthRevenue);
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        // Thất bại thì dùng dữ liệu demo cho đỡ trống
                        applyDemoData();
                    }
                });
    }

    /** Dữ liệu demo nếu API chưa sẵn / lỗi */
    private void applyDemoData() {
        int delivered = 1284;
        double successRate = 98.7;
        double successChange = 1.2;
        long totalRevenue = 14_350_800L;
        long thisMonthRevenue = 2_150_250L;

        bindStats(delivered, successRate, successChange, totalRevenue, thisMonthRevenue);
    }

    /** Gán số liệu lên UI */
    private void bindStats(int delivered,
                           double successRate,
                           double successChange,
                           long totalRevenue,
                           long thisMonthRevenue) {

        tvDeliveredCount.setText(String.valueOf(delivered));
        tvSuccessRate.setText(String.format(Locale.getDefault(), "%.1f%%", successRate));

        // hiển thị + / - thay màu tuỳ bạn
        String changeText = (successChange >= 0 ? "+" : "") +
                String.format(Locale.getDefault(), "%.1f%%", successChange);
        tvSuccessChange.setText(changeText);

        progressSuccess.setProgress((int) Math.round(successRate));

        tvTotalRevenue.setText(formatMoney(totalRevenue));
        tvThisMonthRevenue.setText("This month: " + formatMoney(thisMonthRevenue));
    }

    private String formatMoney(long value) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(value) + " ₫";
    }

    // ===== Helpers ép kiểu an toàn từ Map =====
    private int getInt(Object o) {
        if (o instanceof Number) return ((Number) o).intValue();
        return 0;
    }

    private long getLong(Object o) {
        if (o instanceof Number) return ((Number) o).longValue();
        return 0L;
    }

    private double getDouble(Object o) {
        if (o instanceof Number) return ((Number) o).doubleValue();
        return 0.0;
    }
}
