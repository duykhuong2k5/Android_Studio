package com.example.pandora.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pandora.R;
import com.example.pandora.data.network.RetrofitClient;
import com.example.pandora.data.entity.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private List<User> users;

    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvRole;
        ImageButton btnEdit, btnDelete;

        public UserViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvUserName);
            tvEmail = view.findViewById(R.id.tvUserEmail);
            tvRole = view.findViewById(R.id.tvUserRole);
            btnEdit = view.findViewById(R.id.btnEditUser);
            btnDelete = view.findViewById(R.id.btnDeleteUser);
        }
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = users.get(position);

        holder.tvName.setText(user.getFullName());
        holder.tvEmail.setText(user.getEmail());
        holder.tvRole.setText(user.getRole() != null ? user.getRole() : "ROLE_CUSTOMER");

        holder.btnEdit.setOnClickListener(v -> showEditDialog(user, position));
        holder.btnDelete.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa người dùng này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteUser(user, position))
                .setNegativeButton("Hủy", null)
                .show());
    }

    private void showEditDialog(User user, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_user, null);
        EditText edtName = view.findViewById(R.id.edtUserName);
        EditText edtPhone = view.findViewById(R.id.edtUserPhone);
        EditText edtAddress = view.findViewById(R.id.edtUserAddress);
        Spinner spinnerRole = view.findViewById(R.id.spinnerUserRole);

        edtName.setText(user.getFullName());
        edtPhone.setText(user.getPhone() != null ? user.getPhone() : "");
        edtAddress.setText(user.getAddress() != null ? user.getAddress() : "");

        String[] roles = {"ROLE_CUSTOMER", "ROLE_ADMIN"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
        spinnerRole.setSelection(user.getRole() != null && user.getRole().equals("ROLE_ADMIN") ? 1 : 0);

        new AlertDialog.Builder(context)
                .setTitle("Cập nhật thông tin người dùng")
                .setView(view)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    User updatedUser = new User(
                            user.getId(),
                            edtName.getText().toString(),
                            user.getEmail(),
                            user.getPassword(),
                            edtPhone.getText().toString(),
                            edtAddress.getText().toString(),
                            spinnerRole.getSelectedItem().toString()
                    );

                    RetrofitClient.getInstance().getApi().updateUser(user.getId(), updatedUser)
                            .enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    if (response.isSuccessful()) {
                                        users.set(position, updatedUser);
                                        notifyItemChanged(position);
                                        Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Lỗi cập nhật!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    Toast.makeText(context, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteUser(User user, int position) {
        RetrofitClient.getInstance().getApi().deleteUser(user.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            users.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Đã xóa người dùng!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Lỗi khi xóa!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
