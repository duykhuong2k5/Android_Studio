package com.example.pandora.data.entity;

public class FavoriteRequest {

    private Long userId;
    private Long productId;

    public FavoriteRequest(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }

    // Getter & Setter
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
