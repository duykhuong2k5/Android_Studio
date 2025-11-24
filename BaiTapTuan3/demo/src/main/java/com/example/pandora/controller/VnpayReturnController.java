package com.example.pandora.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pandora.model.response.ResponseDTO;
import com.example.pandora.service.VnpayService;

@RestController
@RequestMapping("/api/payment")
public class VnpayReturnController {

    @Autowired
    private VnpayService vnpayService;

    @GetMapping("/vnpay_return")
    public ResponseDTO handleVnpayReturn(@RequestParam Map<String, String> params) {
        return vnpayService.handleReturn(params);
    }
}
