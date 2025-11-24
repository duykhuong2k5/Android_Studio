package com.example.pandora.controller;

import com.example.pandora.dto.FavoriteDTO;
import com.example.pandora.dto.FavoriteRequest;
import com.example.pandora.model.Favorite;
import com.example.pandora.model.Product;
import com.example.pandora.model.User;
import com.example.pandora.repository.FavoriteRepository;
import com.example.pandora.repository.ProductRepository;
import com.example.pandora.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    // ✅ Lấy danh sách yêu thích theo user
    @GetMapping("/{userId}")
    public ResponseEntity<List<FavoriteDTO>> getFavoritesByUser(@PathVariable Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        if (favorites.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<FavoriteDTO> favoriteDTOs = favorites.stream()
                .map(FavoriteDTO::new)
                .toList();

        return ResponseEntity.ok(favoriteDTOs);
    }

    // ✅ Thêm sản phẩm yêu thích
    @PostMapping("/add")
    public ResponseEntity<?> addFavorite(@RequestBody FavoriteRequest request) {
        if (request.getUserId() == null || request.getProductId() == null) {
            return ResponseEntity.badRequest().body("Thiếu userId hoặc productId!");
        }

        Long userId = request.getUserId();
        Long productId = request.getProductId();

        User user = userRepository.findById(userId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);

        if (user == null || product == null) {
            return ResponseEntity.badRequest().body("User hoặc Product không tồn tại!");
        }

        Favorite existing = favoriteRepository.findByUserIdAndProductId(userId, productId);
        if (existing != null) {
            return ResponseEntity.badRequest().body("Sản phẩm đã có trong danh sách yêu thích!");
        }

        Favorite newFav = new Favorite(user, product);
        Favorite saved = favoriteRepository.save(newFav);
        return ResponseEntity.ok(new FavoriteDTO(saved));
    }

    // ✅ Xóa sản phẩm yêu thích theo id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long id) {
        if (!favoriteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        favoriteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
