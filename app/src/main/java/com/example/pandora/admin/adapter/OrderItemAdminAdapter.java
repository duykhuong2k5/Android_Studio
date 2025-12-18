package com.example.pandora.admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.OrderItem;
import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderItemAdminAdapter extends RecyclerView.Adapter<OrderItemAdminAdapter.ViewHolder> {

    private final Context context;
    private final List<OrderItem> items;

    public OrderItemAdminAdapter(Context context, List<OrderItem> items) {
        this.context = context;
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvProductPrice, tvQuantity;

        public ViewHolder(View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_order_detail_admin, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OrderItem item = items.get(position);

        holder.tvProductName.setText(item.getProductName());
        holder.tvProductPrice.setText(formatPrice(item.getPrice()));
        holder.tvQuantity.setText("x" + item.getQuantity());

        String imageUrl = item.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    private String formatPrice(double price) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(price) + "đ";
    }
}
