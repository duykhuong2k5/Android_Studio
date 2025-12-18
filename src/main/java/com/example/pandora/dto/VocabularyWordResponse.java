package com.example.pandora.dto;

import com.example.pandora.enums.VocabularyTopic;

public class VocabularyWordResponse {
    private Long id;
    private String word;
    private String phonetic;
    private VocabularyTopic topic;
    private int level;
    private String imageUrl;
    private String audioUrl;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public String getPhonetic() { return phonetic; }
    public void setPhonetic(String phonetic) { this.phonetic = phonetic; }

    public VocabularyTopic getTopic() { return topic; }
    public void setTopic(VocabularyTopic topic) { this.topic = topic; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
}

