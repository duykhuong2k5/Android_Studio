package com.example.pandora.service;

import org.springframework.stereotype.Service;

import com.example.pandora.model.GameSession;
import com.example.pandora.model.User;
import com.example.pandora.model.UserProgress;
import com.example.pandora.model.request.GameFinishRequest;
import com.example.pandora.model.request.GameStartRequest;
import com.example.pandora.model.response.GameSessionResponse;
import com.example.pandora.repository.GameSessionRepository;
import com.example.pandora.repository.UserProgressRepository;
import com.example.pandora.repository.UserRepository;

import java.time.LocalDateTime;

@Service
public class GameSessionService {

    private final GameSessionRepository gameSessionRepository;
    private final UserRepository userRepository;
    private final UserProgressRepository userProgressRepository;

    public GameSessionService(GameSessionRepository gameSessionRepository,
                              UserRepository userRepository,
                              UserProgressRepository userProgressRepository) {
        this.gameSessionRepository = gameSessionRepository;
        this.userRepository = userRepository;
        this.userProgressRepository = userProgressRepository;
    }

    public GameSessionResponse start(GameStartRequest req) {
        if (req.getUserId() == null) throw new IllegalArgumentException("userId is required");
        if (req.getGameType() == null) throw new IllegalArgumentException("gameType is required");
        if (req.getDifficulty() == null) throw new IllegalArgumentException("difficulty is required");

        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        GameSession session = new GameSession();
        session.setUser(user);
        session.setGameType(req.getGameType());
        session.setDifficulty(req.getDifficulty());
        session.setLevel(req.getLevel());
        session.setScore(0);
        session.setCorrectCount(0);
        session.setWrongCount(0);
        session.setStartedAt(LocalDateTime.now());

        GameSession saved = gameSessionRepository.save(session);
        return toDto(saved);
    }

    public GameSessionResponse finish(Long sessionId, GameFinishRequest req) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("GameSession not found"));

        session.setScore(req.getScore());
        session.setCorrectCount(req.getCorrectCount());
        session.setWrongCount(req.getWrongCount());
        session.setFinishedAt(LocalDateTime.now());

        GameSession saved = gameSessionRepository.save(session);

        // Update progress (stars + xp)
        int stars = Math.max(0, req.getEarnedStars());
        int xp = Math.max(0, req.getScore()); // đơn giản: xp = score

        upsertProgress(saved.getUser(), stars, xp);

        return toDto(saved);
    }

    private void upsertProgress(User user, int addStars, int addXp) {
        UserProgress progress = userProgressRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    UserProgress p = new UserProgress();
                    p.setUser(user);
                    p.setTotalStars(0);
                    p.setTotalXp(0);
                    p.setStreakDays(0);
                    p.setMasteredWords(0);
                    return p;
                });

        progress.setTotalStars(progress.getTotalStars() + addStars);
        progress.setTotalXp(progress.getTotalXp() + addXp);

        userProgressRepository.save(progress);
    }

    private GameSessionResponse toDto(GameSession s) {
        GameSessionResponse dto = new GameSessionResponse();
        dto.setId(s.getId());
        dto.setUserId(s.getUser().getId());
        dto.setGameType(s.getGameType());
        dto.setDifficulty(s.getDifficulty());
        dto.setLevel(s.getLevel());
        dto.setScore(s.getScore());
        dto.setCorrectCount(s.getCorrectCount());
        dto.setWrongCount(s.getWrongCount());
        dto.setStartedAt(s.getStartedAt());
        dto.setFinishedAt(s.getFinishedAt());
        return dto;
    }
}

