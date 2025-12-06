package com.example.pandora.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "product_sizes")
public class ProductSize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // "one size", "16", "17", "18"...
    @Column(name = "size_label", length = 50, nullable = false)
    private String sizeLabel;

    // tồn kho cho size này (nếu chưa cần thì để nullable)
    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonBackReference(value = "product-sizes")
    private Product product;

    public ProductSize() {}

    public ProductSize(String sizeLabel, Integer stock, Product product) {
        this.sizeLabel = sizeLabel;
        this.stock = stock;
        this.product = product;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSizeLabel() { return sizeLabel; }
    public void setSizeLabel(String sizeLabel) { this.sizeLabel = sizeLabel; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
