package com.example.pandora.data.entity;

public class ReviewRequest {
    private Long userId;
    private double rating;
    private String comment;
    private String imageUrl;

    public ReviewRequest(Long userId, double rating, String comment, String imageUrl) {
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.imageUrl = imageUrl;
    }

    public Long getUserId() { return userId; }
    public double getRating() { return rating; }
    public String getComment() { return comment; }
    public String getImageUrl() { return imageUrl; }
}
