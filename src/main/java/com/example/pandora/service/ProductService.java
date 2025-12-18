package com.example.pandora.service;

import com.example.pandora.model.Order;
import com.example.pandora.model.Product;
import com.example.pandora.model.ProductDetail;
import com.example.pandora.model.ProductImage;
import com.example.pandora.model.ProductSize;
import com.example.pandora.model.Review;
import com.example.pandora.repository.OrderItemRepository;
import com.example.pandora.repository.OrderRepository;
import com.example.pandora.repository.ProductDetailRepository;
import com.example.pandora.repository.ProductImageRepository;
import com.example.pandora.repository.ProductRepository;
import com.example.pandora.repository.ProductSizeRepository;
import com.example.pandora.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepo;
    private final ProductDetailRepository detailRepo;
    private final ReviewRepository reviewRepo;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductSizeRepository productSizeRepository;

    


    public ProductService(ProductRepository productRepo, ProductDetailRepository detailRepo, ReviewRepository reviewRepo,OrderItemRepository orderItemRepository,OrderRepository orderRepository,ProductImageRepository productImageRepository,ProductSizeRepository productSizeRepository) {
        this.productRepo = productRepo;
        this.detailRepo = detailRepo;
        this.reviewRepo = reviewRepo;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productImageRepository = productImageRepository;
        this.productSizeRepository = productSizeRepository;
    }

    // 🔹 Lấy tất cả sản phẩm
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    // 🔹 Lấy sản phẩm theo ID
    public Product getProductById(Long id) {
        return productRepo.findById(id).orElse(null);
    }

    // 🔹 Thêm sản phẩm mới
    public Product addProduct(Product product) {
        return productRepo.save(product);
    }

    // 🔹 Cập nhật sản phẩm
    public Product updateProduct(Long id, Product product) {
        Product existing = productRepo.findById(id).orElseThrow(
                () -> new RuntimeException("Không tìm thấy sản phẩm ID: " + id)
        );
        existing.setName(product.getName());
        existing.setPriceNew(product.getPriceNew());
        existing.setPriceOld(product.getPriceOld());
        existing.setDiscountPercent(product.getDiscountPercent());
        existing.setImageUrl(product.getImageUrl());
        existing.setCategory(product.getCategory());
        return productRepo.save(existing);
    }

    // 🔹 Xóa sản phẩm
    public void deleteProduct(Long id) {
        if (!productRepo.existsById(id)) {
            throw new RuntimeException("Không tìm thấy sản phẩm ID: " + id);
        }
        productRepo.deleteById(id);
    }
    public List<Product> getRelatedProducts(String category, Long excludeId) {
        return productRepo.findByCategoryIgnoreCase(category)
                .stream()
                .filter(p -> !p.getId().equals(excludeId))
                .limit(10)
                .toList();
    }


    // 🔹 Lấy chi tiết sản phẩm
    public ProductDetail getProductDetail(Long productId) {
        return detailRepo.findByProductId(productId);
    }

    // 🔹 Lưu / cập nhật chi tiết sản phẩm
    public ProductDetail saveProductDetail(Long productId, ProductDetail detail) {
        Product product = productRepo.findById(productId).orElseThrow(
                () -> new RuntimeException("Không tìm thấy sản phẩm ID: " + productId)
        );
        detail.setProduct(product);
        return detailRepo.save(detail);
    }

    // 🔹 Lấy danh sách đánh giá của sản phẩm
    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepo.findByProductId(productId);
    }

    // 🔹 Thêm đánh giá mới
    public Review addReview(Long productId, Review review) {
        Optional<Product> productOpt = productRepo.findById(productId);
        if (productOpt.isPresent()) {
            review.setProduct(productOpt.get());
            return reviewRepo.save(review);
        } else {
            throw new RuntimeException("Không tìm thấy sản phẩm ID: " + productId);
        }
    }
    public Long getSoldCount(Long productId) {
        return orderItemRepository.sumSoldQuantity(productId);
    }
    // ⭐ Gợi ý sản phẩm cho user
    public List<Product> getRecommendationsForUser(Long userId) {

        List<Order> orders = orderRepository.findByUserId(userId);

        if (orders.isEmpty()) {
            return productRepo.findTop12ByOrderByIdDesc();
        }

        Set<Long> purchasedIds = orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(item -> item.getProduct().getId())
                .collect(Collectors.toSet());

        // ⭐ Lấy TOP CATEGORY
        List<String> categories = orderRepository.findTopCategoryByUser(
                userId,
                org.springframework.data.domain.PageRequest.of(0, 1)
        );

        if (categories.isEmpty()) {
            return productRepo.findTop12ByOrderByIdDesc();
        }

        String topCategory = categories.get(0);

        return productRepo.findByCategoryIgnoreCase(topCategory)
                .stream()
                .filter(p -> !purchasedIds.contains(p.getId()))
                .limit(12)
                .toList();
    }
 // 🔍 Tìm kiếm sản phẩm theo keyword
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            // nếu không nhập gì thì trả full list hoặc top sản phẩm tùy bạn
            return productRepo.findAll();
        }
        return productRepo
                .findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(keyword, keyword);
    }

    // 💡 Gợi ý tên sản phẩm (autocomplete) – giới hạn 10 gợi ý
    public List<String> getSuggestions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        return productRepo.findNameSuggestions(
                keyword.trim(),
                org.springframework.data.domain.PageRequest.of(0, 10)
        );
    }
 // 🔹 Lấy danh sách combo theo category combo
    public List<Product> getComboByCategory(String comboCategory) {
        return productRepo.findByCategoryIgnoreCaseAndIsComboTrue(comboCategory);
    }
 // =========================
    // 📸 QUẢN LÝ ẢNH SẢN PHẨM
    // =========================

    // Lấy danh sách ảnh theo productId
    public List<ProductImage> getImagesByProductId(Long productId) {
        return productImageRepository.findByProductId(productId);
    }

    // Thêm 1 ảnh mới cho sản phẩm
    public ProductImage addImageToProduct(Long productId, ProductImage image) {
        Product product = productRepo.findById(productId).orElseThrow(
                () -> new RuntimeException("Không tìm thấy sản phẩm ID: " + productId)
        );
        image.setProduct(product);
        return productImageRepository.save(image);
    }

    // Xóa 1 ảnh
    public void deleteProductImage(Long imageId) {
        if (!productImageRepository.existsById(imageId)) {
            throw new RuntimeException("Không tìm thấy ảnh ID: " + imageId);
        }
        productImageRepository.deleteById(imageId);
    }
 // 🔹 Lấy list size theo productId
    public List<ProductSize> getSizesByProductId(Long productId) {
        return productSizeRepository.findByProductId(productId);
    }

    // (nếu cần admin thêm size)
    public ProductSize addSizeToProduct(Long productId, ProductSize size) {
        Product p = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        size.setProduct(p);
        return productSizeRepository.save(size);
    }




}
