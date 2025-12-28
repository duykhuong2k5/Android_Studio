package com.example.pandora.repository;

import com.example.pandora.model.StoryCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryCharacterRepository extends JpaRepository<StoryCharacter, Long> {
    List<StoryCharacter> findByStoryId(Long storyId);
}
