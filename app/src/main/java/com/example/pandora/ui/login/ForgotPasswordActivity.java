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

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        EditText edtEmail = findViewById(R.id.edtEmailForgot);
        Button btnSendOtp = findViewById(R.id.btnSendOtp);

        btnSendOtp.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, String> body = new HashMap<>();
            body.put("email", email);

            RetrofitClient.getInstance().getApi().sendOtp(body)
                    .enqueue(new Callback<Map<String, String>>() {
                        @Override
                        public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {

                            if (response.isSuccessful() && response.body() != null) {

                                String message = response.body().get("message");
                                Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();

                                Intent i = new Intent(ForgotPasswordActivity.this, OtpVerifyActivity.class);
                                i.putExtra("email", email);
                                startActivity(i);

                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, "Email không tồn tại!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, String>> call, Throwable t) {
                            Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                        }
                    });

        });
    }
}
