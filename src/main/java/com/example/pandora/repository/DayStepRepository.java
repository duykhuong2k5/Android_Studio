package com.example.pandora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.pandora.model.DayStep;

import java.util.List;

public interface DayStepRepository extends JpaRepository<DayStep, Long> {
    List<DayStep> findBySessionId(Long sessionId);
}
