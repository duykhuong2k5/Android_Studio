package com.example.pandora.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    private final Map<String, String> otpStore = new HashMap<>();
    private final Map<String, LocalDateTime> otpExpiryStore = new HashMap<>();

    public String generateOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStore.put(email, otp);
        otpExpiryStore.put(email, LocalDateTime.now().plusMinutes(5));
        return otp;
    }

    public boolean verifyOtp(String email, String inputOtp) {
        if (!otpStore.containsKey(email)) return false;

        String correctOtp = otpStore.get(email);
        LocalDateTime expiry = otpExpiryStore.get(email);

        if (LocalDateTime.now().isAfter(expiry)) {
            otpStore.remove(email);
            otpExpiryStore.remove(email);
            return false;
        }

        return correctOtp.equals(inputOtp);
    }
}
