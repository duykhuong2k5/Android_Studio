package com.example.pandora.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pandora.HomeActivity;
import com.example.pandora.R;
import com.example.pandora.admin.AdminDashboardActivity;
import com.example.pandora.data.entity.LoginRequest;
import com.example.pandora.data.entity.User;
import com.example.pandora.data.network.RetrofitClient;
import com.example.pandora.shipper.ShipperDashboardActivity;
import com.example.pandora.shipper.ShipperOrdersActivity;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtPassword = findViewById(R.id.edtPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView btnToRegister = findViewById(R.id.btnToRegister);
        TextView btnForgot = findViewById(R.id.btnForgotPassword);

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            LoginRequest request = new LoginRequest(email, password);

            RetrofitClient.getInstance().getApi().loginUser(request)
                    .enqueue(new Callback<Map<String, String>>() {
                        @Override
                        public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Map<String, String> data = response.body();
                                String message = data.get("message");
                                String role = data.get("role");
                                String emailFromApi = data.get("email");

                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

                                if ("Đăng nhập thành công!".equals(message)) {

                                    // Lưu email + role
                                    getSharedPreferences("user_prefs", MODE_PRIVATE)
                                            .edit()
                                            .putString("email", emailFromApi)
                                            .putString("role", role)
                                            .apply();

                                    // Gọi API lấy thông tin user
                                    RetrofitClient.getInstance().getApi().getUserByEmail(emailFromApi)
                                            .enqueue(new Callback<User>() {
                                                @Override
                                                public void onResponse(Call<User> call, Response<User> resp) {
                                                    if (resp.isSuccessful() && resp.body() != null) {
                                                        User userInfo = resp.body();

                                                        getSharedPreferences("user_prefs", MODE_PRIVATE)
                                                                .edit()
                                                                .putLong("user_id", userInfo.getId())
                                                                .apply();

                                                        // Điều hướng theo role
                                                        switch (role) {
                                                            case "ROLE_ADMIN":
                                                                startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                                                                break;
                                                            case "ROLE_SHIPPER":
                                                                startActivity(new Intent(LoginActivity.this, ShipperDashboardActivity.class));
                                                                break;
                                                            default:
                                                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                                        }
                                                        finish();
                                                    } else {
                                                        Toast.makeText(LoginActivity.this, "Không lấy được thông tin người dùng!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<User> call, Throwable t) {
                                                    Toast.makeText(LoginActivity.this, "Lỗi khi tải thông tin người dùng!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, String>> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        btnToRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        btnForgot.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class))
        );

    }
}

