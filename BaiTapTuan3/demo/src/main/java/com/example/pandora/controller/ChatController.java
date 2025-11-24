package com.example.pandora.controller;

import com.example.pandora.model.ChatMessage;
import com.example.pandora.repository.UserRepository;
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

    // 💬 Lấy lịch sử chat giữa user và manager
    @GetMapping("/conversation/{userId}/{managerId}")
    public ResponseEntity<List<ChatMessage>> getConversation(
            @PathVariable Long userId,
            @PathVariable Long managerId) {
        return ResponseEntity.ok(chatService.getConversation(userId, managerId));
    }

    // 📦 Lấy tất cả tin nhắn liên quan đến 1 sản phẩm
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ChatMessage>> getMessagesByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(chatService.getByProduct(productId));
    }
 // 📋 Lấy danh sách các sản phẩm có tin nhắn
    @GetMapping("/list")
    public ResponseEntity<List<Long>> getAllProductIdsWithMessages() {
        List<Long> productIds = chatService.getAllProductIdsWithMessages();
        return ResponseEntity.ok(productIds);
    }
    @GetMapping("/manager/id")
    public ResponseEntity<Long> getManagerId() {
        return ResponseEntity.ok(chatService.getManagerId());
    }
    @GetMapping("/list/details")
    public ResponseEntity<List<Object[]>> getAllProductAndSenderIds() {
        return ResponseEntity.ok(chatService.getAllProductAndSenderIds());
    }



}
