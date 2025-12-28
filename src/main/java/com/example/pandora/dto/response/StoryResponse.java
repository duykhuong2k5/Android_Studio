package com.example.pandora.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoryResponse {
    private Long id;
    private String titleEn;
    private String contentEn;
    private String titleVi;
    private String contentVi;
    private String thumbnailUrl;
    private List<String> genres;
    private String topic;
    private String style;
    private String audioUrlEn;
    private String audioUrlVi;
    private List<CharacterResponse> characters;
}
