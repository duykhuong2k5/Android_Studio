package com.example.pandora.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Người đặt hàng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"orders", "favorites"})
    private User user;

    // 💰 Tổng giá trị đơn
    @Column(nullable = false)
    private double totalPrice;

    // 📦 Trạng thái đơn hàng
    @Column(nullable = false)
    private String status = "PENDING"; 
    // PENDING / CUSTOMER_PAID / WAITING_SHIPPER / DELIVERING / COMPLETED / FAILED / PAYMENT_FAILED

    // 💳 Phương thức thanh toán: COD / VNPAY
    @Column(nullable = false)
    private String paymentMethod = "COD";

    // 💵 Trạng thái thanh toán:
    @Column(nullable = false)
    private String paymentStatus = "UNPAID";
    // UNPAID / PENDING / PAID / FAILED

    // 🕒 Ngày tạo đơn
    @Column(nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    // 📦 Danh sách sản phẩm trong đơn
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("order")
    private List<OrderItem> orderItems;

    @Column(name = "delivery_image_url")
    private String deliveryImageUrl;

    public Order() {}

    public Long getId() { return id; }
    public User getUser() { return user; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public List<OrderItem> getOrderItems() { return orderItems; }
    public String getDeliveryImageUrl() { return deliveryImageUrl; }

    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setStatus(String status) { this.status = status; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
    public void setDeliveryImageUrl(String deliveryImageUrl) { this.deliveryImageUrl = deliveryImageUrl; }
}
