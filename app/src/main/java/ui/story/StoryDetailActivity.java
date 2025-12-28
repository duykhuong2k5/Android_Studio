package ui.story;

import android.os.Bundle;
import android.os.Handler;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pandora.R;
import com.example.pandora.data.entity.*;
import com.example.pandora.data.network.ApiResponse;
import com.example.pandora.data.network.RetrofitClient;

// Import Coil (Xử lý ảnh SVG)
import coil.ComponentRegistry;
import coil.ImageLoader;
import coil.decode.SvgDecoder;
import coil.request.ImageRequest;

// Import Media3 (Xử lý âm thanh)
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import retrofit2.*;

public class StoryDetailActivity extends AppCompatActivity {
    private StoryResponse story;
    private TextView tvTitle, tvContent, btnEnglish, btnVietnamese;
    private ImageView imgStoryCover;

    // Audio components
    private ExoPlayer player;
    private ImageButton btnPlayPause;
    private boolean isEnglishActive = true; // Theo dõi ngôn ngữ đang chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);

        // 1. Ánh xạ View
        tvTitle = findViewById(R.id.tvStoryTitle);
        tvContent = findViewById(R.id.tvStoryContent);
        imgStoryCover = findViewById(R.id.imgStoryCover);
        btnEnglish = findViewById(R.id.btnEnglish);
        btnVietnamese = findViewById(R.id.btnVietnamese);
        btnPlayPause = findViewById(R.id.btnPlayPause);

        // 2. Khởi tạo ExoPlayer
        setupExoPlayer();

        // 3. Lấy ID từ màn hình trước và tải dữ liệu
        int id = getIntent().getIntExtra("STORY_ID", -1);
        if (id != -1) {
            loadStoryDetail(id);
        }

        // 4. Sự kiện đổi ngôn ngữ
        btnEnglish.setOnClickListener(v -> {
            isEnglishActive = true;
            updateLanguageUI();
            resetAudioPlayer(); // Đổi tiếng thì phải reset nhạc để tải link mới
        });

        btnVietnamese.setOnClickListener(v -> {
            isEnglishActive = false;
            updateLanguageUI();
            resetAudioPlayer();
        });

        // 5. Logic nút Play/Pause
        btnPlayPause.setOnClickListener(v -> togglePlayback());
    }

    private void setupExoPlayer() {
        player = new ExoPlayer.Builder(this).build();
        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                // Tự động đổi icon khi trạng thái nhạc thay đổi
                btnPlayPause.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
            }

            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED) {
                    player.seekTo(0);
                    player.pause();
                }
            }
        });
    }

    private void loadStoryDetail(int id) {
        RetrofitClient.getInstance().getApi().getStoryById(id).enqueue(new Callback<ApiResponse<StoryResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<StoryResponse>> call, Response<ApiResponse<StoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    story = response.body().data;

                    // Hiển thị nội dung chữ ngay lập tức
                    updateLanguageUI();

                    // Kiểm tra xem Audio đã sẵn sàng chưa (Polling logic)
                    if (story.audioUrlEn == null || story.audioUrlVi == null) {
                        // Nếu server chưa tạo xong audio, đợi 5 giây rồi tải lại
                        new Handler().postDelayed(() -> loadStoryDetail(id), 5000);
                    }

                    // Hiển thị ảnh bìa
                    if (story.thumbnailUrl != null) {
                        displayImage(story.thumbnailUrl);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<StoryResponse>> call, Throwable t) {
                Toast.makeText(StoryDetailActivity.this, "Network error! 🪄", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateLanguageUI() {
        if (story == null) return;

        if (isEnglishActive) {
            tvTitle.setText(story.titleEn);
            tvContent.setText(story.contentEn);
            btnEnglish.setBackgroundResource(R.drawable.bg_tab_selected);
            btnVietnamese.setBackground(null);
        } else {
            tvTitle.setText(story.titleVi);
            tvContent.setText(story.contentVi);
            btnVietnamese.setBackgroundResource(R.drawable.bg_tab_selected);
            btnEnglish.setBackground(null);
        }
    }

    private void togglePlayback() {
        if (story == null) return;

        String currentAudioUrl = isEnglishActive ? story.audioUrlEn : story.audioUrlVi;

        if (currentAudioUrl == null) {
            Toast.makeText(this, "Magic voice is being prepared... ✨", Toast.LENGTH_SHORT).show();
            return;
        }

        if (player.getMediaItemCount() == 0) {
            MediaItem mediaItem = MediaItem.fromUri(currentAudioUrl);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        } else {
            if (player.isPlaying()) player.pause();
            else player.play();
        }
    }

    private void resetAudioPlayer() {
        if (player != null) {
            player.stop();
            player.clearMediaItems();
        }
    }

    private void displayImage(String url) {
        ComponentRegistry registry = new ComponentRegistry.Builder()
                .add(new SvgDecoder.Factory())
                .build();

        ImageLoader imageLoader = new ImageLoader.Builder(this)
                .components(registry)
                .build();

        ImageRequest request = new ImageRequest.Builder(this)
                .data(url)
                .target(imgStoryCover)
                .crossfade(true)
                .build();

        imageLoader.enqueue(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}