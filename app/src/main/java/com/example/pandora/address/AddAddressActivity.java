package com.example.pandora.address;


import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pandora.R;
import com.example.pandora.data.network.RetrofitClient;

import java.util.*;

import retrofit2.*;

public class AddAddressActivity extends AppCompatActivity {

    EditText edtFullName, edtPhone, edtHouse, edtStreet, edtWard, edtDistrict, edtProvince, edtNote;
    CheckBox chkDefault;
    Button btnSave;
    long userId;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_add_address);

        edtFullName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone);
        edtHouse = findViewById(R.id.edtHouse);
        edtStreet = findViewById(R.id.edtStreet);
        edtWard = findViewById(R.id.edtWard);
        edtDistrict = findViewById(R.id.edtDistrict);
        edtProvince = findViewById(R.id.edtProvince);
        edtNote = findViewById(R.id.edtNote);
        chkDefault = findViewById(R.id.chkDefault);
        btnSave = findViewById(R.id.btnSave);

        userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getLong("user_id", -1);
        Log.d("ADDRESS", "AddAddressActivity → UserId = " + userId);
        btnSave.setOnClickListener(v -> save());
    }

    private void save() {

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("fullName", edtFullName.getText().toString());
        map.put("phone", edtPhone.getText().toString());
        map.put("house", edtHouse.getText().toString());
        map.put("street", edtStreet.getText().toString());
        map.put("ward", edtWard.getText().toString());
        map.put("district", edtDistrict.getText().toString());
        map.put("province", edtProvince.getText().toString());
        map.put("note", edtNote.getText().toString());
        map.put("default", chkDefault.isChecked());

        RetrofitClient.getInstance().getApi().addAddress(map)
                .enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> res) {
                        Toast.makeText(AddAddressActivity.this, "Thêm thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
                        Toast.makeText(AddAddressActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
