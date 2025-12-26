package com.example.pandora.ui.model;

public class VideoItem {

    public long id;        // 👈 ID TRONG DATABASE
    public String title;
    public String author;
    public boolean karaoke;
    public String topic;
    public int videoRes;
    public int thumbRes;

    public VideoItem(long id,
                     String title,
                     String author,
                     boolean karaoke,
                     String topic,
                     int videoRes,
                     int thumbRes) {

        this.id = id;
        this.title = title;
        this.author = author;
        this.karaoke = karaoke;
        this.topic = topic;
        this.videoRes = videoRes;
        this.thumbRes = thumbRes;
    }
    public String getTitle() {
        return title;
    }

    public int getVideoResId() {
        return videoRes;
    }

}
