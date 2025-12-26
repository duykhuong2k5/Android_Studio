package com.example.pandora.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStoryRequest {
    private String topic; // Animals, School, Space, Fantasy
    private String style; // Funny, Brave, Adventure, Educational
    private List<CharacterRequest> characters;
    private Long userId; // ID người dùng tạo truyện
}
