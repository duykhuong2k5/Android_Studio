package com.example.pandora.shipper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.Order;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ShipperOrderAdapter extends RecyclerView.Adapter<ShipperOrderAdapter.ViewHolder> {

    public interface OnOrderActionListener {
        // Xem chi tiết (chỉ cho DELIVERING)
        void onViewDetail(Order order);

        // Nhận đơn (WAITING_SHIPPER → Activity gọi API shipperAccept, rồi mở detail)
        void onAcceptOrder(Order order);
    }

    private final Context context;
    private final List<Order> orders;
    private final OnOrderActionListener listener;

    public ShipperOrderAdapter(Context context, List<Order> orders, OnOrderActionListener listener) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvAddress, tvTotal, tvStatus;
        MaterialButton btnAccept;

        public ViewHolder(View view) {
            super(view);
            tvOrderId = view.findViewById(R.id.tvShipperOrderId);
            tvAddress = view.findViewById(R.id.tvShipperAddress);
            tvTotal = view.findViewById(R.id.tvShipperTotal);
            tvStatus = view.findViewById(R.id.tvShipperStatus);
            btnAccept = view.findViewById(R.id.btnAcceptOrder);
        }
    }

    @Override
    public ShipperOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shipper_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShipperOrderAdapter.ViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvOrderId.setText("Đơn #" + order.getId());
        holder.tvStatus.setText(order.getStatus());
        holder.tvTotal.setText("Tổng: " + formatPrice(order.getTotalPrice()));

        // LẤY ĐỊA CHỈ TỪ ORDER.ADDRESS
        String address;
        if (order.getAddress() != null &&
                order.getAddress().getFullAddress() != null &&
                !order.getAddress().getFullAddress().isEmpty()) {
            address = order.getAddress().getFullAddress();
        } else {
            address = "Không rõ địa chỉ";
        }
        holder.tvAddress.setText(address);

        boolean isWaiting = "WAITING_SHIPPER".equals(order.getStatus());
        boolean isDelivering = "DELIVERING".equals(order.getStatus());

        // Chỉ hiển thị nút Nhận đơn nếu WAITING_SHIPPER
        holder.btnAccept.setVisibility(isWaiting ? View.VISIBLE : View.GONE);

        // Click vào card:
        holder.itemView.setOnClickListener(v -> {
            if (isDelivering) {
                // Đơn đang giao → cho xem chi tiết luôn
                if (listener != null) listener.onViewDetail(order);
            } else if (isWaiting) {
                // Đơn đang chờ nhận → phải bấm Nhận đơn trước
                Toast.makeText(context,
                        "Hãy bấm 'Nhận đơn' để nhận và xem chi tiết.",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Trạng thái khác nếu có
                Toast.makeText(context,
                        "Đơn ở trạng thái " + order.getStatus(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Nhấn nút Nhận đơn
        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAcceptOrder(order);
            }
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
}
