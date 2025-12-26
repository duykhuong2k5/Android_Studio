package com.example.pandora.data.entity;


import com.example.pandora.data.entity.enums.DifficultyLevel;
import com.example.pandora.data.entity.enums.GameType;

public class GameSessionResponse {
    public long id;
    public long userId;
    public GameType gameType;
    public DifficultyLevel difficulty;
    public int level;
    public int score;
    public int correctCount;
    public int wrongCount;
    public String startedAt;
    public String finishedAt;
}

