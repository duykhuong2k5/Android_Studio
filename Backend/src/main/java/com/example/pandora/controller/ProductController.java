package com.example.pandora.controller;

import com.example.pandora.model.Product;
import com.example.pandora.model.ProductDetail;
import com.example.pandora.model.ProductImage;
import com.example.pandora.model.ProductSize;
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
    @GetMapping("/{id}/sold")
    public ResponseEntity<Long> getProductSold(@PathVariable Long id) {
        Long sold = productService.getSoldCount(id);
        return ResponseEntity.ok(sold);
    }
    
    @GetMapping("/recommend/{userId}")
    public ResponseEntity<List<Product>> getRecommendations(@PathVariable Long userId) {
        List<Product> list = productService.getRecommendationsForUser(userId);
        return ResponseEntity.ok(list);
    }
 // GET /api/products/search?q=nhan bac
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam("q") String keyword) {
        return productService.searchProducts(keyword);
    }

    // GET /api/products/suggestions?q=nh
    @GetMapping("/suggestions")
    public List<String> getSuggestions(@RequestParam("q") String keyword) {
        return productService.getSuggestions(keyword);
    }
 // GET /api/products/combo?category=COMBO_UU_DAI
    @GetMapping("/combo")
    public ResponseEntity<List<Product>> getComboProducts(
            @RequestParam("category") String comboCategory) {

        List<Product> list = productService.getComboByCategory(comboCategory);
        return ResponseEntity.ok(list);
    }
    // ==============================
    // 📸 ẢNH PHỤ CỦA SẢN PHẨM
    // ==============================

    // Lấy danh sách ảnh của 1 sản phẩm
    @GetMapping("/{id}/images")
    public ResponseEntity<List<ProductImage>> getProductImages(@PathVariable Long id) {
        List<ProductImage> images = productService.getImagesByProductId(id);
        return ResponseEntity.ok(images);
    }

    // Thêm 1 ảnh mới cho sản phẩm
    @PostMapping("/{id}/images")
    public ResponseEntity<ProductImage> addProductImage(
            @PathVariable Long id,
            @RequestBody ProductImage image) {

        try {
            ProductImage saved = productService.addImageToProduct(id, image);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Xóa 1 ảnh
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Void> deleteProductImage(@PathVariable Long imageId) {
        try {
            productService.deleteProductImage(imageId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
 // ✅ Lấy các size của 1 sản phẩm
    @GetMapping("/{id}/sizes")
    public ResponseEntity<List<ProductSize>> getProductSizes(@PathVariable Long id) {
        List<ProductSize> sizes = productService.getSizesByProductId(id);
        return ResponseEntity.ok(sizes);
    }

    // (tuỳ chọn – cho admin thêm size)
    @PostMapping("/{id}/sizes")
    public ResponseEntity<ProductSize> addSize(
            @PathVariable Long id,
            @RequestBody ProductSize size) {
        ProductSize saved = productService.addSizeToProduct(id, size);
        return ResponseEntity.ok(saved);
    }




}
