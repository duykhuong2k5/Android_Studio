package com.example.pandora.data.entity;


import java.util.List;

public class DaySessionResponse {
    public Long sessionId;
    public int score;
    public boolean completed;
    public List<DayStepResponse> steps;
}
