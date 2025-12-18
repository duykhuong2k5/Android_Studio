package com.example.pandora.data.entity;

public class ProductDetail {
    private Long id;
    private String description;
    private String shippingPolicy;
    private String compatibility;
    private String compatibilityImageUrl;

    public ProductDetail(Long id, String description, String shippingPolicy, String compatibility, String compatibilityImageUrl) {
        this.id = id;
        this.description = description;
        this.shippingPolicy = shippingPolicy;
        this.compatibility = compatibility;
        this.compatibilityImageUrl = compatibilityImageUrl;
    }

    // Getters
    public Long getId() { return id; }
    public String getDescription() { return description; }
    public String getShippingPolicy() { return shippingPolicy; }
    public String getCompatibility() { return compatibility; }
    public String getCompatibilityImageUrl() { return compatibilityImageUrl; }
}
