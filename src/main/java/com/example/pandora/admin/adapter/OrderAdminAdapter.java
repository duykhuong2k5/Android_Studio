package com.example.pandora.admin.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.admin.OrderDetailAdminActivity;
import com.example.pandora.data.entity.Order;
import com.example.pandora.data.network.RetrofitClient;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdminAdapter extends RecyclerView.Adapter<OrderAdminAdapter.ViewHolder> {

    private final Context context;
    private final List<Order> orders;

    public OrderAdminAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderTotal;
        Spinner spnStatus;
        Button btnDelete, btnViewDetail;

        public ViewHolder(View view) {
            super(view);
            tvOrderId = view.findViewById(R.id.tvOrderId);
            tvOrderDate = view.findViewById(R.id.tvOrderDate);
            tvOrderTotal = view.findViewById(R.id.tvOrderTotal);
            spnStatus = view.findViewById(R.id.spnStatus);
            btnDelete = view.findViewById(R.id.btnDelete);
            btnViewDetail = view.findViewById(R.id.btnViewDetail);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvOrderId.setText("Đơn hàng #" + order.getId());
        holder.tvOrderDate.setText("Ngày đặt: " + order.getOrderDate());
        holder.tvOrderTotal.setText("Tổng: " + formatPrice(order.getTotalPrice()));

        String[] statuses = {"PENDING", "WAITING_SHIPPER", "DELIVERING", "COMPLETED", "FAILED"};

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                statuses
        );
        holder.spnStatus.setAdapter(adapterSpinner);

        int index = 0;
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equals(order.getStatus())) {
                index = i;
                break;
            }
        }
        holder.spnStatus.setSelection(index);

        holder.spnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean firstSelect = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (firstSelect) {
                    firstSelect = false;
                    return;
                }

                String selectedStatus = statuses[pos];

// Chỉ cho phép: PENDING -> WAITING_SHIPPER
                if ("PENDING".equals(order.getStatus())
                        && "WAITING_SHIPPER".equals(selectedStatus)) {
                    approveOrder(order, holder, statuses);   // Gọi API /approve
                } else {
                    // reset về trạng thái cũ
                    int currentIndex = 0;
                    for (int i = 0; i < statuses.length; i++) {
                        if (statuses[i].equals(order.getStatus())) {
                            currentIndex = i;
                            break;
                        }
                    }
                    holder.spnStatus.setSelection(currentIndex);
                    Toast.makeText(context,
                            "Admin chỉ được duyệt đơn từ PENDING sang WAITING_SHIPPER.",
                            Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Xóa đơn
        holder.btnDelete.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setTitle("Xóa đơn hàng")
                .setMessage("Bạn có chắc muốn xóa đơn hàng #" + order.getId() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteOrder(order, position))
                .setNegativeButton("Hủy", null)
                .show());

        // Xem chi tiết
        holder.btnViewDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailAdminActivity.class);
            intent.putExtra("order_id", order.getId());
            context.startActivity(intent);
        });
    }

    private void approveOrder(Order order, ViewHolder holder, String[] statuses) {
        RetrofitClient.getInstance()
                .getApi()
                .approveOrder(order.getId())   // @PUT("orders/{id}/approve")
                .enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call,
                                           Response<Map<String, String>> response) {
                        if (response.isSuccessful()) {
                            // cập nhật trạng thái local
                            order.setStatus("WAITING_SHIPPER");
                            notifyDataSetChanged();
                            Toast.makeText(context,
                                    "Đã duyệt đơn, chờ shipper nhận (WAITING_SHIPPER)!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context,
                                    "Không thể duyệt đơn!",
                                    Toast.LENGTH_SHORT).show();
                            rollbackSpinner();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
                        Toast.makeText(context,
                                "Lỗi: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        rollbackSpinner();
                    }

                    private void rollbackSpinner() {
                        int idx = 0;
                        for (int i = 0; i < statuses.length; i++) {
                            if (statuses[i].equals(order.getStatus())) {
                                idx = i;
                                break;
                            }
                        }
                        holder.spnStatus.setSelection(idx);
                    }
                });
    }

    private void deleteOrder(Order order, int position) {
        RetrofitClient.getInstance().getApi().deleteOrder(order.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            orders.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Đã xóa đơn hàng!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Không thể xóa!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String formatPrice(double price) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(price) + "₫";
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}
