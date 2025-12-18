package com.example.pandora.dto;

import com.example.pandora.model.Favorite;

public class FavoriteDTO {

    private Long id;
    private Long productId;
    private String productName;
    private String imageUrl;
    private double priceNew;
    private double priceOld;
    private String discountPercent;
    private String category;

    // ✅ Constructor chuyển từ entity Favorite sang DTO
    public FavoriteDTO(Favorite favorite) {
        if (favorite.getProduct() != null) {
            this.id = favorite.getId();
            this.productId = favorite.getProduct().getId();
            this.productName = favorite.getProduct().getName();
            this.imageUrl = favorite.getProduct().getImageUrl();
            this.priceNew = favorite.getProduct().getPriceNew();
            this.priceOld = favorite.getProduct().getPriceOld();
            this.discountPercent = favorite.getProduct().getDiscountPercent();
            this.category = favorite.getProduct().getCategory();
        }
    }

    // ✅ Getters
    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getImageUrl() { return imageUrl; }
    public double getPriceNew() { return priceNew; }
    public double getPriceOld() { return priceOld; }
    public String getDiscountPercent() { return discountPercent; }
    public String getCategory() { return category; }
}
