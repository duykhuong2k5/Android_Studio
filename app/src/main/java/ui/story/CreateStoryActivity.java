package ui.story;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);

        setupStyleRecyclerView();

        RecyclerView rvHeroes = findViewById(R.id.rvHeroes);
        characterAdapter = new CreateCharacterAdapter(characterList);
        rvHeroes.setLayoutManager(new GridLayoutManager(this, 3));
        rvHeroes.setAdapter(characterAdapter);

        ChipGroup chipGroup = findViewById(R.id.chipGroupGenre);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if(!checkedIds.isEmpty()){
                Chip chip = findViewById(checkedIds.get(0));
                // Lấy Text và xóa Emoji để tránh lỗi Server
                selectedTopic = chip.getText().toString().replaceAll("[^a-zA-Z]", "").trim();
            }
        });

        // SỬA TẠI ĐÂY: Hiển thị Popup khi bấm nút Add
        findViewById(R.id.btnAddCharacter).setOnClickListener(v -> showAddCharacterPopup());

        findViewById(R.id.btnGenerate).setOnClickListener(v -> callCreateStoryApi());
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
        com.google.android.material.chip.ChipGroup cgRole = view.findViewById(R.id.cgRole); // Ánh xạ ChipGroup
        Button btnConfirm = view.findViewById(R.id.btnConfirmAdd);
        Button btnCancel = view.findViewById(R.id.dialogBtnCancel);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String age = edtAge.getText().toString().trim();
            String gender = rgGender.getCheckedRadioButtonId() == R.id.rbBoy ? "Boy" : "Girl";

            // Lấy Role từ Chip được chọn
            int selectedChipId = cgRole.getCheckedChipId();
            com.google.android.material.chip.Chip selectedChip = view.findViewById(selectedChipId);
            String role = selectedChip.getText().toString().replaceAll("[^a-zA-Z]", "").trim();
            // Lấy chữ "Hero", "Sidekick"... và bỏ emoji đi để gửi API chuẩn hơn

            if (name.isEmpty() || age.isEmpty()) {
                Toast.makeText(this, "Please fill all info! 🪄", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo nhân vật với trường Role mới
            characterList.add(new CharacterRequest(name, age, gender, role));
            characterAdapter.notifyDataSetChanged();

            dialog.dismiss();
        });

        dialog.show();
    }

    private void setupStyleRecyclerView() {
        RecyclerView rvStyles = findViewById(R.id.rvStyles);
        styleItems.add(new StyleItem("Funny 😂", R.drawable.bg_hero_fantasy));
        styleItems.add(new StyleItem("Brave ⚔️", R.drawable.bg_hero_space));
        styleItems.add(new StyleItem("Magical ✨", R.drawable.bg_magic_button));

        StyleAdapter styleAdapter = new StyleAdapter(styleItems, style -> {
            selectedStyle = style.getName().split(" ")[0];
        });

        rvStyles.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvStyles.setAdapter(styleAdapter);
    }

    private void callCreateStoryApi() {
        if (characterList.isEmpty()) {
            Toast.makeText(this, "Add a character first!", Toast.LENGTH_SHORT).show();
            return;
        }

        CreateStoryRequest req = new CreateStoryRequest();
        req.topic = selectedTopic;
        req.style = selectedStyle;
        req.userId = 1;
        req.characters = characterList;

        RetrofitClient.getInstance().getApi().createStory(req).enqueue(new Callback<ApiResponse<StoryResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<StoryResponse>> call, Response<ApiResponse<StoryResponse>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    Intent intent = new Intent(CreateStoryActivity.this, StoryDetailActivity.class);
                    intent.putExtra("STORY_ID", response.body().data.id);
                    startActivity(intent);
                } else {
                    Toast.makeText(CreateStoryActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<ApiResponse<StoryResponse>> call, Throwable t) {
                Toast.makeText(CreateStoryActivity.this, "Check your Connection/Server!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}