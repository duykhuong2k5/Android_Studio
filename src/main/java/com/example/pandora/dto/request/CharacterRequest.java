package com.example.pandora.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterRequest {
    private String name;
    private String age;
    private String gender; // Boy, Girl
    private String role; // Hero, Friend, Helper
}
