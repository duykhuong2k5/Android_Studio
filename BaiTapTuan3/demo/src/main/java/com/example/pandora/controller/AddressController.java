package com.example.pandora.controller;

import com.example.pandora.dto.AddressRequest;
import com.example.pandora.model.Address;
import com.example.pandora.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = "*")
public class AddressController {

    @Autowired
    private AddressService addressService;

    // 🔹 Lấy danh sách địa chỉ theo user
    // GET /api/addresses?userId=1
    @GetMapping
    public ResponseEntity<?> getByUser(@RequestParam Long userId) {
        List<Address> list = addressService.listByUser(userId);
        return ResponseEntity.ok(list);
    }

    // 🔹 Thêm địa chỉ mới
    // POST /api/addresses
    @PostMapping
    public ResponseEntity<?> create(@RequestBody AddressRequest req) {
        try {
            Address saved = addressService.create(req);
            return ResponseEntity.ok(Map.of(
                    "message", "Thêm địa chỉ thành công",
                    "id", saved.getId()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );
        }
    }

    // 🔹 Cập nhật địa chỉ
    // PUT /api/addresses/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody AddressRequest req) {
        try {
            Address saved = addressService.update(id, req);
            return ResponseEntity.ok(Map.of(
                    "message", "Cập nhật địa chỉ thành công",
                    "id", saved.getId()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );
        }
    }

    // 🔹 Xóa địa chỉ
    // DELETE /api/addresses/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        addressService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Xóa địa chỉ thành công"));
    }

    // 🔹 Đặt làm mặc định
    // PUT /api/addresses/{id}/default
    @PutMapping("/{id}/default")
    public ResponseEntity<?> setDefault(@PathVariable Long id) {
        try {
            Address a = addressService.setDefault(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Đã đặt địa chỉ mặc định",
                    "id", a.getId()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );
        }
    }
}
