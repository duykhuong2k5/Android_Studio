package com.example.pandora.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pandora.enums.DifficultyLevel;
import com.example.pandora.enums.SceneType;
import com.example.pandora.model.Scene;

import java.util.List;
import java.util.Optional;

public interface SceneRepository extends JpaRepository<Scene, Long> {

    Optional<Scene> findBySceneType(SceneType sceneType);

    List<Scene> findByDifficulty(DifficultyLevel difficulty);

    List<Scene> findByTitleContainingIgnoreCase(String keyword);
}
