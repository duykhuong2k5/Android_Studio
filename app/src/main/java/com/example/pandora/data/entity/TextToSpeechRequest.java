package com.example.pandora.data.entity;

public class TextToSpeechRequest {
    public int storyId;
    public String language; // "en" hoặc "vi"

    public TextToSpeechRequest(int storyId, String language) {
        this.storyId = storyId;
        this.language = language;
    }
}