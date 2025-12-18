package com.example.pandora.model.request;

import com.example.pandora.enums.DifficultyLevel;
import com.example.pandora.enums.GameType;

public class GameStartRequest {
    private Long userId;
    private GameType gameType;
    private DifficultyLevel difficulty;
    private int level;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public GameType getGameType() { return gameType; }
    public void setGameType(GameType gameType) { this.gameType = gameType; }

    public DifficultyLevel getDifficulty() { return difficulty; }
    public void setDifficulty(DifficultyLevel difficulty) { this.difficulty = difficulty; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
}

