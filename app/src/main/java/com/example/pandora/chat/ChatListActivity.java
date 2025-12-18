package com.example.pandora.chat;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pandora.data.network.RetrofitClient;
import com.example.pandora.databinding.ActivityChatListBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListActivity extends AppCompatActivity {

    private ActivityChatListBinding binding;
    private ChatListAdapter adapter;
    private final List<Triple> chatList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new ChatListAdapter(chatList, (productId, senderId, senderName) -> {
            // admin đang đăng nhập
            long adminId = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    .getLong("user_id", -1L);

            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("productId", productId);
            intent.putExtra("currentUserId", adminId);   // người đang chat (admin)
            intent.putExtra("customerId", senderId);     // khách hàng cần support
            intent.putExtra("senderName", senderName);
            intent.putExtra("isAdmin", true);            // đánh dấu là màn admin
            startActivity(intent);
        });

        binding.recyclerChatList.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerChatList.setAdapter(adapter);

        loadChatList();
    }

    private void loadChatList() {
        RetrofitClient.getInstance().getApi().getChatListDetails()
                .enqueue(new Callback<List<Object[]>>() {
                    @Override
                    public void onResponse(Call<List<Object[]>> call,
                                           Response<List<Object[]>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            chatList.clear();
                            for (Object[] item : response.body()) {
                                long productId   = ((Number) item[0]).longValue();
                                long senderId    = ((Number) item[1]).longValue();
                                String senderName = (String) item[2];
                                String productName = (String) item[3];
                                String rawTime     = item[4] != null ? item[4].toString() : "";

                                // format nhanh: "2025-12-06T05:40:33" -> "06/12/2025 12:40"
                                String displayTime = rawTime;
                                if (rawTime != null && rawTime.length() >= 16) {
                                    String date = rawTime.substring(0, 10);       // 2025-12-06
                                    String time = rawTime.substring(11, 16);      // 05:40
                                    String[] parts = date.split("-");
                                    if (parts.length == 3) {
                                        displayTime = parts[2] + "/" + parts[1] + "/" + parts[0] + " " + time;
                                    }
                                }

                                chatList.add(new Triple(productId, senderId, senderName, productName, displayTime));
                            }

                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Object[]>> call, Throwable t) {}
                });
    }

    public static class Triple {
        public final long productId;
        public final long senderId;
        public final String senderName;
        public final String productName;
        public final String lastTime;   // đã format sẵn để hiển thị

        public Triple(long productId,
                      long senderId,
                      String senderName,
                      String productName,
                      String lastTime) {
            this.productId = productId;
            this.senderId = senderId;
            this.senderName = senderName;
            this.productName = productName;
            this.lastTime = lastTime;
        }
    }

}
