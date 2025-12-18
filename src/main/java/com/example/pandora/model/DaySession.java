package com.example.pandora.model;

import com.example.pandora.enums.JobType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "day_sessions")
public class DaySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private JobType jobType;

    private int score;
    private int correct;
    private int wrong;
    private int stars;

    private boolean completed;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    // ===== GETTER & SETTER =====
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public JobType getJobType() { return jobType; }
    public void setJobType(JobType jobType) { this.jobType = jobType; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getCorrect() { return correct; }
    public void setCorrect(int correct) { this.correct = correct; }

    public int getWrong() { return wrong; }
    public void setWrong(int wrong) { this.wrong = wrong; }

    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }
}
