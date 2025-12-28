package com.example.pandora.ui;


import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pandora.R;
import com.example.pandora.data.entity.GameSessionResponse;
import com.example.pandora.data.entity.GameStartRequest;
import com.example.pandora.data.entity.VocabularyWordResponse;
import com.example.pandora.data.entity.enums.DifficultyLevel;
import com.example.pandora.data.entity.enums.GameType;
import com.example.pandora.data.network.ApiResponse;
import com.example.pandora.data.network.RetrofitClient;

import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedJerryActivity extends AppCompatActivity {

    private static final long USER_ID = 1;

    private FrameLayout foodLayer;
    private ImageView imgJerry, imgTom;
    private TextView tvTarget;

    private List<VocabularyWordResponse> vocabList;
    private VocabularyWordResponse currentTarget;

    private long sessionId;
    private int score = 0, correct = 0, wrong = 0;

    private final Handler handler = new Handler();
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_jerry);

        foodLayer = findViewById(R.id.foodLayer);
        imgJerry = findViewById(R.id.imgJerry);
        imgTom   = findViewById(R.id.imgTom);
        tvTarget = findViewById(R.id.tvTarget);

        startSession();
        loadVocabFromBackend();
    }

    // ================= BACKEND =================

    private void startSession() {
        GameStartRequest req =
                new GameStartRequest(USER_ID, GameType.FEED_JERRY, DifficultyLevel.EASY, 1);

        RetrofitClient.getInstance().getApi().startGame(req)
                .enqueue(new Callback<ApiResponse<GameSessionResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<GameSessionResponse>> call,
                                           Response<ApiResponse<GameSessionResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().success) {
                            sessionId = response.body().data.id;
                        }
                    }
                    @Override public void onFailure(Call<ApiResponse<GameSessionResponse>> call, Throwable t) {}
                });
    }

    private void loadVocabFromBackend() {
        RetrofitClient.getInstance().getApi()
                .getVocab("FOOD", 1)
                .enqueue(new Callback<ApiResponse<List<VocabularyWordResponse>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<VocabularyWordResponse>>> call,
                                           Response<ApiResponse<List<VocabularyWordResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().success) {
                            vocabList = response.body().data;
                            pickNewTarget();
                            startFoodLoop();
                        }
                    }
                    @Override public void onFailure(Call<ApiResponse<List<VocabularyWordResponse>>> call, Throwable t) {}
                });
    }

    // ================= GAME LOGIC =================

    private void pickNewTarget() {
        currentTarget = vocabList.get(random.nextInt(vocabList.size()));
        tvTarget.setText(currentTarget.safeWordUpper());
    }

    private void startFoodLoop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                spawnFood();
                handler.postDelayed(this, 1600);
            }
        }, 1000);
    }

    private void spawnFood() {
        VocabularyWordResponse vocab =
                vocabList.get(random.nextInt(vocabList.size()));

        ImageView food = new ImageView(this);
        food.setImageResource(vocab.getLocalFoodDrawableRes());

        FrameLayout.LayoutParams lp =
                new FrameLayout.LayoutParams(140, 140);
        lp.leftMargin = -150;
        lp.topMargin = 250 + random.nextInt(600);

        foodLayer.addView(food, lp);

        ObjectAnimator fly =
                ObjectAnimator.ofFloat(food, "translationX", 1200f);
        fly.setDuration(4500);
        fly.start();

        food.setOnTouchListener(new FoodTouchListener(food, vocab, fly));
    }

    // ================= TOUCH & COLLISION =================

    private class FoodTouchListener implements View.OnTouchListener {

        float dX, dY;
        final ImageView food;
        final VocabularyWordResponse vocab;
        final ObjectAnimator anim;

        FoodTouchListener(ImageView food, VocabularyWordResponse vocab, ObjectAnimator anim) {
            this.food = food;
            this.vocab = vocab;
            this.anim = anim;
        }

        @Override
        public boolean onTouch(View v, MotionEvent e) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dX = v.getX() - e.getRawX();
                    dY = v.getY() - e.getRawY();
                    anim.cancel();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    v.setX(e.getRawX() + dX);
                    v.setY(e.getRawY() + dY);

                    if (isNearJerry(v)) {
                        handleDrop(vocab);
                        foodLayer.removeView(food);
                    }
                    return true;
            }
            return false;
        }
    }

    private boolean isNearJerry(View food) {
        int[] f = new int[2];
        int[] j = new int[2];
        food.getLocationOnScreen(f);
        imgJerry.getLocationOnScreen(j);
        return Math.abs(f[0] - j[0]) < 150 && Math.abs(f[1] - j[1]) < 150;
    }

    private void handleDrop(VocabularyWordResponse picked) {
        if (picked.safeWordUpper().equals(currentTarget.safeWordUpper())) {
            correct++;
            score += 20;
            playYummy();
            animateJerry();
            pickNewTarget();
        } else {
            wrong++;
            showTom();
        }
    }

    // ================= EFFECT =================

    private void animateJerry() {
        ObjectAnimator sx = ObjectAnimator.ofFloat(imgJerry, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator sy = ObjectAnimator.ofFloat(imgJerry, "scaleY", 1f, 1.2f, 1f);
        sx.setDuration(350);
        sy.setDuration(350);
        sx.start();
        sy.start();
    }

    private void playYummy() {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.yummy_apple);
        mp.start();
    }

    private void showTom() {
        imgTom.setVisibility(View.VISIBLE);
        handler.postDelayed(() -> imgTom.setVisibility(View.GONE), 1000);
    }
}

