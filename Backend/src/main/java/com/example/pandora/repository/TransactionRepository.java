package com.example.pandora.repository;


import com.example.pandora.model.Transaction;
import com.example.pandora.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByVnpTxnRef(String vnpTxnRef);

    Optional<Transaction> findByIdAndUser(Long id, User user);
}

