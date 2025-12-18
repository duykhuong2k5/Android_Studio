package com.example.pandora.repository;

import com.example.pandora.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	
	
	List<Order> findByUserId(Long userId);
    // 🔹 Lấy danh sách đơn hàng theo user_id
	@Query("""
		       SELECT o FROM Order o
		       WHERE extract(year from o.orderDate) = :year
		         AND extract(month from o.orderDate) = :month
		         AND o.status = 'COMPLETED'
		       """)
		List<Order> findByYearAndMonth(@Param("year") int year,
		                               @Param("month") int month);



    // ✅ Tổng doanh thu tất cả đơn đã hoàn tất
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = 'COMPLETED'")
    Double getTotalRevenue();

    // ✅ Tổng số đơn hàng đã hoàn tất
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'COMPLETED'")
    Long getCompletedOrderCount();

    // ✅ Doanh thu theo từng user (manager xem tổng của mình)
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.user.id = :userId AND o.status = 'COMPLETED'")
    Double getRevenueByUser(@Param("userId") Long userId);
    
    @Query("""
    	    SELECT oi.product.category
    	    FROM OrderItem oi
    	    JOIN oi.order o
    	    JOIN o.user u
    	    WHERE u.id = :userId
    	    GROUP BY oi.product.category
    	    ORDER BY COUNT(oi.id) DESC
    	    """)
    	List<String> findTopCategoryByUser(
    	        @Param("userId") Long userId,
    	        org.springframework.data.domain.Pageable pageable
    	);
    @Query("""
            SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END
            FROM Order o
            JOIN o.orderItems i
            WHERE o.user.id = :userId
              AND o.status = 'COMPLETED'
              AND i.product.id = :productId
            """)
     boolean hasCompletedOrderForProduct(@Param("userId") Long userId,
                                         @Param("productId") Long productId);


}

