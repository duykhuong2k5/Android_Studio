package com.example.pandora.ui.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pandora.R;
import com.example.pandora.data.entity.OrderItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private final Context context;
    private final List<OrderItem> items;

    public OrderItemAdapter(Context context, List<OrderItem> items) {
        this.context = context;
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvPrice, tvQuantity;

        public ViewHolder(View view) {
            super(view);
            imgProduct = view.findViewById(R.id.imgProduct);
            tvProductName = view.findViewById(R.id.tvProductName);
            tvPrice = view.findViewById(R.id.tvPrice);
            tvQuantity = view.findViewById(R.id.tvQuantity);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_order_detail_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OrderItem item = items.get(position);

        // 🔥 LẤY TÊN & ẢNH TỪ PRODUCT
        if (item.getProduct() != null) {
            holder.tvProductName.setText(item.getProduct().getName());

            Glide.with(context)
                    .load(item.getProduct().getImageUrl())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imgProduct);
        } else {
            holder.tvProductName.setText("Sản phẩm không xác định");
            holder.imgProduct.setImageResource(R.drawable.placeholder);
        }

        holder.tvQuantity.setText("x" + item.getQuantity());
        holder.tvPrice.setText(formatPrice(item.getPrice()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatPrice(double value) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(value) + "₫";
    }
}
