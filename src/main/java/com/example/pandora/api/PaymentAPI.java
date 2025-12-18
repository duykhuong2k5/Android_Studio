package com.example.pandora.api;

import com.example.pandora.model.request.TransactionCreateRequest;
import com.example.pandora.model.response.ResponseDTO;
import com.example.pandora.service.VnpayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentAPI {

    @Autowired
    private VnpayService vnpayService;

    /**
     * API tạo thanh toán VNPay
     */
    @PostMapping("/create")
    public ResponseDTO createPayment(@RequestBody TransactionCreateRequest req) {
        return vnpayService.createPayment(req);
    }

    /**
     * Callback VNPay RETURN URL
     * /api/payment/return?vnp_Amount=...&vnp_TxnRef=...
     */
    @GetMapping("/return")
    public ResponseDTO vnpayReturn(HttpServletRequest request) {

        Map<String, String> params = new HashMap<>();

        // Lấy toàn bộ tham số do VNPay gửi về
        request.getParameterMap().forEach((key, value) -> {
            if (value.length > 0) {
                params.put(key, value[0]);
            }
        });

        return vnpayService.handleReturn(params);
    }
}
