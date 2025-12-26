package com.example.pandora.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.User;
import com.example.pandora.ui.adapter.UserAdapter;
import com.example.pandora.data.network.RetrofitClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerUsers;
    private UserAdapter adapter;
    private List<User> users = new ArrayList<>();
    private List<User> filteredUsers = new ArrayList<>();
    private EditText edtSearchUser;
    private Spinner spinnerRole;
    private com.google.android.material.floatingactionbutton.FloatingActionButton fabAddUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        recyclerUsers = findViewById(R.id.recyclerUsers);
        edtSearchUser = findViewById(R.id.edtSearchUser);
        spinnerRole = findViewById(R.id.spinnerRole);
        fabAddUser      = findViewById(R.id.fabAddUser);
        recyclerUsers.setLayoutManager(new LinearLayoutManager(this));

        loadUsers();

        List<String> roles = Arrays.asList("Tất cả", "ROLE_CUSTOMER", "ROLE_ADMIN", "ROLE_MANAGER", "ROLE_SHIPPER");
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);

        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedRole = parentView.getItemAtPosition(position).toString();
                String searchQuery = edtSearchUser.getText().toString().trim();
                filterUsers(searchQuery, selectedRole);  // Filter users based on search query and role
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        edtSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String selectedRole = spinnerRole.getSelectedItem().toString();
                filterUsers(charSequence.toString(), selectedRole);  // Filter users based on text input
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        fabAddUser.setOnClickListener(v -> showAddUserDialog());
    }

    private void loadUsers() {
        String adminEmail = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("email", null);

        if (adminEmail == null) {
            Toast.makeText(this, "Không tìm thấy tài khoản admin!", Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitClient.getInstance().getApi().getAllUsers(adminEmail)
                .enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            users = response.body();

                            // Xử lý vai trò mặc định nếu không có vai trò
                            for (User user : users) {
                                if (user.getRole() == null || user.getRole().isEmpty()) {
                                    user.setRole("ROLE_CUSTOMER");  // Mặc định nếu không có vai trò
                                }
                            }

                            filteredUsers.addAll(users);  // Thêm tất cả người dùng vào filteredUsers ban đầu

                            // Thiết lập adapter cho RecyclerView
                            adapter = new UserAdapter(ManageUsersActivity.this, filteredUsers);
                            recyclerUsers.setAdapter(adapter);
                        } else {
                            Toast.makeText(ManageUsersActivity.this, "Không thể tải danh sách người dùng!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<User>> call, Throwable t) {
                        Toast.makeText(ManageUsersActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterUsers(String query, String selectedRole) {
        filteredUsers.clear();

        // Chuyển vai trò đã chọn thành chữ hoa để đảm bảo so sánh đúng
        String selectedRoleUpper = selectedRole.trim().toUpperCase();

        // Lọc người dùng dựa trên tên và vai trò
        for (User user : users) {
            // Kiểm tra tên người dùng có chứa từ khóa tìm kiếm không
            boolean matchesName = user.getFullName().toLowerCase().contains(query.toLowerCase());
            // Kiểm tra vai trò có khớp không
            boolean matchesRole = selectedRoleUpper.equals("TẤT CẢ") || user.getRole().toUpperCase().equals(selectedRoleUpper);

            // Thêm người dùng vào danh sách lọc nếu cả hai điều kiện thỏa mãn
            if (matchesName && matchesRole) {
                filteredUsers.add(user);
            }
        }

        // Cập nhật RecyclerView sau khi lọc
        if (adapter != null) {
            adapter.notifyDataSetChanged();  // Cập nhật lại RecyclerView với danh sách đã lọc
        }
    }
    private void showAddUserDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_user, null);

        EditText edtName     = view.findViewById(R.id.edtUserName);
        EditText edtEmail    = view.findViewById(R.id.edtUserEmail);
        EditText edtPassword = view.findViewById(R.id.edtUserPassword);
        EditText edtPhone    = view.findViewById(R.id.edtUserPhone);
        EditText edtAddress  = view.findViewById(R.id.edtUserAddress);
        Spinner spinnerUserRole = view.findViewById(R.id.spinnerUserRole);

        String[] roles = {"ROLE_CUSTOMER", "ROLE_ADMIN", "ROLE_MANAGER", "ROLE_SHIPPER"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserRole.setAdapter(roleAdapter);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Thêm người dùng mới")
                .setView(view)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String fullName = edtName.getText().toString().trim();
                    String email    = edtEmail.getText().toString().trim();
                    String password = edtPassword.getText().toString().trim();
                    String phone    = edtPhone.getText().toString().trim();
                    String address  = edtAddress.getText().toString().trim();
                    String role     = spinnerUserRole.getSelectedItem().toString();

                    if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập tên, email, mật khẩu!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    User newUser = new User(
                            null,
                            fullName,
                            email,
                            password,   // backend sẽ tự mã hoá
                            phone,
                            address,
                            role
                    );

                    String adminEmail = getSharedPreferences("user_prefs", MODE_PRIVATE)
                            .getString("email", null);

                    if (adminEmail == null) {
                        Toast.makeText(this, "Không tìm thấy tài khoản admin!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    RetrofitClient.getInstance().getApi()
                            .createUserByAdmin(adminEmail, newUser)
                            .enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        User created = response.body();
                                        users.add(created);
                                        filteredUsers.add(created);
                                        adapter.notifyItemInserted(filteredUsers.size() - 1);
                                        Toast.makeText(ManageUsersActivity.this,
                                                "Đã thêm người dùng mới!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ManageUsersActivity.this,
                                                "Không thể thêm người dùng!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    Toast.makeText(ManageUsersActivity.this,
                                            "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
