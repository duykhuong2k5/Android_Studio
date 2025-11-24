package com.example.pandora.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendOtp(String to, String otp) throws MessagingException;
}
