package com.example.pandora.controller;

import com.example.pandora.model.Video;
import com.example.pandora.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "*")
public class VideoController {

    @Autowired
    private VideoRepository videoRepository;

    // 📌 Lấy danh sách video theo chủ đề (Animals)
    @GetMapping
    public List<Video> getByTopic(@RequestParam String topic) {
        return videoRepository.findByTopic(topic);
    }

    // 📌 Lấy lyrics theo title
    @GetMapping("/lyrics/by-title")
    public String getLyricsByTitle(@RequestParam String title) {
        return videoRepository.findByTitle(title)
                .map(Video::getLyrics)
                .orElse("");
    }

    
    @GetMapping("/lyrics/{id}")
    public String getLyricsById(@PathVariable Long id) {
        return videoRepository.findById(id)
                .map(Video::getLyrics)
                .orElse("");
    }


    // 📌 Admin thêm video + lyrics
    @PostMapping
    public Video addVideo(@RequestBody Video video) {
        return videoRepository.save(video);
    }
}
