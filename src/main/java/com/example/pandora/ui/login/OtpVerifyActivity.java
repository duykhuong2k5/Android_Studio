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

public class OtpVerifyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        EditText edtOtp = findViewById(R.id.edtOtp);
        Button btnVerify = findViewById(R.id.btnVerifyOtp);

        String email = getIntent().getStringExtra("email");

        btnVerify.setOnClickListener(v -> {
            String otp = edtOtp.getText().toString().trim();

            if (otp.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập OTP!", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, String> map = new HashMap<>();
            map.put("email", email);
            map.put("otp", otp);

            RetrofitClient.getInstance().getApi().verifyOtp(map)
                    .enqueue(new Callback<Map<String, String>>() {
                        @Override
                        public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {

                            if (response.isSuccessful() && response.body() != null) {
                                String msg = response.body().get("message");

                                Toast.makeText(OtpVerifyActivity.this, msg, Toast.LENGTH_SHORT).show();

                                if (msg.contains("hợp lệ")) {
                                    Intent intent = new Intent(OtpVerifyActivity.this, ResetPasswordActivity.class);
                                    intent.putExtra("email", email);
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(OtpVerifyActivity.this, "OTP không đúng!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, String>> call, Throwable t) {
                            Toast.makeText(OtpVerifyActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                        }
                    });

        });
    }
}
