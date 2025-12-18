package com.example.pandora.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pandora.enums.VocabularyTopic;
import com.example.pandora.model.VocabularyWord;

import java.util.List;

public interface VocabularyWordRepository extends JpaRepository<VocabularyWord, Long> {

    List<VocabularyWord> findByTopic(VocabularyTopic topic);

    List<VocabularyWord> findByTopicAndLevel(VocabularyTopic topic, int level);

    List<VocabularyWord> findByLevel(int level);

    List<VocabularyWord> findByWordContainingIgnoreCase(String keyword);
}

