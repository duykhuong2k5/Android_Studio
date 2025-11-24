package com.example.pandora.dto;

import com.example.pandora.model.Order;

import java.util.List;
import java.util.stream.Collectors;

public class OrderDTO {

    private Long id;
    private double totalPrice;
    private String status;
    private String paymentMethod;    // COD / VNPAY
    private String paymentStatus;    // UNPAID / PENDING / PAID / FAILED
    private String orderDate;
    private List<OrderItemDTO> items;

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.totalPrice = order.getTotalPrice();
        this.status = order.getStatus();
        this.paymentMethod = order.getPaymentMethod();    // 🔥 thêm vào
        this.paymentStatus = order.getPaymentStatus();    // 🔥 thêm vào
        this.orderDate = order.getOrderDate().toString();

        this.items = order.getOrderItems().stream()
                .map(OrderItemDTO::new)
                .collect(Collectors.toList());
    }

    // Getters
    public Long getId() { return id; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getOrderDate() { return orderDate; }
    public List<OrderItemDTO> getItems() { return items; }
}
