package com.example.pandora.data.entity;

import java.util.List;

public class OrderRequest {

    private UserDTO user;
    private double totalPrice;
    private String status;
    private List<OrderItemRequest> orderItems;
    private AddressDTO address;
    private double discount;      // số tiền giảm (vd 100000)
    private double shippingFee;   // phí vận chuyển (mặc định 30000, hoặc 0 nếu freeship)
    private boolean freeShip;     // true nếu dùng voucher miễn phí vận chuyển
    public OrderRequest() {}

    public OrderRequest(UserDTO user, double totalPrice, String status, List<OrderItemRequest> orderItems) {
        this.user = user;
        this.totalPrice = totalPrice;
        this.status = status;
        this.orderItems = orderItems;
    }

    // GETTER & SETTER đầy đủ để Retrofit serialize đúng
    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItemRequest> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemRequest> orderItems) {
        this.orderItems = orderItems;
    }
    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }
    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public boolean isFreeShip() {
        return freeShip;
    }

    public void setFreeShip(boolean freeShip) {
        this.freeShip = freeShip;
    }
}
