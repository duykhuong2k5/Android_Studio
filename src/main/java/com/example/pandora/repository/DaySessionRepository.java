package com.example.pandora.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pandora.enums.JobType;
import com.example.pandora.model.DaySession;

public interface DaySessionRepository extends JpaRepository<DaySession, Long> {
	Optional<DaySession> findByIdAndUser_Id(Long id, Long userId);
	Optional<DaySession>
    findFirstByUser_IdAndJobTypeAndCompletedFalseOrderByStartedAtDesc(
            Long userId,
            JobType jobType
    );
}

