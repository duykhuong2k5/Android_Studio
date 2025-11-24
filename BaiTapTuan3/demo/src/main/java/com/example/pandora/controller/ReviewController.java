package com.example.pandora.controller;

import java.util.List;

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

import com.example.pandora.model.Product;
import com.example.pandora.model.Review;
import com.example.pandora.model.User;
import com.example.pandora.model.request.ReviewRequest;
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


    @PostMapping("/{productId}/reviews")
    public Review addReview(
            @PathVariable Long productId,
            @RequestBody ReviewRequest request) {

        Product product = productRepository.findById(productId).orElseThrow();
        User user = userRepository.findById(request.getUserId()).orElseThrow();

        Review review = new Review(
                user,
                request.getRating(),
                request.getComment(),
                request.getImageUrl(),
                product
        );

        return reviewRepository.save(review);
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
}
