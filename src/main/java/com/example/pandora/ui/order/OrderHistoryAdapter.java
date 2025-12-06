package com.example.pandora.ui.order;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.Order;
import com.example.pandora.data.entity.OrderItem;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private final Context context;
    private final List<Order> orders;

    public OrderHistoryAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderStatus, tvOrderDate, tvOrderTotal;

        public OrderViewHolder(View view) {
            super(view);
            tvOrderId = view.findViewById(R.id.tvOrderId);
            tvOrderStatus = view.findViewById(R.id.tvOrderStatus);
            tvOrderDate = view.findViewById(R.id.tvOrderDate);
            tvOrderTotal = view.findViewById(R.id.tvOrderTotal);
        }
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvOrderId.setText("Mã đơn hàng: #" + order.getId());
        holder.tvOrderStatus.setText(order.getStatus());
        holder.tvOrderDate.setText("Ngày đặt: " + formatDate(order.getOrderDate()));
        holder.tvOrderTotal.setText("Tổng tiền: " + formatPrice(order.getTotalPrice()));

        // Khi bấm → mở chi tiết đơn hàng
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("orderId", order.getId());
            intent.putExtra("status", order.getStatus());
            intent.putExtra("total", order.getTotalPrice());
            intent.putExtra("date", order.getOrderDate());

            ArrayList<OrderItem> items = new ArrayList<>();
            if (order.getItems() != null) {
                items.addAll(order.getItems());
            }


            intent.putParcelableArrayListExtra("items", items);


            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private String formatPrice(double value) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(value) + "₫";
    }

    private String formatDate(String dateString) {
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date date = input.parse(dateString);
            return output.format(date);
        } catch (Exception e) {
            return dateString;
        }
    }
}
