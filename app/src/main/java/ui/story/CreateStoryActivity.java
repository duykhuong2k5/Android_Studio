package ui.story;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.*;
import com.example.pandora.data.network.ApiResponse;
import com.example.pandora.data.network.RetrofitClient;
import com.example.pandora.ui.adapter.CreateCharacterAdapter;
import com.example.pandora.ui.adapter.StyleAdapter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import retrofit2.*;

public class CreateStoryActivity extends AppCompatActivity {

    private List<CharacterRequest> characterList = new ArrayList<>();
    private List<StyleItem> styleItems = new ArrayList<>();
    private CreateCharacterAdapter characterAdapter;
    private String selectedTopic = "Animals";
    private String selectedStyle = "Funny";

    // Khai báo Dialog Loading toàn cục
    private AlertDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);

        // 1. Cấu hình 10 Phong cách (Styles)
        setupStyleRecyclerView();

        // 2. Cấu hình danh sách nhân vật (Heroes)
        RecyclerView rvHeroes = findViewById(R.id.rvHeroes);
        characterAdapter = new CreateCharacterAdapter(characterList);
        rvHeroes.setLayoutManager(new GridLayoutManager(this, 3));
        rvHeroes.setAdapter(characterAdapter);

        // 3. Logic chọn 10 Chủ đề (Topics)
        ChipGroup chipGroup = findViewById(R.id.chipGroupGenre);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if(!checkedIds.isEmpty()){
                Chip chip = findViewById(checkedIds.get(0));
                // Lọc bỏ Emoji để gửi chuỗi text sạch lên Server
                selectedTopic = chip.getText().toString().replaceAll("[^a-zA-Z]", "").trim();
            }
        });

        // 4. Mở Popup thêm nhân vật
        findViewById(R.id.btnAddCharacter).setOnClickListener(v -> showAddCharacterPopup());

        // 5. Nút bấm tạo truyện (Có hiện màn hình chờ)
        findViewById(R.id.btnGenerate).setOnClickListener(v -> {
            if (characterList.isEmpty()) {
                Toast.makeText(this, "Add a hero to your story! 🦸‍♂️", Toast.LENGTH_SHORT).show();
                return;
            }
            showLoadingScreen(); // Hiện màn hình loading
            callCreateStoryApi(); // Gọi API tạo truyện
        });
    }

    // Hàm hiển thị màn hình Loading "Phép thuật"
    private void showLoadingScreen() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Sử dụng layout custom cho loading
        View view = getLayoutInflater().inflate(R.layout.dialog_loading, null);
        builder.setView(view);
        builder.setCancelable(false); // Bé không thể bấm ra ngoài để tắt khi đang tạo

        loadingDialog = builder.create();
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        loadingDialog.show();
    }

    private void callCreateStoryApi() {
        CreateStoryRequest req = new CreateStoryRequest();
        req.topic = selectedTopic;
        req.style = selectedStyle;
        req.userId = 1;
        req.characters = characterList;

        RetrofitClient.getInstance().getApi().createStory(req).enqueue(new Callback<ApiResponse<StoryResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<StoryResponse>> call, Response<ApiResponse<StoryResponse>> response) {
                // Tắt màn hình Loading ngay khi có phản hồi
                if (loadingDialog != null) loadingDialog.dismiss();

                if(response.isSuccessful() && response.body() != null) {
                    Intent intent = new Intent(CreateStoryActivity.this, StoryDetailActivity.class);
                    intent.putExtra("STORY_ID", response.body().data.id);
                    startActivity(intent);
                    finish(); // Kết thúc màn hình Create để bé không quay lại tạo trùng
                } else {
                    Toast.makeText(CreateStoryActivity.this, "The magic failed! Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override public void onFailure(Call<ApiResponse<StoryResponse>> call, Throwable t) {
                if (loadingDialog != null) loadingDialog.dismiss();
                Toast.makeText(CreateStoryActivity.this, "Server is resting... 😴 Try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupStyleRecyclerView() {
        RecyclerView rvStyles = findViewById(R.id.rvStyles);
        styleItems.clear();
        styleItems.add(new StyleItem("Funny 😂", R.drawable.bg_hero_fantasy));
        styleItems.add(new StyleItem("Magical ✨", R.drawable.bg_magic_button));
        styleItems.add(new StyleItem("Brave ⚔️", R.drawable.bg_hero_space));
        styleItems.add(new StyleItem("Dreamy 🌙", R.drawable.bg_hero_fantasy));
        styleItems.add(new StyleItem("Mystery 🔍", R.drawable.bg_hero_space));
        styleItems.add(new StyleItem("Comic 🗯️", R.drawable.bg_magic_button));
        styleItems.add(new StyleItem("Watercolor 🎨", R.drawable.bg_hero_fantasy));
        styleItems.add(new StyleItem("Pixel Art 👾", R.drawable.bg_hero_space));
        styleItems.add(new StyleItem("Neon 🌈", R.drawable.bg_magic_button));
        styleItems.add(new StyleItem("Sketch 📝", R.drawable.bg_hero_fantasy));

        StyleAdapter styleAdapter = new StyleAdapter(styleItems, style -> {
            selectedStyle = style.getName().replaceAll("[^a-zA-Z]", "").trim();
        });

        rvStyles.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvStyles.setAdapter(styleAdapter);
    }

    private void showAddCharacterPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_character, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        EditText edtName = view.findViewById(R.id.edtName);
        EditText edtAge = view.findViewById(R.id.edtAge);
        RadioGroup rgGender = view.findViewById(R.id.rgGender);
        ChipGroup cgRole = view.findViewById(R.id.cgRole);
        Button btnConfirm = view.findViewById(R.id.btnConfirmAdd);

        btnConfirm.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String age = edtAge.getText().toString().trim();
            String gender = rgGender.getCheckedRadioButtonId() == R.id.rbBoy ? "Boy" : "Girl";

            int selectedChipId = cgRole.getCheckedChipId();
            if (selectedChipId == View.NO_ID) {
                Toast.makeText(this, "Pick a role! 🎭", Toast.LENGTH_SHORT).show();
                return;
            }

            Chip selectedChip = view.findViewById(selectedChipId);
            String role = selectedChip.getText().toString().replaceAll("[^a-zA-Z]", "").trim();

            if (name.isEmpty() || age.isEmpty()) {
                Toast.makeText(this, "Tell us more! 🪄", Toast.LENGTH_SHORT).show();
                return;
            }

            characterList.add(new CharacterRequest(name, age, gender, role));
            characterAdapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        dialog.show();
    }
}