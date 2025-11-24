package com.example.pandora.repository;

import com.example.pandora.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 🔹 Lấy danh sách đơn hàng theo user_id
    List<Order> findByUserId(Long userId);

    // ✅ Tổng doanh thu tất cả đơn đã hoàn tất
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = 'COMPLETED'")
    Double getTotalRevenue();

    // ✅ Tổng số đơn hàng đã hoàn tất
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'COMPLETED'")
    Long getCompletedOrderCount();

    // ✅ Doanh thu theo từng user (manager xem tổng của mình)
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.user.id = :userId AND o.status = 'COMPLETED'")
    Double getRevenueByUser(Long userId);
}

