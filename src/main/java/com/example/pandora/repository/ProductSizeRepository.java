package com.example.pandora.repository;

import com.example.pandora.model.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {
    List<ProductSize> findByProductId(Long productId);
}
