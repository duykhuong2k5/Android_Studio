package com.example.pandora.data.entity;

public class ChatMessage {
    private Long id;
    private long senderId;
    private long receiverId;
    private long productId;
    private String message;
    private String timestamp;

    public ChatMessage() {
        // needed for JSON parse
    }

    public ChatMessage(Long id, long senderId, long receiverId,
                       long productId, String message, String timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.productId = productId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public long getSenderId() { return senderId; }
    public long getReceiverId() { return receiverId; }
    public long getProductId() { return productId; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }

    public void setId(Long id) { this.id = id; }
    public void setMessage(String message) { this.message = message; }
}
