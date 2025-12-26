package com.example.pandora.data.entity;


import com.example.pandora.data.entity.enums.DifficultyLevel;
import com.example.pandora.data.entity.enums.GameType;

public class GameStartRequest {
    public Long userId;
    public GameType gameType;
    public DifficultyLevel difficulty;
    public int level;

    public GameStartRequest(Long userId, GameType gameType, DifficultyLevel difficulty, int level) {
        this.userId = userId;
        this.gameType = gameType;
        this.difficulty = difficulty;
        this.level = level;
    }
}

