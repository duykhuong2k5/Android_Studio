package com.example.pandora.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private double priceNew;
    private double priceOld;

    @Column(length = 10)
    private String discountPercent;

    @Column(length = 255)
    private String imageUrl;

    private String category;
    // 🔹 1 sản phẩm có 1 chi tiết
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "product-detail")
    private ProductDetail productDetail;

    // 🔹 1 sản phẩm có nhiều đánh giá
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "product-reviews")
    
    private List<Review> reviews = new ArrayList<>();
    
 // 🔹 1 sản phẩm có nhiều ảnh phụ
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference(value = "product-images")
    private List<ProductImage> images = new ArrayList<>();
    
    @Column(name = "is_combo")
    private Boolean isCombo;

    @Column(name = "promo_rule", length = 255)
    private String promoRule;
 // 🔹 1 sản phẩm có nhiều size
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference(value = "product-sizes")
    private List<ProductSize> sizes = new ArrayList<>();

    // ✅ Constructor mặc định (bắt buộc cho JPA)
    
    public Product() {
    }

    // ✅ Constructor có tham số
    public Product(String name, double priceNew, double priceOld, String discountPercent, String imageUrl, String category) {
        this.name = name;
        this.priceNew = priceNew;
        this.priceOld = priceOld;
        this.discountPercent = discountPercent;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    // ✅ Getter & Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPriceNew() {
        return priceNew;
    }

    public void setPriceNew(double priceNew) {
        this.priceNew = priceNew;
    }

    public double getPriceOld() {
        return priceOld;
    }

    public void setPriceOld(double priceOld) {
        this.priceOld = priceOld;
    }

    public String getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(String discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public ProductDetail getProductDetail() { return productDetail; }
    public void setProductDetail(ProductDetail productDetail) { this.productDetail = productDetail; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
    public Boolean getIsCombo() {
        return isCombo;
    }

    public void setIsCombo(Boolean isCombo) {
        this.isCombo = isCombo;
    }

    public String getPromoRule() {
        return promoRule;
    }

    public void setPromoRule(String promoRule) {
        this.promoRule = promoRule;
    }
    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    // tiện: thêm / xóa ảnh trong list
    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }

    public void removeImage(ProductImage image) {
        images.remove(image);
        image.setProduct(null);
    }
    public List<ProductSize> getSizes() {
        return sizes;
    }

    public void setSizes(List<ProductSize> sizes) {
        this.sizes = sizes;
    }

    public void addSize(ProductSize size) {
        sizes.add(size);
        size.setProduct(this);
    }

    public void removeSize(ProductSize size) {
        sizes.remove(size);
        size.setProduct(null);
    }
}
