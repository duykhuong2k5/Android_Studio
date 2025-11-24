package com.example.pandora.repository;

import com.example.pandora.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 💬 Lấy toàn bộ tin nhắn giữa user và manager
    @Query("SELECT c FROM ChatMessage c WHERE " +
            "(c.senderId = :userId AND c.receiverId = :managerId) " +
            "OR (c.senderId = :managerId AND c.receiverId = :userId) " +
            "ORDER BY c.timestamp ASC")
    List<ChatMessage> findConversation(Long userId, Long managerId);

    // 📦 Lấy tất cả tin nhắn liên quan đến một sản phẩm cụ thể
    List<ChatMessage> findByProductId(Long productId);

 // 🧩 Lấy danh sách sản phẩm, người gửi và tên người gửi (theo thời gian mới nhất)
    @Query("""
        SELECT c.productId, u.id, u.fullName
        FROM ChatMessage c
        JOIN User u ON c.senderId = u.id
        GROUP BY c.productId, u.id, u.fullName
        ORDER BY MAX(c.timestamp) DESC
    """)
    List<Object[]> findProductSenderWithName();


}
