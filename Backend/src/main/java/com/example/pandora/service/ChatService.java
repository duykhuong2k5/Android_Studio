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

    // Gửi tin nhắn bình thường
    public ChatMessage saveMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        return chatRepo.save(message);
    }

    // ✅ Auto gửi tin nhắn chào khi mở chat lần đầu
    public ChatMessage autoStartChat(Long userId, Long productId) {
        Long adminId = getAdminId();

        boolean existed = chatRepo.existsConversationForProduct(userId, adminId, productId);
        if (existed) {
            return null; // đã có hội thoại rồi, không gửi lại tin chào
        }

        ChatMessage msg = new ChatMessage();
        msg.setSenderId(adminId);  // admin gửi
        msg.setReceiverId(userId); // khách nhận
        msg.setProductId(productId);
        msg.setMessage("Xin chào bạn, tôi có thể tư vấn cho bạn về sản phẩm này không?");
        msg.setTimestamp(LocalDateTime.now());

        return chatRepo.save(msg);
    }

    // Danh sách (productId, senderId, fullName...) cho màn admin
    public List<Object[]> getAllProductAndSenderIds() {
        return chatRepo.findProductSenderWithName();
    }

    // Lịch sử chat user – admin
    public List<ChatMessage> getConversation(Long userId, Long adminId) {
        return chatRepo.findConversation(userId, adminId);
    }

    // Tin theo sản phẩm
    public List<ChatMessage> getByProduct(Long productId) {
        return chatRepo.findByProductId(productId);
    }

    // Danh sách product có tin nhắn
    public List<Long> getAllProductIdsWithMessages() {
        return chatRepo.findDistinctProductIds();
    }

    // Lấy ID admin
    public Long getAdminId() {
        return userRepository.findFirstByRole("ROLE_ADMIN")
                .map(user -> user.getId())
                .orElse(3L); // fallback nếu không tìm thấy
    }
}
