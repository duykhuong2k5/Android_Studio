package com.example.pandora.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Tên người viết đánh giá
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;



    // 🔹 Điểm đánh giá (1–5)
    @Column(nullable = false)
    private double rating;

    // 🔹 Nội dung bình luận
    @Column(columnDefinition = "TEXT")
    private String comment;

    // 🔹 Ảnh minh họa (ví dụ ảnh sản phẩm thực tế)
    @Column(length = 500)
    private String imageUrl;

    // 🔹 Thời gian tạo
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // 🔹 Quan hệ N:1 với Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference(value = "product-reviews")
    private Product product;

    // ✅ Constructor mặc định (JPA yêu cầu)
    public Review() {}

    public Review(User user, double rating, String comment, String imageUrl, Product product) {
        this.user = user;                     // lưu đối tượng User
        this.rating = rating;
        this.comment = comment;
        this.imageUrl = imageUrl;
        this.product = product;
        this.createdAt = LocalDateTime.now();
    }


    // ✅ Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    @Transient
    public String getUsername() {
        return user != null ? user.getFullName() : "Người dùng";
    }

}
