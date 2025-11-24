package com.example.pandora.controller;

import com.example.pandora.dto.OrderDTO;
import com.example.pandora.model.Order;
import com.example.pandora.model.User;
import com.example.pandora.repository.OrderRepository;
import com.example.pandora.repository.UserRepository;
import com.example.pandora.service.ImageUploadService;
import com.example.pandora.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ImageUploadService imageUploadService;
    
 // ✅ 1. Lấy tất cả đơn hàng (ADMIN)
    @GetMapping("/all")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<OrderDTO> result = orders.stream()
                .map(OrderDTO::new)
                .toList();

        return ResponseEntity.ok(result);
    }
    
    // ✅ Lấy danh sách đơn hàng theo user
    @GetMapping("/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUser(@PathVariable Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<OrderDTO> result = orders.stream()
                .map(OrderDTO::new)
                .toList();

        return ResponseEntity.ok(result);
    }


    // ✅ Tạo đơn hàng mới (sửa lại đúng chuẩn)
    @PostMapping("/add")
    public ResponseEntity<?> addOrder(@RequestBody Order order) {

        // 1️⃣ Kiểm tra user hợp lệ
        if (order.getUser() == null || order.getUser().getId() == null) {
            return ResponseEntity.badRequest().body("Thiếu thông tin người dùng!");
        }

        User user = userRepository.findById(order.getUser().getId()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User không tồn tại!");
        }

        // 2️⃣ Tạo order mới
        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setTotalPrice(order.getTotalPrice());
        newOrder.setOrderDate(LocalDateTime.now());

        // 3️⃣ Phương thức thanh toán: COD / VNPAY
        String method = order.getPaymentMethod();
        if (method == null || method.isBlank()) {
            method = "COD"; // mặc định nếu Android không gửi
        }
        newOrder.setPaymentMethod(method);

        // 4️⃣ Xử lý trạng thái thanh toán
        if (method.equalsIgnoreCase("COD")) {
            // 💵 Thanh toán khi nhận hàng
            newOrder.setPaymentStatus("UNPAID");
            newOrder.setStatus("PENDING"); 
        } else {
            // 💳 Thanh toán VNPay
            newOrder.setPaymentStatus("PENDING"); 
            newOrder.setStatus("PENDING"); 
        }

        // 5️⃣ Xử lý order items
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            order.getOrderItems().forEach(item -> item.setOrder(newOrder));
            newOrder.setOrderItems(order.getOrderItems());
        } else {
            return ResponseEntity.badRequest().body("Đơn hàng không có sản phẩm!");
        }

        // 6️⃣ Lưu đơn
        Order saved = orderRepository.save(newOrder);

        // ✔ Với Android Retrofit: Call<Order> → phải trả Order gốc
        return ResponseEntity.ok(saved);
    }



    

    // ✅ Tổng doanh thu toàn hệ thống
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getOverallStats() {
        return ResponseEntity.ok(orderService.getOverallStats());
    }

    // ✅ Doanh thu theo user (manager hoặc khách hàng)
    @GetMapping("/stats/{userId}")
    public ResponseEntity<Map<String, Object>> getStatsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getStatsByUser(userId));
    }
    
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return ResponseEntity.notFound().build();

        // ❗ Chỉ admin/manager mới chuyển đơn sang WAITING_SHIPPER
        if (!order.getStatus().equals("CUSTOMER_PAID")) {
            return ResponseEntity.badRequest().body("Đơn không hợp lệ để duyệt!");
        }

        order.setStatus("WAITING_SHIPPER");
        orderRepository.save(order);

        return ResponseEntity.ok(Map.of(
                "message", "Đơn đã chuyển sang WAITING_SHIPPER",
                "status", order.getStatus()
        ));
    }

    
    @PutMapping("/{id}/shipper-accept")
    public ResponseEntity<?> shipperAccept(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return ResponseEntity.notFound().build();

        if (!order.getStatus().equals("WAITING_SHIPPER")) {
            return ResponseEntity.badRequest().body("Đơn này đã có shipper hoặc không chờ nhận!");
        }

        order.setStatus("DELIVERING");
        orderRepository.save(order);

        return ResponseEntity.ok(Map.of(
                "message", "Shipper đã nhận đơn!",
                "status", order.getStatus()
        ));
    }


    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeOrder(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return ResponseEntity.notFound().build();

        String base64 = request.get("image");

        if (base64 == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Thiếu ảnh xác nhận giao hàng!"
            ));
        }

        String imageUrl = imageUploadService.uploadBase64(base64);

        order.setDeliveryImageUrl(imageUrl);
        order.setStatus("COMPLETED");
        orderRepository.save(order);

        return ResponseEntity.ok(Map.of(
                "message", "Giao hàng thành công!",
                "imageUrl", imageUrl
        ));
    }



    @PutMapping("/{id}/failed")
    public ResponseEntity<?> failedOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return ResponseEntity.notFound().build();

        if (!order.getStatus().equals("DELIVERING")) {
            return ResponseEntity.badRequest().body("Đơn không ở trạng thái giao hàng!");
        }

        order.setStatus("FAILED");
        orderRepository.save(order);

        return ResponseEntity.ok(Map.of(
                "message", "Giao hàng thất bại!",
                "status", order.getStatus()
        ));
    }


    

}
