package com.example.pandora.address;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.Address;
import com.example.pandora.data.network.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectAddressActivity extends AppCompatActivity {

    RecyclerView recycler;
    List<Address> list = new ArrayList<>();
    AddressSelectAdapter adapter;
    long userId;

    private MaterialToolbar topAppBar;
    private MaterialButton btnAddNew;

    // Launcher để mở màn hình thêm địa chỉ và nhận kết quả
    private ActivityResultLauncher<Intent> addAddressLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_address);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Lấy userId từ SharedPreferences
        userId = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getLong("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ view
        recycler = findViewById(R.id.recyclerSelectAddress);
        topAppBar = findViewById(R.id.topAppBar);
        btnAddNew = findViewById(R.id.btnAddNew);

        // Toolbar back
        topAppBar.setNavigationOnClickListener(v -> onBackPressed());

        // Setup RecyclerView
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AddressSelectAdapter(list, address -> {
            Intent result = new Intent();
            result.putExtra("address_id", address.getId());
            setResult(RESULT_OK, result);
            finish();
        });

        recycler.setAdapter(adapter);

        // Đăng ký launcher để mở màn hình thêm/sửa địa chỉ
        addAddressLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Sau khi thêm địa chỉ thành công -> load lại danh sách
                        loadAddresses();
                    }
                }
        );

        // Xử lý nút "Thêm địa chỉ mới"
        btnAddNew.setOnClickListener(v -> {
            Intent intent = new Intent(SelectAddressActivity.this, AddAddressActivity.class);
            // Nếu màn thêm cần biết userId để gán địa chỉ cho user
            intent.putExtra("user_id", userId);
            addAddressLauncher.launch(intent);
        });

        // Load danh sách lần đầu
        loadAddresses();
    }

    private void loadAddresses() {
        RetrofitClient.getInstance().getApi().getAddresses(userId)
                .enqueue(new Callback<List<Address>>() {
                    @Override
                    public void onResponse(Call<List<Address>> call, Response<List<Address>> res) {
                        if (res.isSuccessful() && res.body() != null) {
                            list.clear();
                            list.addAll(res.body());
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(SelectAddressActivity.this,
                                    "Không tải được danh sách địa chỉ!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Address>> call, Throwable t) {
                        Toast.makeText(SelectAddressActivity.this,
                                "Không tải được danh sách địa chỉ!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
