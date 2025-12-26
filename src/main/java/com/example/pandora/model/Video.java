package com.example.pandora.model;

import jakarta.persistence.*;

@Entity
@Table(name = "video")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String topic;

    @Column(name = "is_karaoke")
    private boolean karaoke;

    @Column(columnDefinition = "TEXT")
    private String lyrics;

    // ===== GET / SET =====
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getTopic() {
        return topic;
    }

    public boolean isKaraoke() {
        return karaoke;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setKaraoke(boolean karaoke) {
        this.karaoke = karaoke;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }
}
