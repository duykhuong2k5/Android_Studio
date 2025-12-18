package com.example.pandora.service;

import com.example.pandora.enums.DayStepType;
import com.example.pandora.enums.JobType;
import com.example.pandora.model.QuestionTemplate;
import com.example.pandora.repository.QuestionTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class AiQuestionGeneratorService {

    private final QuestionTemplateRepository repo;
    private final Random random = new Random();

    public AiQuestionGeneratorService(QuestionTemplateRepository repo) {
        this.repo = repo;
    }

    public QuestionTemplate pick(JobType jobType, DayStepType stepType, int level) {
        List<QuestionTemplate> list = repo.findByJobTypeAndStepTypeAndLevel(jobType, stepType, level);
        if (list == null || list.isEmpty()) {
            // fallback mềm: theo stepType & level (không phân job)
            list = repo.findByStepTypeAndLevel(stepType, level);
        }
        if (list == null || list.isEmpty()) {
            throw new IllegalStateException("No question template for " + jobType + " - " + stepType + " level=" + level);
        }
        return list.get(random.nextInt(list.size()));
    }

    public static String[] splitOptions(String options) {
        if (options == null || options.trim().isEmpty()) return new String[0];
        return options.split("\\|");
    }
}
