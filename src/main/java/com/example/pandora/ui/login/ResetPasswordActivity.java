package com.example.pandora.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pandora.R;
import com.example.pandora.data.network.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.*;

public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        EditText edtPass = findViewById(R.id.edtNewPassword);
        EditText edtConfirm = findViewById(R.id.edtConfirmPassword);
        Button btnReset = findViewById(R.id.btnResetPassword);

        String email = getIntent().getStringExtra("email");

        btnReset.setOnClickListener(v -> {
            String pass = edtPass.getText().toString().trim();
            String confirm = edtConfirm.getText().toString().trim();

            if (!pass.equals(confirm)) {
                Toast.makeText(this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, String> map = new HashMap<>();
            map.put("email", email);
            map.put("newPassword", pass);

            RetrofitClient.getInstance().getApi().resetPassword(map)
                    .enqueue(new Callback<Map<String, String>>() {
                        @Override
                        public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {

                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(ResetPasswordActivity.this,
                                        response.body().get("message"), Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(ResetPasswordActivity.this, "Lỗi cập nhật mật khẩu!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, String>> call, Throwable t) {
                            Toast.makeText(ResetPasswordActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                        }
                    });

        });
    }
}
