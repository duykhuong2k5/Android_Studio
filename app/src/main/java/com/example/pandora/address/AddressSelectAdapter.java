package com.example.pandora.address;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.Address;

import java.util.List;

public class AddressSelectAdapter extends RecyclerView.Adapter<AddressSelectAdapter.AddressHolder> {

    private List<Address> list;
    private OnAddressSelected listener;

    // Interface callback khi chọn địa chỉ
    public interface OnAddressSelected {
        void onSelected(Address address);
    }

    public AddressSelectAdapter(List<Address> list, OnAddressSelected listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address_select, parent, false);
        return new AddressHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressHolder holder, int position) {
        Address a = list.get(position);

        holder.tvName.setText(a.getFullName());
        holder.tvPhone.setText(a.getPhone());
        holder.tvAddress.setText(a.getFullAddress());

        // Khi bấm vào item sẽ chọn địa chỉ
        holder.itemView.setOnClickListener(v -> listener.onSelected(a));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class AddressHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPhone, tvAddress;

        public AddressHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvAddress = itemView.findViewById(R.id.tvAddress);
        }
    }
}
