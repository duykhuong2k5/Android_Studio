package com.example.pandora.ui.user;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.ui.adapter.VideoAdapter;
import com.example.pandora.ui.model.VideoItem;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends AppCompatActivity {

    private PlayerView playerView;
    private ExoPlayer exoPlayer;
    private TextView txtTitle;

    private RecyclerView rvRelatedVideos;

    private long videoId;
    private int videoRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // ===== ÁNH XẠ =====
        txtTitle = findViewById(R.id.txtTitle);
        playerView = findViewById(R.id.playerView);

        // 👉 SAU playerView
        rvRelatedVideos = findViewById(R.id.rvRelatedVideos);
        rvRelatedVideos.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        // ===== NHẬN DATA =====
        videoId = getIntent().getLongExtra("VIDEO_ID", -1);
        videoRes = getIntent().getIntExtra("VIDEO_RES", -1);
        String title = getIntent().getStringExtra("TITLE");

        Log.d("PLAYER", "videoId = " + videoId);

        txtTitle.setText(title != null ? title : "");

        // ===== VIDEO =====
        exoPlayer = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(exoPlayer);

        if (videoRes != -1) {
            playVideoByRes(videoRes);
        }

        // ===== RELATED VIDEOS =====
        loadRelatedVideos();
    }

    // =======================
    // ▶ PHÁT VIDEO
    // =======================
    private void playVideoByRes(int resId) {
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + resId);
        MediaItem mediaItem = MediaItem.fromUri(uri);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.play();
    }

    // Adapter gọi khi click
    public void playVideo(VideoItem item) {
        txtTitle.setText(item.getTitle());
        playVideoByRes(item.getVideoResId());
    }

    // =======================
    // 📺 VIDEO LIÊN QUAN
    // =======================
    private void loadRelatedVideos() {

        List<VideoItem> related = new ArrayList<>();

        related.add(new VideoItem(
                14,
                "Zootopia",
                "Kids Song",
                false,
                "Others",
                R.raw.zootopia,
                R.drawable.zootopia_thumb
        ));

        related.add(new VideoItem(
                4,
                "ABC Song",
                "Kids Song",
                false,
                "Alphabet",
                R.raw.abc,
                R.drawable.abc_thumb
        ));

        related.add(new VideoItem(
                3,
                "Monkey Banana",
                "Kids Song",
                false,
                "Animals",
                R.raw.monkeybanana,
                R.drawable.monkeybanana_thumb
        ));


        rvRelatedVideos.setNestedScrollingEnabled(false);

        rvRelatedVideos.setLayoutManager(
                new LinearLayoutManager(this)
        );

        VideoAdapter adapter = new VideoAdapter(this, related);
        rvRelatedVideos.setAdapter(adapter);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.release();
        }
    }
}
