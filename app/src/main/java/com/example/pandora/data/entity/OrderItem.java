package com.example.pandora.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderItem implements Parcelable {

    private Long id;

    private Long productId;
    private String productName;
    private String imageUrl;

    private Product product;  // giữ lại nếu nơi khác dùng
    private int quantity;
    private double price;

    public OrderItem() {}

    protected OrderItem(Parcel in) {
        id = in.readLong();
        productId = in.readLong();
        productName = in.readString();
        imageUrl = in.readString();
        product = in.readParcelable(Product.class.getClassLoader());
        quantity = in.readInt();
        price = in.readDouble();
    }

    public static final Creator<OrderItem> CREATOR = new Creator<>() {
        @Override
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };

    // ====== GETTERS ======
    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getImageUrl() { return imageUrl; }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }

    // ====== SETTERS (QUAN TRỌNG CHO RETROFIT) ======
    public void setId(Long id) { this.id = id; }
    public void setProductId(Long productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setProduct(Product product) { this.product = product; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }

    // ===== PARCELABLE =====
    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id != null ? id : -1L);
        dest.writeLong(productId != null ? productId : -1L);
        dest.writeString(productName);
        dest.writeString(imageUrl);
        dest.writeParcelable(product, flags);
        dest.writeInt(quantity);
        dest.writeDouble(price);
    }
}
