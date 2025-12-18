package com.example.pandora.model;

import com.example.pandora.enums.JobType;
import jakarta.persistence.*;

@Entity
@Table(
    name = "job_progress",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "job_type"})
)
public class JobProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false)
    private JobType jobType;

    private boolean unlocked;
    private boolean completed;
    private int stars;

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public JobType getJobType() { return jobType; }
    public void setJobType(JobType jobType) { this.jobType = jobType; }

    public boolean isUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }
}
