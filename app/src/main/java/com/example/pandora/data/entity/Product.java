package com.example.pandora.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    private Long id;
    private String name;
    private String priceNew;
    private String priceOld;
    private String discountPercent;
    private String imageUrl;
    private String category;

    public Product() {}

    public Product(Long id, String name, String priceNew, String priceOld,
                   String discountPercent, String imageUrl, String category) {
        this.id = id;
        this.name = name;
        this.priceNew = priceNew;
        this.priceOld = priceOld;
        this.discountPercent = discountPercent;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    protected Product(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        name = in.readString();
        priceNew = in.readString();
        priceOld = in.readString();
        discountPercent = in.readString();
        imageUrl = in.readString();
        category = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    // ======================= GETTER & SETTER ==========================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPriceNew() { return priceNew; }
    public void setPriceNew(String priceNew) { this.priceNew = priceNew; }

    public String getPriceOld() { return priceOld; }
    public void setPriceOld(String priceOld) { this.priceOld = priceOld; }

    public String getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(String discountPercent) { this.discountPercent = discountPercent; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    // ======================= PARSE PRICE ==============================
    /**
     * Chuyển "125.000₫" → 125000
     */
    public double getParsedPrice() {
        try {
            if (priceNew == null) return 0;

            // TRƯỜNG HỢP 1: API trả về DOUBLE dạng chuỗi "2590000.0"
            if (priceNew.matches("^\\d+(\\.\\d+)?$")) {
                return Double.parseDouble(priceNew);
            }

            // TRƯỜNG HỢP 2: Giá có dạng "2.590.000₫" -> xóa ký tự ngoài số
            String cleaned = priceNew.replaceAll("[^0-9]", "");

            if (cleaned.isEmpty()) return 0;

            return Double.parseDouble(cleaned);

        } catch (Exception e) {
            return 0;
        }
    }


    // =================================================================

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(name);
        dest.writeString(priceNew);
        dest.writeString(priceOld);
        dest.writeString(discountPercent);
        dest.writeString(imageUrl);
        dest.writeString(category);
    }
}
