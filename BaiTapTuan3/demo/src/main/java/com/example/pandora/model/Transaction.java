package com.example.pandora.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // mã giao dịch nội bộ

    @Column(name = "order_id")
    private Long orderId; // ID đơn hàng (nếu có)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // người thực hiện giao dịch

    @Column(name = "amount", nullable = false)
    private Long amount; // số tiền (VND)

    @Column(name = "status", length = 20)
    private String status; // PENDING / SUCCESS / FAILED

    @Column(name = "vnpay_txn_ref")
    private String vnpTxnRef; // mã giao dịch VNPAY trả về

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate = new Date();

    @Column(name = "modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;

    public Transaction() {}

    // ======== GETTERS ========

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public User getUser() {
        return user;
    }

    public Long getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getVnpTxnRef() {
        return vnpTxnRef;
    }

    public String getNote() {
        return note;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    // ======== SETTERS ========

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setVnpTxnRef(String vnpTxnRef) {
        this.vnpTxnRef = vnpTxnRef;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
