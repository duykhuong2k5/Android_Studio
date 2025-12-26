package com.example.pandora.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title_en", nullable = false)
    private String titleEn;

    @Column(name = "content_en", columnDefinition = "TEXT", nullable = false)
    private String contentEn;

    @Column(name = "title_vi", nullable = false)
    private String titleVi;

    @Column(name = "content_vi", columnDefinition = "TEXT", nullable = false)
    private String contentVi;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @ElementCollection
    @CollectionTable(name = "story_genres", joinColumns = @JoinColumn(name = "story_id"))
    @Column(name = "genre")
    private List<String> genres = new ArrayList<>();

    @Column(name = "topic")
    private String topic; // Animals, School, Space, etc.

    @Column(name = "style")
    private String style; // Funny, Brave, Adventure, etc.

    @Column(name = "audio_url_en")
    private String audioUrlEn; // URL cho file audio tiếng Anh

    @Column(name = "audio_url_vi")
    private String audioUrlVi; // URL cho file audio tiếng Việt

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoryCharacter> characters = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
