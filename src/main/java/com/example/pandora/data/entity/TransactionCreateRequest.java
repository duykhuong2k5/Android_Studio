package com.example.pandora.data.entity;

public class TransactionCreateRequest {

    private Long userId;
    private Long orderId;
    private Long amount;
    private String note;

    // Constructor rỗng — BẮT BUỘC CHO GSON
    public TransactionCreateRequest() {}

    public TransactionCreateRequest(Long userId, Long orderId, Long amount, String note) {
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
        this.note = note;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
