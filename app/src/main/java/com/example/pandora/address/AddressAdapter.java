package com.example.pandora.address;


import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> {

    private Context context;
    private List<Address> list;
    private AddressListener listener;

    public interface AddressListener {
        void onSetDefault(Address a);
        void onEdit(Address a);
        void onDelete(Address a);
    }

    public AddressAdapter(Context context, List<Address> list, AddressListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder h, int pos) {
        Address a = list.get(pos);

        h.tvFullName.setText(a.getFullName());
        h.tvPhone.setText(a.getPhone());
        h.tvAddress.setText(a.getFullAddress());

        if (a.isDefault()) {
            h.btnSetDefault.setEnabled(false);
            h.btnSetDefault.setText("Đã mặc định");
        } else {
            h.btnSetDefault.setEnabled(true);
        }

        h.btnSetDefault.setOnClickListener(v -> listener.onSetDefault(a));
        h.btnEdit.setOnClickListener(v -> listener.onEdit(a));
        h.btnDelete.setOnClickListener(v -> listener.onDelete(a));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullName, tvPhone, tvAddress;
        Button btnSetDefault, btnEdit, btnDelete;

        public MyViewHolder(@NonNull View v) {
            super(v);
            tvFullName = v.findViewById(R.id.tvFullName);
            tvPhone = v.findViewById(R.id.tvPhone);
            tvAddress = v.findViewById(R.id.tvAddress);
            btnSetDefault = v.findViewById(R.id.btnSetDefault);
            btnEdit = v.findViewById(R.id.btnEdit);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}

