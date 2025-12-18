package com.example.pandora.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {

    private Long id;
    private double totalPrice;
    private double discount;
    private double shippingFee;
    private boolean freeShip;
    private String voucherCode;
    private String status;
    private String paymentMethod;
    private String paymentStatus;

    // Chú ý sử dụng @JsonFormat để định dạng ngày tháng nếu sử dụng LocalDateTime
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderDate;

    private String deliveryImageUrl;
    private String cancelReason;

    private AddressDTO address;
    private List<OrderItemDTO> items;

    // Constructor
    public OrderDTO() {}

    public OrderDTO(Long id, double totalPrice, double discount, double shippingFee, boolean freeShip,
                    String voucherCode, String status, String paymentMethod, String paymentStatus,
                    LocalDateTime orderDate, String deliveryImageUrl, String cancelReason,
                    AddressDTO address, List<OrderItemDTO> items) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.discount = discount;
        this.shippingFee = shippingFee;
        this.freeShip = freeShip;
        this.voucherCode = voucherCode;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.orderDate = orderDate;
        this.deliveryImageUrl = deliveryImageUrl;
        this.cancelReason = cancelReason;
        this.address = address;
        this.items = items;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
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

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getDeliveryImageUrl() {
        return deliveryImageUrl;
    }

    public void setDeliveryImageUrl(String deliveryImageUrl) {
        this.deliveryImageUrl = deliveryImageUrl;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }
}
