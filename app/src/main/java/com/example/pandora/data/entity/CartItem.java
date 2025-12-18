package com.example.pandora.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class CartItem implements Parcelable {

    private Product product;
    private int quantity;
    private boolean isSelected;
    private String sizeLabel;
    public CartItem() {}

    public CartItem(Product product, int quantity, boolean isSelected) {
        this.product = product;
        this.quantity = quantity;
        this.isSelected = isSelected;
    }
    public CartItem(Product product, int quantity, boolean isSelected, String sizeLabel) {
        this.product = product;
        this.quantity = quantity;
        this.isSelected = isSelected;
        this.sizeLabel = sizeLabel;
    }

    public CartItem(Product product) {
        this(product, 1, false);
    }

    protected CartItem(Parcel in) {
        product = in.readParcelable(Product.class.getClassLoader());
        quantity = in.readInt();
        isSelected = in.readByte() != 0;
        sizeLabel = in.readString();
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    // ======================= GETTER & SETTER =========================

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
    public String getSizeLabel() {
        return sizeLabel;
    }

    public void setSizeLabel(String sizeLabel) {
        this.sizeLabel = sizeLabel;
    }

    // ======================= PRICE PARSER =============================

    /**
     * Trả về giá dạng số (double) từ String "125.000₫" → 125000
     */
    public double getParsedPrice() {
        try {
            if (product.getPriceNew() == null) return 0;

            String cleaned = product.getPriceNew()
                    .replaceAll("[^0-9]", "");  // bỏ dấu chấm ₫ , v.v

            if (cleaned.isEmpty()) return 0;

            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            return 0;
        }
    }

    // =================================================================

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(product, flags);
        dest.writeInt(quantity);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeString(sizeLabel);
    }
}
