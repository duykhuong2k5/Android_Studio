package com.example.pandora.service;

import com.example.pandora.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // ✅ Tổng doanh thu cho tất cả đơn hàng
    public Map<String, Object> getOverallStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenue", orderRepository.getTotalRevenue());
        stats.put("completedOrders", orderRepository.getCompletedOrderCount());
        return stats;
    }

    // ✅ Doanh thu của riêng một user (manager / khách hàng)
    public Map<String, Object> getStatsByUser(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userId", userId);
        stats.put("userRevenue", orderRepository.getRevenueByUser(userId));
        return stats;
    }
}
