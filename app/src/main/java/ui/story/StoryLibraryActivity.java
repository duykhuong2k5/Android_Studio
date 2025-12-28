package ui.story;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.StoryResponse;
import com.example.pandora.data.network.ApiResponse;
import com.example.pandora.data.network.RetrofitClient;
import com.example.pandora.ui.adapter.StoryLibraryAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoryLibraryActivity extends AppCompatActivity {
    private RecyclerView rvStoryLibrary;
    private StoryLibraryAdapter adapter;
    private List<StoryResponse> stories = new ArrayList<>();
    private FloatingActionButton fabAddStory; // Khai báo nút mới

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_library);

        // 1. Ánh xạ các View
        rvStoryLibrary = findViewById(R.id.rvStoryLibrary);
        fabAddStory = findViewById(R.id.fabAddStory);

        // 2. Thiết lập hiển thị danh sách (2 cột)
        rvStoryLibrary.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new StoryLibraryAdapter(stories);
        rvStoryLibrary.setAdapter(adapter);

        // 3. Sự kiện bấm nút + để đi tới trang Tạo Truyện
        fabAddStory.setOnClickListener(v -> {
            Intent intent = new Intent(StoryLibraryActivity.this, CreateStoryActivity.class);
            startActivity(intent);
        });

        // 4. Tải danh sách truyện hiện có (Ví dụ user ID = 1)
        loadUserStories(1);
    }

    private void loadUserStories(int userId) {
        RetrofitClient.getInstance().getApi().getStoriesByUserId(userId).enqueue(new Callback<ApiResponse<List<StoryResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<StoryResponse>>> call, Response<ApiResponse<List<StoryResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    stories.clear();
                    stories.addAll(response.body().data);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<StoryResponse>>> call, Throwable t) {
                Toast.makeText(StoryLibraryActivity.this, "Check your internet magic! 🪄", Toast.LENGTH_SHORT).show();
            }
        });
    }
}