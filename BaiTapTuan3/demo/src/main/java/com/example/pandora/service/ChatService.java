package com.example.pandora.service;

import com.example.pandora.model.ChatMessage;
import com.example.pandora.repository.ChatMessageRepository;
import com.example.pandora.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatRepo;
    @Autowired
    private UserRepository userRepository;

    public ChatMessage saveMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        return chatRepo.save(message);
    }
    public List<Object[]> getAllProductAndSenderIds() {
        return chatRepo.findProductSenderWithName();
    }



    public List<ChatMessage> getConversation(Long userId, Long managerId) {
        return chatRepo.findConversation(userId, managerId);
    }

    public List<ChatMessage> getByProduct(Long productId) {
        return chatRepo.findByProductId(productId);
    }
    public List<Long> getAllProductIdsWithMessages() {
        return chatRepo.findDistinctProductIds();
    }
 // ✅ thêm hàm lấy manager ID
    public Long getManagerId() {
        return userRepository.findFirstByRole("ROLE_MANAGER")
                .map(user -> user.getId())
                .orElse(4L);
    }

}
