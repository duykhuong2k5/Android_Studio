package com.example.pandora.mapper;

import com.example.pandora.model.JobProgress;
import com.example.pandora.model.UserProgress;
import com.example.pandora.model.response.JobProgressResponse;
import com.example.pandora.model.response.UserProgressResponse;

public class ProgressMapper {

    public static UserProgressResponse toUserProgress(UserProgress up) {
        UserProgressResponse r = new UserProgressResponse();
        r.setId(up.getId());
        r.setUserId(up.getUser().getId());
        r.setTotalStars(up.getTotalStars());
        r.setTotalXp(up.getTotalXp());
        r.setStreakDays(up.getStreakDays());
        r.setMasteredWords(up.getMasteredWords());
        return r;
    }

    public static JobProgressResponse toJob(JobProgress jp) {
        JobProgressResponse r = new JobProgressResponse();
        r.setJobType(jp.getJobType());
        r.setUnlocked(jp.isUnlocked());
        r.setCompleted(jp.isCompleted());
        r.setStars(jp.getStars());
        return r;
    }
}
