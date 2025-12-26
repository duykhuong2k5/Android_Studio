package com.example.pandora.ui.user;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.ui.adapter.TopicAdapter;
import com.example.pandora.ui.adapter.VideoAdapter;
import com.example.pandora.ui.model.VideoItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MusicActivity extends AppCompatActivity {

    RecyclerView rvTopics, rvFeaturedVideos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        // ===== VIDEO NỔI BẬT =====
        rvFeaturedVideos = findViewById(R.id.rvFeaturedVideos);
        rvFeaturedVideos.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        loadFeaturedVideos();

        // ===== CHỦ ĐỀ =====
        rvTopics = findViewById(R.id.rvTopics);
        rvTopics.setLayoutManager(new GridLayoutManager(this, 2));

        List<String> topics = Arrays.asList(
                "Animals", "Colors", "Alphabet", "Numbers"
        );
        rvTopics.setAdapter(new TopicAdapter(topics, this));
    }

    private void loadFeaturedVideos() {
        List<VideoItem> featured = new ArrayList<>();

        featured.add(new VideoItem(
                4,
                "Wheel on the bus",
                "Kids Song",
                false,
                "Others",
                R.raw.wheelbus,
                R.drawable.wheelbus_thumb
        ));

        featured.add(new VideoItem(
                5,
                "Proud of you",
                "Kids Song",
                false,
                "Others",
                R.raw.proudofyou,
                R.drawable.proudofyou_thumb
        ));

        featured.add(new VideoItem(
                6,
                "Let it go",
                "Kids Song",
                false,
                "Others",
                R.raw.letitgo,
                R.drawable.letitgo_thumb
        ));

        VideoAdapter adapter = new VideoAdapter(this, featured);
        rvFeaturedVideos.setAdapter(adapter);
    }
}
