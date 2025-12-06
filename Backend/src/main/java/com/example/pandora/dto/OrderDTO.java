package com.example.pandora.dto;

import com.example.pandora.model.Order;

import java.util.List;
import java.util.stream.Collectors;

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

    private String orderDate;
    private String deliveryImageUrl;

    private String cancelReason;
    private String failReason;

    private AddressDTO address;

    private List<OrderItemDTO> items;

    // Constructor
    public OrderDTO(Order order) {

        this.id = order.getId();
        this.totalPrice = order.getTotalPrice();

        this.discount = order.getDiscount();
        this.shippingFee = order.getShippingFee();
        this.freeShip = order.isFreeShip();

        this.voucherCode = order.getVoucherCode();

        this.status = order.getStatus();
        this.paymentMethod = order.getPaymentMethod();
        this.paymentStatus = order.getPaymentStatus();

        this.orderDate = order.getOrderDate().toString();
        this.deliveryImageUrl = order.getDeliveryImageUrl();

        this.cancelReason = order.getCancelReason();
        this.failReason = order.getFailReason();

        // Address
        if (order.getAddress() != null) {
            this.address = new AddressDTO(order.getAddress());
        }

        // Order items
        this.items = order.getOrderItems().stream()
                .map(OrderItemDTO::new)
                .collect(Collectors.toList());
    }

    // GETTERS
    public Long getId() { return id; }
    public double getTotalPrice() { return totalPrice; }
    public double getDiscount() { return discount; }
    public double getShippingFee() { return shippingFee; }
    public boolean isFreeShip() { return freeShip; }

    public String getVoucherCode() { return voucherCode; }

    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }

    public String getOrderDate() { return orderDate; }
    public String getDeliveryImageUrl() { return deliveryImageUrl; }

    public String getCancelReason() { return cancelReason; }
    public String getFailReason() { return failReason; }

    public AddressDTO getAddress() { return address; }

    public List<OrderItemDTO> getItems() { return items; }
}
