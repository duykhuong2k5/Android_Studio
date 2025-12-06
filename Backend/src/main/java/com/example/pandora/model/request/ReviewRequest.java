package com.example.pandora.model.request;

public class ReviewRequest {

    private Long userId;
    private double rating;
    private String comment;
    private String imageUrl;

    public ReviewRequest() {}

    public ReviewRequest(Long userId, double rating, String comment, String imageUrl) {
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.imageUrl = imageUrl;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
