package com.example.pandora.repository;

import com.example.pandora.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {

    Optional<Video> findByTitle(String title);

    List<Video> findByTopic(String topic);

}
