package com.example.pandora.shipper;

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

public class ShipperOrderItemAdapter extends RecyclerView.Adapter<ShipperOrderItemAdapter.ViewHolder> {

    private final Context context;
    private final List<OrderItem> items;

    public ShipperOrderItemAdapter(Context context, List<OrderItem> items) {
        this.context = context;
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvPrice, tvQty;

        public ViewHolder(View view) {
            super(view);
            imgProduct = view.findViewById(R.id.imgItem);
            tvProductName = view.findViewById(R.id.tvItemName);
            tvPrice = view.findViewById(R.id.tvItemPrice);
            tvQty = view.findViewById(R.id.tvItemQty);
        }
    }

    @Override
    public ShipperOrderItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shipper_order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShipperOrderItemAdapter.ViewHolder holder, int position) {
        OrderItem item = items.get(position);

        // 🔥 Đọc từ product
        if (item.getProduct() != null) {
            holder.tvProductName.setText(item.getProduct().getName());

            Glide.with(context)
                    .load(item.getProduct().getImageUrl())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imgProduct);
        } else {
            holder.tvProductName.setText("Không rõ sản phẩm");
        }

        holder.tvQty.setText("x" + item.getQuantity());
        holder.tvPrice.setText(formatPrice(item.getPrice()));
    }

    @Override
    public int getItemCount() { return items.size(); }

    private String formatPrice(double v) {
        NumberFormat fmt = NumberFormat.getInstance(new Locale("vi", "VN"));
        return fmt.format(v) + "₫";
    }
}
