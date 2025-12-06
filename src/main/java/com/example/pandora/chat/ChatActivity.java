package com.example.pandora.chat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pandora.data.entity.ChatMessage;
import com.example.pandora.data.network.RetrofitClient;
import com.example.pandora.databinding.ActivityChatBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private ChatAdapter adapter;
    private final List<ChatMessage> messages = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private long currentUserId = 0;   // user đang đăng nhập trên thiết bị
    private long otherUserId = 0;     // user phía bên kia (admin hoặc khách)
    private long adminId = 0;         // id admin (để gửi tin đúng)
    private long productId = 0;
    private boolean isAdmin = false;  // true = màn admin, false = khách

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ======= Nhận dữ liệu từ Intent =======
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        currentUserId = getIntent().getLongExtra("currentUserId", 0);
        productId = getIntent().getLongExtra("productId", 0);

        if (isAdmin) {
            // Admin đang xem chat với 1 khách hàng cụ thể
            long customerId = getIntent().getLongExtra("customerId", 0);
            otherUserId = customerId;
            adminId = currentUserId;  // trên thiết bị admin
        }

        // ======= Setup RecyclerView =======
        adapter = new ChatAdapter(this, messages, currentUserId);
        binding.recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerChat.setAdapter(adapter);

        // ======= Gửi tin nhắn =======
        binding.btnSend.setOnClickListener(v -> {
            String msgText = binding.edtMessage.getText().toString().trim();
            if (msgText.isEmpty()) return;

            // Xác định người nhận
            long receiverId;
            if (isAdmin) {
                // Admin gửi cho khách
                receiverId = otherUserId;
            } else {
                // Khách gửi cho admin
                if (adminId == 0) {
                    Toast.makeText(this, "Chưa tải được thông tin admin", Toast.LENGTH_SHORT).show();
                    return;
                }
                receiverId = adminId;
            }

            ChatMessage message = new ChatMessage(
                    null,
                    currentUserId,
                    receiverId,
                    productId,
                    msgText,
                    null
            );
            sendMessage(message);
            binding.edtMessage.setText("");
        });

        // ======= Logic load lần đầu =======
        if (isAdmin) {
            // Admin không cần auto-start
            loadMessages();
        } else {
            // Khách: lấy adminId + auto-start chat
            fetchAdminAndAutoStart();
        }

        // ======= Auto refresh mỗi 3s =======
        handler.post(new Runnable() {
            @Override
            public void run() {
                loadMessages();
                handler.postDelayed(this, 3000);
            }
        });
    }

    // Lấy adminId rồi auto-start chat cho khách
    private void fetchAdminAndAutoStart() {
        RetrofitClient.getInstance().getApi().getAdminId()
                .enqueue(new Callback<Long>() {
                    @Override
                    public void onResponse(Call<Long> call, Response<Long> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            adminId = response.body();
                            otherUserId = adminId;

                            // Gọi auto-start: tạo tin nhắn chào nếu chưa có hội thoại
                            RetrofitClient.getInstance().getApi()
                                    .autoStartChat(currentUserId, productId)
                                    .enqueue(new Callback<ChatMessage>() {
                                        @Override
                                        public void onResponse(Call<ChatMessage> call,
                                                               Response<ChatMessage> response) {
                                            // Dù có tạo hay không (backend tự kiểm tra) thì vẫn load tin nhắn
                                            loadMessages();
                                        }

                                        @Override
                                        public void onFailure(Call<ChatMessage> call, Throwable t) {
                                            loadMessages();
                                        }
                                    });

                        } else {
                            Toast.makeText(ChatActivity.this,
                                    "Không lấy được thông tin admin", Toast.LENGTH_SHORT).show();
                            loadMessages();
                        }
                    }

                    @Override
                    public void onFailure(Call<Long> call, Throwable t) {
                        Toast.makeText(ChatActivity.this,
                                "Không lấy được thông tin admin", Toast.LENGTH_SHORT).show();
                        loadMessages();
                    }
                });
    }

    // Load toàn bộ tin nhắn của sản phẩm (cả admin + khách)
    private void loadMessages() {
        if (productId == 0) return;

        RetrofitClient.getInstance().getApi().getMessagesByProduct(productId)
                .enqueue(new Callback<List<ChatMessage>>() {
                    @Override
                    public void onResponse(Call<List<ChatMessage>> call,
                                           Response<List<ChatMessage>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            messages.clear();
                            messages.addAll(response.body());
                            adapter.notifyDataSetChanged();
                            if (!messages.isEmpty()) {
                                binding.recyclerChat.scrollToPosition(messages.size() - 1);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ChatMessage>> call, Throwable t) {
                        // Bỏ qua lỗi mạng nhẹ
                    }
                });
    }

    // Gửi tin nhắn
    private void sendMessage(ChatMessage message) {
        RetrofitClient.getInstance().getApi().sendMessage(message)
                .enqueue(new Callback<ChatMessage>() {
                    @Override
                    public void onResponse(Call<ChatMessage> call,
                                           Response<ChatMessage> response) {
                        if (response.isSuccessful()) {
                            loadMessages();
                        }
                    }

                    @Override
                    public void onFailure(Call<ChatMessage> call, Throwable t) {}
                });
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
