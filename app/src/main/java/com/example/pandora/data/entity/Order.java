package com.example.pandora.data.entity;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.List;

public class Order {

    private Long id;
    private double totalPrice;
    private double discount;
    private double shippingFee;
    private boolean freeShip;

    private String voucherCode;

    private String status;
    private String paymentMethod;
    private String paymentStatus;

    private String orderDate;
    private String deliveryImageUrl;
    private String cancelReason;

    // ✔ Backend trả về address dạng object
    private Address address;

    // ✔ Backend trả đúng tên field: "items"
    private List<OrderItem> items;

    // ✔ Backend trả về user → Android phải có field
    private User user;


    // ======================
    //       GETTERS
    // ======================

    public Long getId() { return id; }
    public double getTotalPrice() { return totalPrice; }
    public double getDiscount() { return discount; }
    public double getShippingFee() { return shippingFee; }
    public boolean isFreeShip() { return freeShip; }

    public String getVoucherCode() { return voucherCode; }

    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }

    public String getOrderDate() {  // Trả về LocalDateTime
        return orderDate;
    }
    public String getDeliveryImageUrl() { return deliveryImageUrl; }
    public String getCancelReason() { return cancelReason; }

    public Address getAddress() { return address; }

    // ⭐ QUAN TRỌNG: dùng items thay vì orderItems
    public List<OrderItem> getItems() { return items; }

    public User getUser() { return user; }


    // ======================
    //       SETTERS
    // ======================

    // ⭐ Quan trọng cho AdminAdapter khi update trạng thái
    public void setStatus(String status) {
        this.status = status;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setDeliveryImageUrl(String deliveryImageUrl) {
        this.deliveryImageUrl = deliveryImageUrl;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
