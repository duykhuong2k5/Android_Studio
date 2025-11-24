package com.example.pandora.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;     // ID người gửi (user hoặc manager)
    private Long receiverId;   // ID người nhận
    private Long productId;    // ID sản phẩm được hỏi
    private String message;    // Nội dung tin nhắn
    private LocalDateTime timestamp; // Thời gian gửi

    public ChatMessage() {}

    public ChatMessage(Long senderId, Long receiverId, Long productId, String message) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.productId = productId;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    // ✅ Getter/Setter
    public Long getId() { return id; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
