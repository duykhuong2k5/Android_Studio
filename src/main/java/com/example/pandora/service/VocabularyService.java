package com.example.pandora.service;

import org.springframework.stereotype.Service;

import com.example.pandora.dto.VocabularyWordResponse;
import com.example.pandora.enums.VocabularyTopic;
import com.example.pandora.model.VocabularyWord;
import com.example.pandora.repository.VocabularyWordRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class VocabularyService {

    private final VocabularyWordRepository vocabularyWordRepository;

    public VocabularyService(VocabularyWordRepository vocabularyWordRepository) {
        this.vocabularyWordRepository = vocabularyWordRepository;
    }

    public List<VocabularyWordResponse> getVocab(VocabularyTopic topic, Integer level) {
        List<VocabularyWord> words;
        if (topic != null && level != null) {
            words = vocabularyWordRepository.findByTopicAndLevel(topic, level);
        } else if (topic != null) {
            words = vocabularyWordRepository.findByTopic(topic);
        } else if (level != null) {
            words = vocabularyWordRepository.findByLevel(level);
        } else {
            words = vocabularyWordRepository.findAll();
        }

        List<VocabularyWordResponse> res = new ArrayList<>();
        for (VocabularyWord w : words) {
            res.add(toDto(w));
        }
        return res;
    }

    private VocabularyWordResponse toDto(VocabularyWord w) {
        VocabularyWordResponse dto = new VocabularyWordResponse();
        dto.setId(w.getId());
        dto.setWord(w.getWord());
        dto.setPhonetic(w.getPhonetic());
        dto.setTopic(w.getTopic());
        dto.setLevel(w.getLevel());
        dto.setImageUrl(w.getImageUrl());
        dto.setAudioUrl(w.getAudioUrl());
        return dto;
    }
}

