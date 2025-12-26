package com.example.pandora.data.entity.network;

import com.example.pandora.ui.model.Video;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VideoApi {

    // 📌 Lấy danh sách video theo topic
    @GET("api/videos")
    Call<List<Video>> getVideosByTopic(
            @Query("topic") String topic
    );

    // 📌 Lấy lyrics theo ID
    @GET("api/videos/lyrics/{id}")
    Call<String> getLyricsById(
            @Path("id") long id
    );
}
