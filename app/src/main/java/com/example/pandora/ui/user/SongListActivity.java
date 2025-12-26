package com.example.pandora.ui.user;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.ui.adapter.VideoAdapter;
import com.example.pandora.ui.model.VideoItem;

import java.util.ArrayList;
import java.util.List;

public class SongListActivity extends AppCompatActivity {

    RecyclerView rvVideos;
    VideoAdapter adapter;

    Button btnVideo, btnKaraoke;
    TextView txtTopicTitle;

    List<VideoItem> allVideos = new ArrayList<>();
    List<VideoItem> filtered = new ArrayList<>();

    String topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        topic = getIntent().getStringExtra("TOPIC_NAME");

        txtTopicTitle = findViewById(R.id.txtTopicTitle);
        btnVideo = findViewById(R.id.btnVideo);
        btnKaraoke = findViewById(R.id.btnKaraoke);
        rvVideos = findViewById(R.id.rvVideos);

        txtTopicTitle.setText(topic);
        rvVideos.setLayoutManager(new LinearLayoutManager(this));

        loadVideos();
        filter(false);

        btnVideo.setOnClickListener(v -> filter(false));
        btnKaraoke.setOnClickListener(v -> filter(true));
    }

    private void loadVideos() {
        allVideos.clear();

        // ===== ANIMALS =====
        allVideos.add(new VideoItem(
                1,
                "See You",
                "Kids Song",
                false,
                "Animals",
                R.raw.seeyou,
                R.drawable.seeyou_thumb
        ));

        allVideos.add(new VideoItem(
                2,
                "Baby Shark",
                "Pinkfong",
                false,
                "Animals",
                R.raw.babyshark,
                R.drawable.babyshark_thumb
        ));

        allVideos.add(new VideoItem(
                3,
                "Monkey Banana",
                "Kids Karaoke",
                true,
                "Animals",
                R.raw.monkeybanana,
                R.drawable.monkeybanana_thumb
        ));

        // ===== ALPHABET =====
        allVideos.add(new VideoItem(
                4,
                "ABC Song",
                "Kids Song",
                false,
                "Alphabet",
                R.raw.abc,
                R.drawable.abc_thumb
        ));

        allVideos.add(new VideoItem(
                5,
                "Alphabet Song",
                "Kids Song",
                false,
                "Alphabet",
                R.raw.alphabet,
                R.drawable.alphabet_thumb
        ));

        allVideos.add(new VideoItem(
                6,
                "ABC Karaoke",
                "Kids Karaoke",
                true,
                "Alphabet",
                R.raw.kara_abc,
                R.drawable.abc_thumb
        ));

        // ===== COLORS (ALL SONG) =====
        allVideos.add(new VideoItem(
                7,
                "Bé Học Màu Sắc",
                "Kids Song",
                false,
                "Colors",
                R.raw.behocmausac,
                R.drawable.behocmausac_thumb
        ));

        allVideos.add(new VideoItem(
                8,
                "Colors Song",
                "Kids Song",
                false,
                "Colors",
                R.raw.colors,
                R.drawable.colorsong_thumb
        ));

        allVideos.add(new VideoItem(
                9,
                "Color Song",
                "Kids Song",
                false,
                "Colors",
                R.raw.colorsong,
                R.drawable.colors_thumb
        ));

        // ===== NUMBERS (ALL SONG) =====
        allVideos.add(new VideoItem(
                7,
                "Bé Học Đếm Số",
                "Kids Song",
                false,
                "Numbers",
                R.raw.numbers,
                R.drawable.numbers_thumb
        ));
    }

    private void filter(boolean karaoke) {
        filtered.clear();
        for (VideoItem v : allVideos) {
            if (v.topic.equals(topic) && v.karaoke == karaoke) {
                filtered.add(v);
            }
        }
        adapter = new VideoAdapter(this, filtered);
        rvVideos.setAdapter(adapter);
    }
}
