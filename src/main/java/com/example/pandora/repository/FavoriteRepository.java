package com.example.pandora.repository;

import com.example.pandora.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // 🔹 Lấy danh sách yêu thích theo user_id
    List<Favorite> findByUserId(Long userId);

    // 🔹 Kiểm tra xem 1 sản phẩm đã được người dùng thích chưa
    Favorite findByUserIdAndProductId(Long userId, Long productId);
}
