package com.example.pandora.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "story_characters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoryCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "age")
    private String age;

    @Column(name = "gender")
    private String gender; // Boy, Girl

    @Column(name = "role")
    private String role; // Hero, Villain, Friend, etc.

    @Column(name = "image_url")
    private String imageUrl; // URL hình ảnh nhân vật do AI generate

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // Mô tả chi tiết nhân vật cho AI

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    private Story story;
}
