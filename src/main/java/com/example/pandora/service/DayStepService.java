package com.example.pandora.service;

import com.example.pandora.enums.DayStepType;
import com.example.pandora.enums.GameOptionType;
import com.example.pandora.enums.JobType;
import com.example.pandora.model.DaySession;
import com.example.pandora.model.DayStep;
import com.example.pandora.model.QuestionTemplate;
import com.example.pandora.repository.DayStepRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DayStepService {

    private final DayStepRepository stepRepo;
    private final AiQuestionGeneratorService ai;

    public DayStepService(DayStepRepository stepRepo, AiQuestionGeneratorService ai) {
        this.stepRepo = stepRepo;
        this.ai = ai;
    }

    @Transactional
    public void generateStepsFromAI(DaySession session, int level) {
        JobType job = session.getJobType();

        // 1 ngày cơ bản: 6 bước (bạn có thể thêm/bớt)
        createFromTemplate(session, job, DayStepType.WAKE_UP, level);
        createFromTemplate(session, job, DayStepType.BREAKFAST, level);
        createFromTemplate(session, job, DayStepType.GO_TO_WORK, level);
        createFromTemplate(session, job, DayStepType.DIAGNOSE_TOOL, level);
        createFromTemplate(session, job, DayStepType.DIAGNOSE_DISEASE, level);
        createFromTemplate(session, job, DayStepType.NIGHT_ROUTINE, level);
    }

    private void createFromTemplate(DaySession session, JobType job, DayStepType stepType, int level) {
        QuestionTemplate t = ai.pick(job, stepType, level);

        DayStep s = new DayStep();
        s.setSession(session);
        s.setStepType(t.getStepType());
        s.setOptionType(t.getOptionType() == null ? GameOptionType.WORD : t.getOptionType());
        s.setQuestion(t.getQuestion());
        s.setCorrectAnswer(t.getCorrectAnswer());
        s.setOptions(t.getOptions());
        s.setLevel(level);

        s.setUserAnswer(null);
        s.setCorrect(false);

        stepRepo.save(s);
    }

    @Transactional(readOnly = true)
    public List<DayStep> getSteps(Long sessionId) {
        return stepRepo.findBySessionId(sessionId);
    }

    @Transactional
    public DayStep submit(Long stepId, String answer) {
        DayStep step = stepRepo.findById(stepId)
                .orElseThrow(() -> new IllegalArgumentException("Step not found"));

        step.setUserAnswer(answer);
        step.setCorrect(step.getCorrectAnswer() != null && step.getCorrectAnswer().equalsIgnoreCase(answer));
        return stepRepo.save(step);
    }
}
