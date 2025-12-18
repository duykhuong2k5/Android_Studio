// src/main/java/com/example/pandora/dto/ReviewAdminDto.java
package com.example.pandora.dto;

public class ReviewAdminDto {

    private Long id;
    private String username;
    private double rating;
    private String comment;
    private String createdAt;
    private String productName;

    public ReviewAdminDto(Long id,
                          String username,
                          double rating,
                          String comment,
                          String createdAt,
                          String productName) {
        this.id = id;
        this.username = username;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
        this.productName = productName;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public double getRating() { return rating; }
    public String getComment() { return comment; }
    public String getCreatedAt() { return createdAt; }
    public String getProductName() { return productName; }
}
