package com.example.pandora.data.entity;

public class Review {

    private Long id;
    private Long productId;
    private double rating;         // backend trả double
    private String comment;
    private String imageUrl;
    private String createdAt;
    private String username;       // ⭐ backend trả trực tiếp
    private String productName;

    public Review() {}

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public double getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUsername() {
        return username != null ? username : "Người dùng";
    }
    public String getProductName() { return productName; }
}
