package com.example.pandora.controller;

import com.example.pandora.model.Voucher;
import com.example.pandora.repository.VoucherRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vouchers")
@CrossOrigin("*")
public class VoucherController {

    @Autowired
    private VoucherRepository voucherRepo;

    // Danh sách voucher (admin)
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(voucherRepo.findAll());
    }

    // Thêm voucher mới
    @PostMapping("/add")
    public ResponseEntity<?> addVoucher(@RequestBody Voucher v) {
        return ResponseEntity.ok(voucherRepo.save(v));
    }

    // Vô hiệu hóa voucher
    @PutMapping("/disable/{id}")
    public ResponseEntity<?> disable(@PathVariable Long id) {
        Voucher v = voucherRepo.findById(id).orElse(null);

        if (v == null)
            return ResponseEntity.notFound().build();

        v.setActive(false);
        voucherRepo.save(v);

        return ResponseEntity.ok("Voucher đã bị vô hiệu hóa");
    }

    @GetMapping("/apply")
    public ResponseEntity<?> applyVoucher(
            @RequestParam String code,
            @RequestParam double total) {

        Voucher v = voucherRepo.findByCode(code);

        if (v == null || !v.isActive()) {
            return ResponseEntity.ok(new ResponseDTO(false, "Voucher không tồn tại!"));
        }

        if (total < v.getMinOrder()) {
            return ResponseEntity.ok(new ResponseDTO(false,
                    "Đơn hàng phải tối thiểu " + (int) v.getMinOrder() + "đ"));
        }

        double discount = 0;


        if (v.getDiscountAmount() != null && v.getDiscountAmount() > 0) {
            discount = v.getDiscountAmount();
        }
        // Nếu không có, dùng % giảm
        else if (v.getDiscountPercent() != null && v.getDiscountPercent() > 0) {
            discount = total * (v.getDiscountPercent() / 100);
        }

        return ResponseEntity.ok(new VoucherResponseDTO(
                true,
                v.getCode(),
                v.getDescription(),
                discount
        ));
    }


    // =========================
    // DTO nội bộ  
    // =========================

    record ResponseDTO(boolean success, String message) {}

    record VoucherResponseDTO(
            boolean success,
            String code,
            String description,
            double discount
    ) {}
}
