package com.example.pandora;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pandora.ui.FeedJerryActivity;
import com.google.android.material.card.MaterialCardView;

public class HomeActivity extends AppCompatActivity {

    private MaterialCardView cardTomJerry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardTomJerry = findViewById(R.id.cardTomJerry);

        if (cardTomJerry == null) {
            throw new IllegalStateException(
                    "❌ activity_home.xml MUST contain MaterialCardView with id cardTomJerry"
            );
        }

        cardTomJerry.setOnClickListener(v ->
                startActivity(new Intent(this, FeedJerryActivity.class))
        );
    }
}
