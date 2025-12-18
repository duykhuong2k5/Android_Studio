package com.example.pandora.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.MiniGameItem;
import com.example.pandora.ui.adapter.MiniGameAdapter;

import java.util.ArrayList;
import java.util.List;

public class TomJerryGamesActivity extends AppCompatActivity {

    private MiniGameAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tom_jerry_games);

        RecyclerView rv = findViewById(R.id.rvMiniGames);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MiniGameAdapter();
        rv.setAdapter(adapter);

        adapter.setData(buildGames());
    }

    private List<MiniGameItem> buildGames() {
        List<MiniGameItem> list = new ArrayList<>();

        list.add(new MiniGameItem(
                "MINI GAME #1",
                "Feed Jerry",
                "Jerry is hungry! Match the correct food words to feed him.",
                "Start Game →",
                R.drawable.bg_left_orange,
                R.drawable.ic_game_feed,
                R.color.action_orange,
                FeedJerryActivity.class
        ));

        /*list.add(new MiniGameItem(
                "MINI GAME #2",
                "Catch the Food",
                "Tom is chasing, help Jerry catch the right food!",
                "Start Chase →",
                R.drawable.bg_left_pink,
                R.drawable.ic_game_catch,
                R.color.action_red,
                CatchFoodActivity.class
        ));

        list.add(new MiniGameItem(
                "MINI GAME #3",
                "Hide the Fruit",
                "Find where the fruits are hiding! Learn prepositions.",
                "Start Game →",
                R.drawable.bg_left_green,
                R.drawable.ic_game_hide,
                R.color.action_green,
                HideFruitActivity.class
        ));

        list.add(new MiniGameItem(
                "MINI GAME #4",
                "Cook with Tom",
                "Join Chef Tom, add ingredients and make yummy dishes!",
                "Start Game →",
                R.drawable.bg_left_red,
                R.drawable.ic_game_cook,
                R.color.action_red,
                CookWithTomActivity.class
        ));

        list.add(new MiniGameItem(
                "MINI GAME #5",
                "Dress Jerry",
                "Help Jerry choose his outfit! Learn clothes vocabulary.",
                "Start Game →",
                R.drawable.bg_left_purple,
                R.drawable.ic_game_dress,
                R.color.action_teal,
                DressJerryActivity.class
        ));*/

        return list;
    }
}
