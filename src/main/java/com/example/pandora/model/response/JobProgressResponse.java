package com.example.pandora.model.response;

import com.example.pandora.enums.JobType;

public class JobProgressResponse {

    private JobType jobType;
    private boolean unlocked;
    private boolean completed;
    private int stars;

    public JobType getJobType() { return jobType; }
    public void setJobType(JobType jobType) { this.jobType = jobType; }

    public boolean isUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }
}
