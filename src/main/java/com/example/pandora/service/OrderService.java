package com.example.pandora.service;

import com.example.pandora.dto.OrderDTO;
import com.example.pandora.dto.RevenueSummaryDTO;
import com.example.pandora.model.Order;
import com.example.pandora.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public List<OrderDTO> getOrdersByYearAndMonth(String year, String month) {
        int y = Integer.parseInt(year);
        int m = Integer.parseInt(month);

        List<Order> orders = orderRepository.findByYearAndMonth(y, m);

        return orders.stream()
                .map(OrderDTO::new)
                .collect(Collectors.toList());
    }

    public RevenueSummaryDTO getRevenueSummary(String yearStr, String monthStr) {
        int year = Integer.parseInt(yearStr);
        int month = Integer.parseInt(monthStr);

        List<Order> orders = orderRepository.findByYearAndMonth(year, month);

        double totalRevenue = 0;
        for (Order o : orders) {
            totalRevenue += o.getTotalPrice();
        }

        long count = orders.size();
        return new RevenueSummaryDTO(totalRevenue, count);
    }


}
