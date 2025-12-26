package com.example.pandora;

import com.example.pandora.ui.user.MusicActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pandora.R;

public class HomeActivity extends AppCompatActivity {

    private Button btnPlayGame;
    private LinearLayout menuMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnPlayGame = findViewById(R.id.btnPlayGame);
        menuMusic = findViewById(R.id.menuMusic);


        // ✅ MUSIC VIDEO (DÒNG BỊ THIẾU)
        menuMusic.setOnClickListener(v -> {
            startActivity(new Intent(this, MusicActivity.class));
        });
    }
}
