package com.example.pandora.repository;

import com.example.pandora.model.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findByUserId(Long userId);
    List<Story> findByTopic(String topic);
    List<Story> findByStyle(String style);
    List<Story> findByGenresContaining(String genre);
}
