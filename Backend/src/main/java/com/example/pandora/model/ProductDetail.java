package com.example.pandora.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
@Table(name = "product_details")
public class ProductDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference(value = "product-detail")
    private Product product;

    @Column(columnDefinition = "TEXT")
    private String description; // Chi tiết sản phẩm

    @Column(columnDefinition = "TEXT")
    private String shippingPolicy =
            "Pandora miễn phí vận chuyển toàn quốc cho mọi đơn hàng. "
          + "Áp dụng trên Website & Fanpage chính thức của Pandora Việt Nam."; // ✅ Chính sách mặc định

    @Column(columnDefinition = "TEXT")
    private String compatibility; // Tính tương thích (mô tả text)

    // ✅ Thêm hình ảnh minh họa cho phần "Tính tương thích"
    @Column(length = 500)
    private String compatibilityImageUrl; // Link ảnh minh họa tương thích

    public ProductDetail() {}

    // 🔹 Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getShippingPolicy() {
        // Trả về chính sách mặc định nếu database chưa có
        if (shippingPolicy == null || shippingPolicy.trim().isEmpty()) {
            return "Pandora miễn phí vận chuyển toàn quốc cho mọi đơn hàng. "
                 + "Áp dụng trên Website & Fanpage chính thức của Pandora Việt Nam.";
        }
        return shippingPolicy;
    }
    public void setShippingPolicy(String shippingPolicy) { this.shippingPolicy = shippingPolicy; }

    public String getCompatibility() { return compatibility; }
    public void setCompatibility(String compatibility) { this.compatibility = compatibility; }

    public String getCompatibilityImageUrl() { return compatibilityImageUrl; }
    public void setCompatibilityImageUrl(String compatibilityImageUrl) { this.compatibilityImageUrl = compatibilityImageUrl; }
}
