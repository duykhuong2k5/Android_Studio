package com.example.pandora.dto;

import com.example.pandora.model.OrderItem;

public class OrderItemDTO {

    private Long productId;
    private String productName;
    private String imageUrl;
    private double price;
    private int quantity;

    public OrderItemDTO(OrderItem item) {
        this.productId = item.getProduct().getId();
        this.productName = item.getProduct().getName();
        this.imageUrl = item.getProduct().getImageUrl();
        this.price = item.getPrice();
        this.quantity = item.getQuantity();
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}
