package com.example.pandora.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pandora.enums.JobType;
import com.example.pandora.model.JobProgress;

public interface JobProgressRepository extends JpaRepository<JobProgress, Long> {
	Optional<JobProgress> findByUser_IdAndJobType(Long userId, JobType jobType);
	List<JobProgress> findByUser_Id(Long userId);

}
