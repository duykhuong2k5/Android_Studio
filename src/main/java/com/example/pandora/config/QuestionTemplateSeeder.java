package com.example.pandora.config;

import com.example.pandora.enums.DayStepType;
import com.example.pandora.enums.GameOptionType;
import com.example.pandora.enums.JobType;
import com.example.pandora.model.QuestionTemplate;
import com.example.pandora.repository.QuestionTemplateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class QuestionTemplateSeeder implements CommandLineRunner {

    private final QuestionTemplateRepository repo;

    public QuestionTemplateSeeder(QuestionTemplateRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;

        // ===== DOCTOR - LEVEL 1 =====
        save(JobType.DOCTOR, DayStepType.WAKE_UP, 1, GameOptionType.ACTION,
                "When you wake up, what do you do?",
                "get up",
                "get up|sleep|cry");

        save(JobType.DOCTOR, DayStepType.BREAKFAST, 1, GameOptionType.FOOD,
                "What do you eat for breakfast?",
                "bread",
                "bread|soap|shoe");

        save(JobType.DOCTOR, DayStepType.GO_TO_WORK, 1, GameOptionType.ACTION,
                "A doctor goes to the hospital. What do you do next?",
                "go to work",
                "go to work|go to bed|go swimming");

        save(JobType.DOCTOR, DayStepType.DIAGNOSE_TOOL, 1, GameOptionType.TOOL,
                "Which tool checks body temperature?",
                "thermometer",
                "thermometer|pan|pencil");

        save(JobType.DOCTOR, DayStepType.DIAGNOSE_DISEASE, 1, GameOptionType.DISEASE,
                "He has a high temperature. What is the problem?",
                "fever",
                "fever|hungry|happy");

        save(JobType.DOCTOR, DayStepType.NIGHT_ROUTINE, 1, GameOptionType.ACTION,
                "Before sleeping, what should you do?",
                "brush teeth",
                "brush teeth|jump|run");

        // ===== CHEF - LEVEL 1 =====
        save(JobType.CHEF, DayStepType.WAKE_UP, 1, GameOptionType.ACTION,
                "A chef starts a day. What do you do after waking up?",
                "get up",
                "get up|sleep|hide");

        save(JobType.CHEF, DayStepType.COOK_DINNER, 1, GameOptionType.TOOL,
                "Which tool helps you cook dinner?",
                "pan",
                "pan|stethoscope|helmet");

        save(JobType.CHEF, DayStepType.NIGHT_ROUTINE, 1, GameOptionType.ACTION,
                "After dinner, what do you do before bed?",
                "brush teeth",
                "brush teeth|eat more|dance");
    }

    private void save(JobType job, DayStepType step, int level, GameOptionType type,
                      String q, String correct, String options) {
        QuestionTemplate t = new QuestionTemplate();
        t.setJobType(job);
        t.setStepType(step);
        t.setLevel(level);
        t.setOptionType(type);
        t.setQuestion(q);
        t.setCorrectAnswer(correct);
        t.setOptions(options);
        repo.save(t);
    }
}
