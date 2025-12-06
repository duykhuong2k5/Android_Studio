package com.example.pandora.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pandora.R;
import com.example.pandora.data.entity.CartItem;
import com.example.pandora.ui.cart.CartManager;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<CartItem> cartItems;
    private final Runnable onCartChange;

    public CartAdapter(List<CartItem> cartItems, Runnable onCartChange) {
        this.cartItems = cartItems;
        this.onCartChange = onCartChange;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkSelect;
        ImageView imgProduct;
        TextView tvName, tvPrice, tvQuantity;
        Button btnPlus, btnMinus;
        ImageButton btnRemove;

        public CartViewHolder(View view) {
            super(view);
            checkSelect = view.findViewById(R.id.checkSelect);
            imgProduct = view.findViewById(R.id.imgProduct);
            tvName = view.findViewById(R.id.tvName);
            tvPrice = view.findViewById(R.id.tvPrice);
            tvQuantity = view.findViewById(R.id.tvQuantity);
            btnPlus = view.findViewById(R.id.btnPlus);
            btnMinus = view.findViewById(R.id.btnMinus);
            btnRemove = view.findViewById(R.id.btnRemove);
        }
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        Glide.with(holder.itemView.getContext())
                .load(item.getProduct().getImageUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_image)
                .into(holder.imgProduct);

        holder.tvName.setText(item.getProduct().getName());
        holder.tvPrice.setText(formatPrice(item.getParsedPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        // ------------ CHECKBOX SELECT ------------
        holder.checkSelect.setOnCheckedChangeListener(null);
        holder.checkSelect.setChecked(item.isSelected());
        holder.checkSelect.setOnCheckedChangeListener((btn, isChecked) -> {
            item.setSelected(isChecked);
            CartManager.setItemSelected(item.getProduct().getId(), isChecked);
            onCartChange.run();
        });

        // ------------ BTN + ------------
        holder.btnPlus.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            CartManager.updateQuantity(item.getProduct().getId(), item.getQuantity());

            notifyItemChanged(position);
            onCartChange.run();
        });

        // ------------ BTN - ------------
        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                CartManager.updateQuantity(item.getProduct().getId(), item.getQuantity());

                notifyItemChanged(position);
                onCartChange.run();
            }
        });

        // ------------ BTN REMOVE ------------
        holder.btnRemove.setOnClickListener(v -> {
            CartManager.removeItem(item.getProduct().getId());
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
            onCartChange.run();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    private String formatPrice(double price) {
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return nf.format(price) + "₫";
    }
}
