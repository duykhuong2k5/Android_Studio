package com.example.pandora.model;

import com.example.pandora.enums.DayStepType;
import com.example.pandora.enums.GameOptionType;
import jakarta.persistence.*;

@Entity
@Table(name = "day_steps")
public class DayStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "session_id")
    private DaySession session;

    @Enumerated(EnumType.STRING)
    private DayStepType stepType;

    @Enumerated(EnumType.STRING)
    private GameOptionType optionType;

    private String question;
    private String correctAnswer;
    private String userAnswer;

    private boolean correct;
 // add inside DayStep
    @Column(columnDefinition = "TEXT")
    private String options;

    private int level;

    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }


    // ===== GETTER & SETTER =====
    public Long getId() { return id; }

    public DaySession getSession() { return session; }
    public void setSession(DaySession session) { this.session = session; }

    public DayStepType getStepType() { return stepType; }
    public void setStepType(DayStepType stepType) { this.stepType = stepType; }

    public GameOptionType getOptionType() { return optionType; }
    public void setOptionType(GameOptionType optionType) { this.optionType = optionType; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }
}
