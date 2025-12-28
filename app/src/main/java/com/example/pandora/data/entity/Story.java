package com.example.pandora.data.entity;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Story implements Serializable {

    @SerializedName("id")
    private long id;

    // --- Tiếng Anh ---
    @SerializedName("title_en")
    private String titleEn;

    @SerializedName("content_en")
    private String contentEn;

    // --- Tiếng Việt ---
    @SerializedName("title_vi")
    private String titleVi;

    @SerializedName("content_vi")
    private String contentVi;

    @SerializedName("thumbnail_url")
    private String thumbnailUrl;

    @SerializedName("genres")
    private List<String> genres;

    public Story() {}

    // Getter và Setter cho Tiếng Anh
    public String getTitleEn() { return titleEn; }
    public void setTitleEn(String titleEn) { this.titleEn = titleEn; }

    public String getContentEn() { return contentEn; }
    public void setContentEn(String contentEn) { this.contentEn = contentEn; }

    // Getter và Setter cho Tiếng Việt
    public String getTitleVi() { return titleVi; }
    public void setTitleVi(String titleVi) { this.titleVi = titleVi; }

    public String getContentVi() { return contentVi; }
    public void setContentVi(String contentVi) { this.contentVi = contentVi; }

    // Các Getter/Setter khác
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }
}