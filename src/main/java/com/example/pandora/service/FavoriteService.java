package com.example.pandora.service;

import com.example.pandora.dto.FavoriteDTO;
import com.example.pandora.dto.FavoriteRequest;
import com.example.pandora.model.Favorite;
import com.example.pandora.model.Product;
import com.example.pandora.model.User;
import com.example.pandora.repository.FavoriteRepository;
import com.example.pandora.repository.ProductRepository;
import com.example.pandora.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    // ⭐ Lấy danh sách yêu thích theo user
    public List<FavoriteDTO> getFavoritesByUser(Long userId) {
        return favoriteRepository.findByUserId(userId)
                .stream()
                .map(FavoriteDTO::new)
                .toList();
    }

    // ⭐ Thêm sản phẩm yêu thích
    public Object addFavorite(FavoriteRequest request) {

        if (request.getUserId() == null || request.getProductId() == null) {
            return "Thiếu userId hoặc productId!";
        }

        User user = userRepository.findById(request.getUserId()).orElse(null);
        Product product = productRepository.findById(request.getProductId()).orElse(null);

        if (user == null || product == null) {
            return "User hoặc Product không tồn tại!";
        }

        Favorite existing = favoriteRepository
                .findByUserIdAndProductId(request.getUserId(), request.getProductId());

        if (existing != null) {
            return "Sản phẩm đã có trong danh sách yêu thích!";
        }

        Favorite saved = favoriteRepository.save(new Favorite(user, product));
        return new FavoriteDTO(saved);
    }

    // ⭐ Xóa khỏi yêu thích
    public boolean removeFavorite(Long id) {
        if (!favoriteRepository.existsById(id)) {
            return false;
        }

        favoriteRepository.deleteById(id);
        return true;
    }
}
