package com.example.pandora.ui.cart;

import com.example.pandora.data.entity.CartItem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CartManager {

    private static final List<CartItem> cartItems = new ArrayList<>();

    // ➕ Thêm sản phẩm vào giỏ
    public static void addToCart(CartItem item) {
        for (CartItem existing : cartItems) {
            if (existing.getProduct().getId().equals(item.getProduct().getId())) {
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                return;
            }
        }
        cartItems.add(item);
    }

    // 📦 Lấy toàn bộ giỏ hàng
    public static List<CartItem> getCartItems() {
        return cartItems;
    }

    // 🗑 Xóa 1 sản phẩm trong giỏ
    public static void removeItem(Long productId) {
        Iterator<CartItem> iterator = cartItems.iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.getProduct().getId().equals(productId)) {
                iterator.remove();
                break;
            }
        }
    }

    // 🧹 Xóa toàn bộ giỏ hàng
    public static void clearCart() {
        cartItems.clear();
    }

    // 🔄 Cập nhật số lượng sản phẩm
    public static void updateQuantity(Long productId, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    // ✔ Đánh dấu sản phẩm để thanh toán
    public static void setItemSelected(Long productId, boolean selected) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(productId)) {
                item.setSelected(selected);
            }
        }
    }

    // ✔ Lấy danh sách sản phẩm được chọn để thanh toán
    public static List<CartItem> getSelectedItems() {
        List<CartItem> selected = new ArrayList<>();
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                selected.add(item);
            }
        }
        return selected;
    }

    // 💰 Tính tổng tiền sản phẩm đã chọn
    public static double getSelectedTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                total += item.getQuantity() * item.getParsedPrice();
            }
        }
        return total;
    }
}
