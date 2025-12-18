package com.example.pandora.service;

import com.example.pandora.model.request.TransactionCreateRequest;
import com.example.pandora.model.response.ResponseDTO;

import java.util.Map;

public interface VnpayService {

    /**
     * Tạo URL thanh toán VNPay
     */
    ResponseDTO createPayment(TransactionCreateRequest req);

    /**
     * Xử lý callback /returnUrl từ VNPay
     */
    ResponseDTO handleReturn(Map<String, String> params);
}
