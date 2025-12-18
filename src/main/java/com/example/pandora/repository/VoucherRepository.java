package com.example.pandora.repository;

import com.example.pandora.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    Voucher findByCode(String code);
}
	