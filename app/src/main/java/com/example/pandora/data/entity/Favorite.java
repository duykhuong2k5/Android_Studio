package com.example.pandora.data.entity;

public class Favorite {
    private Long id;
    private Long productId;
    private String productName;
    private String imageUrl;
    private double priceNew;
    private double priceOld;
    private String discountPercent;
    private String category;

    public Favorite(Long id, Long productId, String productName, String imageUrl,
                    double priceNew, double priceOld, String discountPercent, String category) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.priceNew = priceNew;
        this.priceOld = priceOld;
        this.discountPercent = discountPercent;
        this.category = category;
    }

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getPriceNew() { return priceNew; }
    public void setPriceNew(double priceNew) { this.priceNew = priceNew; }

    public double getPriceOld() { return priceOld; }
    public void setPriceOld(double priceOld) { this.priceOld = priceOld; }

    public String getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(String discountPercent) { this.discountPercent = discountPercent; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
