package com.example.pandora.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pandora.enums.DifficultyLevel;
import com.example.pandora.enums.GameType;
import com.example.pandora.model.GameSession;

import java.util.List;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    List<GameSession> findByUser_IdOrderByStartedAtDesc(Long userId);

    List<GameSession> findByUser_IdAndGameTypeOrderByStartedAtDesc(Long userId, GameType gameType);

    List<GameSession> findByUser_IdAndGameTypeAndLevel(Long userId, GameType gameType, int level);

    List<GameSession> findByUser_IdAndGameTypeAndDifficultyOrderByStartedAtDesc(
            Long userId, GameType gameType, DifficultyLevel difficulty
    );
}


