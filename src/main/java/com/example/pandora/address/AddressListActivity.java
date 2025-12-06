package com.example.pandora.address;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.Address;
import com.example.pandora.data.network.RetrofitClient;

import java.util.*;

import retrofit2.*;

public class AddressListActivity extends AppCompatActivity {

    RecyclerView recycler;
    Button btnAddAddress;
    List<Address> list = new ArrayList<>();
    AddressAdapter adapter;

    long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());


        recycler = findViewById(R.id.recyclerAddress);
        btnAddAddress = findViewById(R.id.btnAddAddress);

        userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getLong("user_id", -1);


        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AddressAdapter(this, list, new AddressAdapter.AddressListener() {
            @Override
            public void onSetDefault(Address a) {
                setDefault(a.getId());
            }

            @Override
            public void onEdit(Address a) {
                Intent i = new Intent(AddressListActivity.this, EditAddressActivity.class);
                i.putExtra("addressId", a.getId());
                startActivity(i);
            }

            @Override
            public void onDelete(Address a) {
                delete(a.getId());
            }
        });

        recycler.setAdapter(adapter);

        btnAddAddress.setOnClickListener(v -> startActivity(
                new Intent(this, AddAddressActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Address>> call, Throwable t) {
                        Toast.makeText(AddressListActivity.this, "Lỗi tải địa chỉ!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void delete(long id) {
        RetrofitClient.getInstance().getApi().deleteAddress(id)
                .enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> res) {
                        loadAddresses();
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) { }
                });
    }

    private void setDefault(long id) {
        RetrofitClient.getInstance().getApi().setDefaultAddress(id)
                .enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> res) {
                        loadAddresses();
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) { }
                });
    }
}
