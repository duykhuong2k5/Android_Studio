package com.example.pandora.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.pandora.enums.JobType;

@Service
public class JobUnlockService {

    private static final List<JobType> ORDER = List.of(
        JobType.DOCTOR,
        JobType.FIREFIGHTER,
        JobType.POLICE,
        JobType.CHEF,
        JobType.TEACHER
    );

    public JobType next(JobType current) {
        int i = ORDER.indexOf(current);
        return (i >= 0 && i + 1 < ORDER.size()) ? ORDER.get(i + 1) : null;
    }
}

