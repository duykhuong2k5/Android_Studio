package com.example.pandora.controller;

import com.example.pandora.model.User;
import com.example.pandora.repository.UserRepository;
import com.example.pandora.dto.ForgotPasswordRequest;
import com.example.pandora.dto.VerifyOtpRequest;
import com.example.pandora.dto.ResetPasswordRequest;
import com.example.pandora.service.EmailService;
import com.example.pandora.service.OtpService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ===========================
    // GỬI OTP
    // ===========================
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) throws Exception {
        User user = userRepo.findByEmail(request.getEmail());

        if (user == null) {
            return ResponseEntity.status(404).body(
                Map.of("message", "Email không tồn tại!")
            );
        }

        String otp = otpService.generateOtp(request.getEmail());

        emailService.sendOtp(request.getEmail(), otp);

        return ResponseEntity.ok(
            Map.of("message", "OTP đã được gửi!")
        );
    }

    // ===========================
    // XÁC THỰC OTP
    // ===========================
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {

        boolean isValid = otpService.verifyOtp(request.getEmail(), request.getOtp());

        if (!isValid) {
            return ResponseEntity.status(400).body(
                Map.of("message", "OTP không hợp lệ hoặc đã hết hạn!")
            );
        }

        return ResponseEntity.ok(
            Map.of("message", "OTP hợp lệ!")
        );
    }

    // ===========================
    // ĐỔI MẬT KHẨU
    // ===========================
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {

        User user = userRepo.findByEmail(request.getEmail());

        if (user == null) {
            return ResponseEntity.status(404).body(
                Map.of("message", "Email không tồn tại!")
            );
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);

        return ResponseEntity.ok(
            Map.of("message", "Đổi mật khẩu thành công!")
        );
    }
}
