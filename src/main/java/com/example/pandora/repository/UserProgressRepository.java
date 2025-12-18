package com.example.pandora.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pandora.model.UserProgress;

import java.util.Optional;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {

    Optional<UserProgress> findByUser_Id(Long userId);

    boolean existsByUser_Id(Long userId);
}

