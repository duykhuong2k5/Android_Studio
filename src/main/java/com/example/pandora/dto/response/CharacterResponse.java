package com.example.pandora.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterResponse {
    private Long id;
    private String name;
    private String age;
    private String gender;
    private String role;
    private String imageUrl;
    private String description;
}
