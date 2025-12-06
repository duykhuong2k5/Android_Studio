package com.example.pandora.repository;

import com.example.pandora.dto.ReviewAdminDto;
import com.example.pandora.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
    @Query("""
            SELECT new com.example.pandora.dto.ReviewAdminDto(
                r.id,
                COALESCE(u.fullName, 'Người dùng'),
                r.rating,
                r.comment,
                CAST(r.createdAt AS string),
                p.name
            )
            FROM Review r
            LEFT JOIN r.user u
            LEFT JOIN r.product p
            ORDER BY r.createdAt DESC
        """)
        List<ReviewAdminDto> findAllForAdmin();
}
