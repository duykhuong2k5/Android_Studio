package com.example.pandora.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import com.example.pandora.dto.ReviewAdminDto;
import com.example.pandora.model.Product;
import com.example.pandora.model.Review;
import com.example.pandora.model.User;
import com.example.pandora.model.request.ReviewRequest;
import com.example.pandora.repository.OrderRepository;
import com.example.pandora.repository.ProductRepository;
import com.example.pandora.repository.ReviewRepository;
import com.example.pandora.repository.UserRepository;


@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderRepository orderRepository;


 // ⭐ Chỉ user đã mua & đơn COMPLETED mới được đánh giá
    @PostMapping("/{productId}/reviews")
    public ResponseEntity<?> addReview(
            @PathVariable Long productId,
            @RequestBody ReviewRequest request) {

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Không tìm thấy sản phẩm."));
        }

        User user = userRepository.findById(request.getUserId()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Không tìm thấy người dùng."));
        }

        // ✅ kiểm tra đã có đơn COMPLETED chứa sản phẩm này chưa
        boolean canReview = orderRepository
                .hasCompletedOrderForProduct(user.getId(), productId);

        if (!canReview) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message",
                            "Bạn chỉ có thể đánh giá sản phẩm đã mua và đơn hàng đã hoàn thành."));
        }

        Review review = new Review(
                user,
                request.getRating(),
                request.getComment(),
                request.getImageUrl(),
                product
        );

        Review saved = reviewRepository.save(review);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/all")
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        if (!reviewRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        reviewRepository.deleteById(id);
        return ResponseEntity.ok("Đã xóa review");
    }
 // 👉 API riêng cho admin (dùng ở màn ManageReviewsActivity)
    @GetMapping("/admin")
    public List<ReviewAdminDto> getAllForAdmin() {
        return reviewRepository.findAllForAdmin();
    }
}
