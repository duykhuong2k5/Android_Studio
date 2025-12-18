package com.example.pandora.model.response;


import com.example.pandora.model.response.UserProgressResponse;
import com.example.pandora.model.response.JobProgressResponse;

import java.util.List;

public class ProgressResponse {

    private UserProgressResponse userProgress;
    private List<JobProgressResponse> jobs;

    public ProgressResponse(UserProgressResponse userProgress,
                            List<JobProgressResponse> jobs) {
        this.userProgress = userProgress;
        this.jobs = jobs;
    }

    public UserProgressResponse getUserProgress() {
        return userProgress;
    }

    public void setUserProgress(UserProgressResponse userProgress) {
        this.userProgress = userProgress;
    }

    public List<JobProgressResponse> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobProgressResponse> jobs) {
        this.jobs = jobs;
    }
}


