package com.example.pandora.repository;

import com.example.pandora.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 💬 Lấy toàn bộ tin nhắn giữa user và admin
    @Query("""
        SELECT c FROM ChatMessage c
        WHERE (c.senderId = :userId AND c.receiverId = :adminId)
           OR (c.senderId = :adminId AND c.receiverId = :userId)
        ORDER BY c.timestamp ASC
    """)
    List<ChatMessage> findConversation(Long userId, Long adminId);

    // 📦 Lấy tất cả tin nhắn liên quan đến một sản phẩm cụ thể
    List<ChatMessage> findByProductId(Long productId);

    // 🧩 Lấy danh sách sản phẩm, người gửi và tên người gửi (theo thời gian mới nhất)
    @Query("""
    	    SELECT c.productId, u.id, u.fullName, p.name, MAX(c.timestamp)
    	    FROM ChatMessage c
    	    JOIN User u ON c.senderId = u.id
    	    JOIN Product p ON c.productId = p.id
    	    GROUP BY c.productId, u.id, u.fullName, p.name
    	    ORDER BY MAX(c.timestamp) DESC
    	""")
    	List<Object[]> findProductSenderWithName();


    // ✅ Kiểm tra đã tồn tại hội thoại user–admin cho 1 sản phẩm chưa
    @Query("""
        SELECT COUNT(c) > 0
        FROM ChatMessage c
        WHERE (
                 (c.senderId = :userId AND c.receiverId = :adminId)
              OR (c.senderId = :adminId AND c.receiverId = :userId)
              )
          AND c.productId = :productId
    """)
    boolean existsConversationForProduct(Long userId, Long adminId, Long productId);

    // 📋 Lấy danh sách productId có tin nhắn (không trùng)
    @Query("SELECT DISTINCT c.productId FROM ChatMessage c WHERE c.productId IS NOT NULL")
    List<Long> findDistinctProductIds();
}
