package com.example.pandora.ui.model;

public class Video {

    private long id;
    private String title;
    private String author;
    private String topic;
    private boolean karaoke;
    private String lyrics;

    public long getId() {
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
}
