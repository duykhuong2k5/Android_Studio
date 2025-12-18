package com.example.pandora.model.response;

import java.util.List;

public class DaySessionResponse {
    public Long sessionId;
    public int score;
    public boolean completed;
    public List<DayStepResponse> steps;
}
