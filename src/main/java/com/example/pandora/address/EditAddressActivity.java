package com.example.pandora.address;


import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pandora.R;
import com.example.pandora.data.entity.Address;
import com.example.pandora.data.network.RetrofitClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.*;

public class EditAddressActivity extends AppCompatActivity {

    EditText edtFullName, edtPhone, edtHouse, edtStreet, edtWard, edtDistrict, edtProvince, edtNote;
    CheckBox chkDefault;
    Button btnUpdate;

    long addressId;
    long userId;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_edit_address);

        addressId = getIntent().getLongExtra("addressId", -1);
        userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getLong("userId", -1);

        edtFullName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone);
        edtHouse = findViewById(R.id.edtHouse);
        edtStreet = findViewById(R.id.edtStreet);
        edtWard = findViewById(R.id.edtWard);
        edtDistrict = findViewById(R.id.edtDistrict);
        edtProvince = findViewById(R.id.edtProvince);
        edtNote = findViewById(R.id.edtNote);
        chkDefault = findViewById(R.id.chkDefault);
        btnUpdate = findViewById(R.id.btnUpdate);

        loadAddress();

        btnUpdate.setOnClickListener(v -> updateAddress());
    }

    private void loadAddress() {
        RetrofitClient.getInstance().getApi().getAddresses(userId)
                .enqueue(new Callback<List<Address>>() {
                    @Override
                    public void onResponse(Call<List<Address>> call, Response<List<Address>> res) {
                        if (res.isSuccessful()) {
                            for (Address a : res.body()) {
                                if (a.getId().equals(addressId)) {
                                    edtFullName.setText(a.getFullName());
                                    edtPhone.setText(a.getPhone());
                                    edtHouse.setText(a.getHouse());
                                    edtStreet.setText(a.getStreet());
                                    edtWard.setText(a.getWard());
                                    edtDistrict.setText(a.getDistrict());
                                    edtProvince.setText(a.getProvince());
                                    edtNote.setText(a.getNote());
                                    chkDefault.setChecked(a.isDefault());
                                    return;
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Address>> call, Throwable t) {
                        Toast.makeText(EditAddressActivity.this, "Lỗi tải địa chỉ!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateAddress() {

        if (!validate()) return;

        Map<String, Object> map = new HashMap<>();
        map.put("fullName", edtFullName.getText().toString());
        map.put("phone", edtPhone.getText().toString());
        map.put("house", edtHouse.getText().toString());
        map.put("street", edtStreet.getText().toString());
        map.put("ward", edtWard.getText().toString());
        map.put("district", edtDistrict.getText().toString());
        map.put("province", edtProvince.getText().toString());
        map.put("note", edtNote.getText().toString());
        map.put("default", chkDefault.isChecked());

        RetrofitClient.getInstance().getApi().updateAddress(addressId, map)
                .enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> res) {
                        Toast.makeText(EditAddressActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
                        Toast.makeText(EditAddressActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validate() {
        if (edtHouse.getText().toString().trim().length() < 3) {
            edtHouse.setError("Cần nhập số nhà / xóm rõ ràng!");
            return false;
        }
        if (edtStreet.getText().toString().trim().length() < 3) {
            edtStreet.setError("Cần nhập tên đường / thôn!");
            return false;
        }
        if (edtWard.getText().toString().trim().length() < 2) {
            edtWard.setError("Cần nhập phường / xã!");
            return false;
        }
        if (edtDistrict.getText().toString().trim().length() < 2) {
            edtDistrict.setError("Cần nhập quận / huyện!");
            return false;
        }
        if (edtProvince.getText().toString().trim().length() < 2) {
            edtProvince.setError("Cần nhập tỉnh / thành phố!");
            return false;
        }
        return true;
    }
}

