package com.example.pandora.controller;

import com.example.pandora.model.ChatMessage;
import com.example.pandora.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    // 📩 Gửi tin nhắn
    @PostMapping("/send")
    public ResponseEntity<ChatMessage> sendMessage(@RequestBody ChatMessage message) {
        ChatMessage saved = chatService.saveMessage(message);
        return ResponseEntity.ok(saved);
    }

    // 💬 Lấy lịch sử chat giữa user và admin
    @GetMapping("/conversation/{userId}/{adminId}")
    public ResponseEntity<List<ChatMessage>> getConversation(
            @PathVariable Long userId,
            @PathVariable Long adminId) {
        return ResponseEntity.ok(chatService.getConversation(userId, adminId));
    }

    // 👉 Hoặc nếu muốn client không cần gửi adminId:
    // @GetMapping("/conversation/{userId}")
    // public ResponseEntity<List<ChatMessage>> getConversation(@PathVariable Long userId) {
    //     Long adminId = chatService.getAdminId();
    //     return ResponseEntity.ok(chatService.getConversation(userId, adminId));
    // }

    // 📦 Lấy tất cả tin nhắn liên quan đến 1 sản phẩm
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ChatMessage>> getMessagesByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(chatService.getByProduct(productId));
    }

    // 📋 Lấy danh sách các sản phẩm có tin nhắn
    @GetMapping("/list")
    public ResponseEntity<List<Long>> getAllProductIdsWithMessages() {
        return ResponseEntity.ok(chatService.getAllProductIdsWithMessages());
    }

    // 🔑 Lấy ID admin
    @GetMapping("/admin/id")
    public ResponseEntity<Long> getAdminId() {
        return ResponseEntity.ok(chatService.getAdminId());
    }

    // 🧩 Lấy danh sách (productId, senderId, tên sender...) cho màn admin
    @GetMapping("/list/details")
    public ResponseEntity<List<Object[]>> getAllProductAndSenderIds() {
        return ResponseEntity.ok(chatService.getAllProductAndSenderIds());
    }

    // 🚀 API auto-start: tạo tin nhắn chào khi khách mở chat sản phẩm
    @PostMapping("/auto-start")
    public ResponseEntity<ChatMessage> autoStartChat(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        ChatMessage msg = chatService.autoStartChat(userId, productId);
        return ResponseEntity.ok(msg); // có thể null nếu đã có hội thoại từ trước
    }
}
