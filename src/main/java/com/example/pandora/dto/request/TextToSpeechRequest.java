package com.example.pandora.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextToSpeechRequest {
    private Long storyId;
    private String language; // "en" hoặc "vi"
}
