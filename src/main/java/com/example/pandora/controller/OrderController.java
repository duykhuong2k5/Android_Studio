package com.example.pandora.controller;

import com.example.pandora.dto.OrderDTO;
import com.example.pandora.dto.RevenueSummaryDTO;
import com.example.pandora.model.Address;
import com.example.pandora.model.Order;
import com.example.pandora.model.User;
import com.example.pandora.repository.AddressRepository;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin("*")
public class OrderController {

    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private OrderService orderService;
    @Autowired private ImageUploadService imageUploadService;


    // ============================================================
    // 1) Lấy tất cả đơn hàng (admin)
    // ============================================================
    @GetMapping("/all")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        return ResponseEntity.ok(
                orders.stream().map(OrderDTO::new).toList()
        );
    }


    // ============================================================
    // 2) Lấy đơn theo user
    // ============================================================
    @GetMapping("/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUser(@PathVariable Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        return ResponseEntity.ok(
                orders.stream().map(OrderDTO::new).toList()
        );
    }
	 // ============================================================
	 // LẤY CHI TIẾT ĐƠN HÀNG
	 // ============================================================
	 @GetMapping("/detail/{id}")
	 public ResponseEntity<?> getOrderDetail(@PathVariable Long id) {
	
	     Order order = orderRepository.findById(id).orElse(null);
	
	     if (order == null)
	         return ResponseEntity.notFound().build();
	
	     return ResponseEntity.ok(new OrderDTO(order));
	 }



    // ============================================================
    // 3) Tạo đơn mới (Android)
    // ============================================================
    @PostMapping("/add")
    public ResponseEntity<?> addOrder(@RequestBody Order order) {

        // Kiểm tra user
        User user = userRepository.findById(order.getUser().getId()).orElse(null);
        if (user == null) return ResponseEntity.badRequest().body("User không tồn tại!");

        // Kiểm tra địa chỉ
        Address address = addressRepository.findById(order.getAddress().getId()).orElse(null);
        if (address == null) return ResponseEntity.badRequest().body("Địa chỉ không tồn tại!");

        // Tạo order mới
        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setAddress(address);

        newOrder.setOrderDate(LocalDateTime.now());

        newOrder.setTotalPrice(order.getTotalPrice());
        newOrder.setDiscount(order.getDiscount());

        // Free ship luôn bật
        newOrder.setShippingFee(0);
        newOrder.setFreeShip(true);

        newOrder.setVoucherCode(order.getVoucherCode());

        // Phương thức thanh toán
        String method = order.getPaymentMethod();
        if (method == null || method.isBlank()) method = "COD";

        newOrder.setPaymentMethod(method);
        newOrder.setPaymentStatus("UNPAID");

        // Trạng thái ban đầu
        newOrder.setStatus("PENDING");

        // Thêm sản phẩm
        order.getOrderItems().forEach(i -> i.setOrder(newOrder));
        newOrder.setOrderItems(order.getOrderItems());

        Order saved = orderRepository.save(newOrder);

        return ResponseEntity.ok(saved);
    }


	 // ============================================================
	 // 4) Admin/Manager duyệt đơn → WAITING_SHIPPER
	 // ============================================================
	 @PutMapping("/{id}/approve")
	 public ResponseEntity<?> approveOrder(@PathVariable Long id) {
	     Order order = orderRepository.findById(id).orElse(null);
	     if (order == null) {
	         return ResponseEntity.notFound().build();
	     }
	
	     if (!"PENDING".equals(order.getStatus())) {
	         return ResponseEntity
	                 .badRequest()
	                 .body(Map.of("message", "Chỉ duyệt được đơn đang ở trạng thái PENDING"));
	     }
	
	     order.setStatus("WAITING_SHIPPER");
	     orderRepository.save(order);
	
	     return ResponseEntity.ok(
	             Map.of(
	                     "message", "Đã duyệt đơn, chờ shipper nhận (WAITING_SHIPPER)",
	                     "status", order.getStatus()
	             )
	     );
	 }
	 
	// ============================================================
	// 5) Shipper nhận đơn → DELIVERING
	// ============================================================
	@PutMapping("/{id}/shipper-accept")
	public ResponseEntity<?> shipperAccept(@PathVariable Long id) {
	    Order order = orderRepository.findById(id).orElse(null);
	    if (order == null) {
	        return ResponseEntity.notFound().build();
	    }

	    if (!"WAITING_SHIPPER".equals(order.getStatus())) {
	        return ResponseEntity
	                .badRequest()
	                .body(Map.of("message", "Đơn phải ở trạng thái WAITING_SHIPPER thì mới nhận được"));
	    }

	    order.setStatus("DELIVERING");
	    orderRepository.save(order);

	    return ResponseEntity.ok(
	            Map.of(
	                    "message", "Shipper đã nhận đơn, chuyển sang DELIVERING",
	                    "status", order.getStatus()
	            )
	    );
	}
	



    // ============================================================
    // 5) Hoàn thành đơn (upload ảnh)
    // ============================================================
    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeOrder(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return ResponseEntity.notFound().build();

        if (!order.getStatus().equals("DELIVERING"))
            return ResponseEntity.badRequest().body("Đơn chưa ở trạng thái giao hàng!");

        String base64 = request.get("image");
        if (base64 == null)
            return ResponseEntity.badRequest().body(Map.of("message", "Thiếu ảnh giao hàng!"));

        String imageUrl = imageUploadService.uploadBase64(base64);

        order.setDeliveryImageUrl(imageUrl);
        order.setStatus("COMPLETED");
        orderRepository.save(order);

        return ResponseEntity.ok(Map.of("message", "Giao hàng thành công!", "imageUrl", imageUrl));
    }


    // ============================================================
    // 6) Hủy đơn (khách hàng)
    // ============================================================
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return ResponseEntity.notFound().build();

        if (!order.getStatus().equals("PENDING"))
            return ResponseEntity.badRequest().body("Đơn không thể hủy!");

        String reason = request.get("reason");
        if (reason == null || reason.isBlank())
            return ResponseEntity.badRequest().body("Vui lòng nhập lý do hủy!");

        order.setStatus("FAILED");
        order.setCancelReason(reason);
        orderRepository.save(order);

        return ResponseEntity.ok(Map.of(
                "message", "Hủy đơn thành công",
                "reason", reason
        ));
    }


    @PutMapping("/{id}/failed")
    public ResponseEntity<?> failedOrder(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return ResponseEntity.notFound().build();

        if (!"DELIVERING".equals(order.getStatus())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Chỉ báo thất bại cho đơn đang ở trạng thái DELIVERING"));
        }

        String reason = request.get("reason");
        if (reason == null || reason.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Vui lòng nhập lý do giao thất bại"));
        }

        order.setStatus("FAILED");
        order.setFailReason(reason);   // <-- lưu lý do shipper nhập
        orderRepository.save(order);

        return ResponseEntity.ok(Map.of(
                "message", "Đã cập nhật đơn giao thất bại",
                "reason", reason
        ));
    }

    // Endpoint để lấy đơn hàng theo năm và tháng
    @GetMapping("/revenue/summary")
    public ResponseEntity<RevenueSummaryDTO> getRevenueSummary(
            @RequestParam String year,
            @RequestParam String month) {

        RevenueSummaryDTO summary = orderService.getRevenueSummary(year, month);
        return ResponseEntity.ok(summary);
    }


}
