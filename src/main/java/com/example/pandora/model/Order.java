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
    
    // 🏠 Địa chỉ giao hàng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    @JsonIgnoreProperties({"user"})
    private Address address;


    // 💰 Tổng giá trị đơn
    @Column(nullable = false)
    private double totalPrice;
    // 🏷 Mã voucher đã áp dụng
    private String voucherCode;

    // 💸 Số tiền giảm từ voucher
    private double discount;

    // 🚚 Phí vận chuyển (backend luôn 0 – vì freeship)
    private double shippingFee = 0;

    // 🚚 Có phải freeship không
    private boolean freeShip = true;

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
    // ❌ Lý do hủy đơn
    private String cancelReason;
    private String failReason;
    public Order() {}

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Address getAddress() { return address; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public List<OrderItem> getOrderItems() { return orderItems; }
    public String getDeliveryImageUrl() { return deliveryImageUrl; }
    public String getVoucherCode() { return voucherCode; }
    public double getDiscount() { return discount; }
    public double getShippingFee() { return shippingFee; }
    public boolean isFreeShip() { return freeShip; }
    public String getCancelReason() { return cancelReason; }
    public String getFailReason() { return failReason; }
    public void setFailReason(String failReason) { this.failReason = failReason; }

    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setAddress(Address address) { this.address = address; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setStatus(String status) { this.status = status; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
    public void setDeliveryImageUrl(String deliveryImageUrl) { this.deliveryImageUrl = deliveryImageUrl; }
    public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }
    public void setDiscount(double discount) { this.discount = discount; }
    public void setShippingFee(double shippingFee) { this.shippingFee = shippingFee; }
    public void setFreeShip(boolean freeShip) { this.freeShip = freeShip; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
}
