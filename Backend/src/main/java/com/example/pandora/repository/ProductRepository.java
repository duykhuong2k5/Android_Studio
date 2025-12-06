package com.example.pandora.repository;

import com.example.pandora.model.Product;
import org.springframework.data.domain.Pageable;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByCategoryIgnoreCase(String category);
	List<Product> findByCategoryIgnoreCaseAndIsComboTrue(String category);
	List<Product> findTop12ByOrderByIdDesc();
	// Tìm kiếm sản phẩm theo tên hoặc category (chứa keyword)
    List<Product> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(
            String name, String category
    );

    // Lấy gợi ý tên sản phẩm bắt đầu bằng keyword (autocomplete)
    @Query("SELECT DISTINCT p.name FROM Product p " +
    	       "WHERE LOWER(p.name) LIKE LOWER(CONCAT(:keyword, '%'))")
    	List<String> findNameSuggestions(@Param("keyword") String keyword, Pageable pageable);

}
