package com.example.pandora.controller;

import com.example.pandora.model.Product;
import com.example.pandora.model.ProductDetail;
import com.example.pandora.model.Review;
import com.example.pandora.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    // ✅ Constructor Injection (chuẩn hơn @Autowired)
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ==============================================================
    // 🟢 QUẢN LÝ SẢN PHẨM
    // ==============================================================

    // ✅ Lấy toàn bộ sản phẩm
    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // ✅ Lấy 1 sản phẩm cụ thể
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    // ✅ Thêm sản phẩm mới
    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product saved = productService.addProduct(product);
        return ResponseEntity.ok(saved);
    }

    // ✅ Cập nhật sản phẩm
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            Product updated = productService.updateProduct(id, product);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Xóa sản phẩm
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
 // Lấy sản phẩm cùng loại, loại trừ chính nó
    @GetMapping("/related/{category}/{excludeId}")
    public ResponseEntity<List<Product>> getRelatedProducts(
            @PathVariable String category,
            @PathVariable Long excludeId) {

        List<Product> related = productService.getRelatedProducts(category, excludeId);

        return ResponseEntity.ok(related);
    }

    // ==============================================================
    // 🔵 CHI TIẾT SẢN PHẨM
    // ==============================================================

    // ✅ Lấy chi tiết sản phẩm theo productId
    @GetMapping("/{id}/detail")
    public ResponseEntity<ProductDetail> getProductDetail(@PathVariable Long id) {
        ProductDetail detail = productService.getProductDetail(id);
        if (detail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(detail);
    }

    // ✅ Cập nhật hoặc thêm mới chi tiết sản phẩm
    @PutMapping("/{id}/detail")
    public ResponseEntity<ProductDetail> saveProductDetail(@PathVariable Long id, @RequestBody ProductDetail detail) {
        try {
            ProductDetail saved = productService.saveProductDetail(id, detail);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==============================================================
    // 🟣 ĐÁNH GIÁ (REVIEWS)
    // ==============================================================

    // ✅ Lấy danh sách đánh giá của 1 sản phẩm
    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable Long id) {
        List<Review> reviews = productService.getReviewsByProductId(id);
        return ResponseEntity.ok(reviews);
    }

    // ✅ Thêm đánh giá mới cho sản phẩm
    @PostMapping("/{id}/reviews")
    public ResponseEntity<Review> addReview(@PathVariable Long id, @RequestBody Review review) {
        try {
            Review saved = productService.addReview(id, review);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
