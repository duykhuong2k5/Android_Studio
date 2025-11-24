package com.example.pandora.controller;

import com.example.pandora.dto.LoginRequest;
import com.example.pandora.model.User;
import com.example.pandora.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // ===============================================================
    // 🟢 ĐĂNG KÝ NGƯỜI DÙNG
    // ===============================================================
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {

        if (userRepository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Email đã tồn tại!"));
        }

        user.setRole("ROLE_CUSTOMER");

        // Mã hóa mật khẩu
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "Đăng ký thành công!",
                "email", user.getEmail(),
                "role", user.getRole()
        ));
    }


    // ===============================================================
    // 🟢 ĐĂNG NHẬP
    // ===============================================================
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody LoginRequest request) {

        User existing = userRepository.findByEmail(request.getEmail());

        if (existing == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Tài khoản không tồn tại!"));
        }

        if (!passwordEncoder.matches(request.getPassword(), existing.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Sai mật khẩu!"));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Đăng nhập thành công!",
                "email", existing.getEmail(),
                "fullName", existing.getFullName(),
                "role", existing.getRole()
        ));
    }


    // ===============================================================
    // 🔵 API LẤY DANH SÁCH NGƯỜI DÙNG (CHỈ ADMIN ĐƯỢC TRUY CẬP)
    // ===============================================================
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(@RequestParam("email") String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Không tìm thấy người dùng!"));
        }

        if (!"ROLE_ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Bạn không có quyền xem danh sách người dùng!"));
        }

        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // ===============================================================
    // 🔹 TÌM USER THEO EMAIL
    // ===============================================================
    @GetMapping("/email")
    public ResponseEntity<User> getUserByEmail(@RequestParam("email") String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(user);
    }

    // ===============================================================
    // 🔴 API CHỈ ADMIN ĐƯỢC PHÉP TRUY CẬP
    // ===============================================================
    @GetMapping("/admin-only")
    public ResponseEntity<Map<String, String>> adminOnly(@RequestParam("email") String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Không tìm thấy tài khoản!"));
        }

        if (!"ROLE_ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Bạn không có quyền truy cập vào trang ADMIN!"));
        }

        return ResponseEntity.ok(Map.of("message", "Xin chào ADMIN! Bạn có quyền quản trị."));
    }
 // ✅ Cập nhật thông tin người dùng (Admin hoặc chính chủ)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Không tìm thấy user!"));
        }

        User user = optionalUser.get();
        user.setFullName(updatedUser.getFullName());
        user.setPhone(updatedUser.getPhone());
        user.setAddress(updatedUser.getAddress());
        user.setRole(updatedUser.getRole());
        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    // ❌ Xóa người dùng (chỉ ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Không tìm thấy user!"));
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Xóa user thành công!"));
    }

}
