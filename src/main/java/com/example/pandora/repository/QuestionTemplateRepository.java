package com.example.pandora.repository;

import com.example.pandora.enums.DayStepType;
import com.example.pandora.enums.JobType;
import com.example.pandora.model.QuestionTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionTemplateRepository extends JpaRepository<QuestionTemplate, Long> {
    List<QuestionTemplate> findByJobTypeAndStepTypeAndLevel(JobType jobType, DayStepType stepType, int level);

    // fallback mềm: không có job thì lấy theo step/level
    List<QuestionTemplate> findByStepTypeAndLevel(DayStepType stepType, int level);
}
