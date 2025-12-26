package com.example.pandora.data.entity;

public class OrderItemRequest {

    private ProductDTO product;
    private int quantity;
    private double price;

    // Constructor rỗng (bắt buộc)
    public OrderItemRequest() {}

    // Constructor đầy đủ
    public OrderItemRequest(ProductDTO product, int quantity, double price) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
