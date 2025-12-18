package com.example.pandora.model;

import com.example.pandora.enums.DayStepType;
import com.example.pandora.enums.GameOptionType;
import com.example.pandora.enums.JobType;
import jakarta.persistence.*;

@Entity
@Table(name = "question_templates",
       indexes = {
           @Index(name = "idx_qt_job_step_level", columnList = "job_type, step_type, level")
       })
public class QuestionTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(name = "step_type", nullable = false)
    private DayStepType stepType;

    @Column(nullable = false)
    private int level;

    @Enumerated(EnumType.STRING)
    @Column(name = "option_type", nullable = false)
    private GameOptionType optionType;

    @Column(nullable = false, length = 300)
    private String question;

    @Column(name = "correct_answer", nullable = false, length = 120)
    private String correctAnswer;

    /**
     * Options lưu dạng: "get up|sleep|run"
     */
    @Column(columnDefinition = "TEXT")
    private String options;

    // ===== GETTER & SETTER =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public JobType getJobType() { return jobType; }
    public void setJobType(JobType jobType) { this.jobType = jobType; }

    public DayStepType getStepType() { return stepType; }
    public void setStepType(DayStepType stepType) { this.stepType = stepType; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public GameOptionType getOptionType() { return optionType; }
    public void setOptionType(GameOptionType optionType) { this.optionType = optionType; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }
}
