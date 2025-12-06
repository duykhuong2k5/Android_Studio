package com.example.pandora.controller;

import com.example.pandora.dto.FavoriteDTO;
import com.example.pandora.dto.FavoriteRequest;
import com.example.pandora.model.Favorite;
import com.example.pandora.model.Product;
import com.example.pandora.model.User;
import com.example.pandora.repository.FavoriteRepository;
import com.example.pandora.repository.ProductRepository;
import com.example.pandora.repository.UserRepository;
import com.example.pandora.service.FavoriteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getFavorites(@PathVariable Long userId) {
        List<FavoriteDTO> list = favoriteService.getFavoritesByUser(userId);
        if (list.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addFavorite(@RequestBody FavoriteRequest request) {
        Object result = favoriteService.addFavorite(request);
        if (result instanceof String) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFavorite(@PathVariable Long id) {
        boolean deleted = favoriteService.removeFavorite(id);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}
