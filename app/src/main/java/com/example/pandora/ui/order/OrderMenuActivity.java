package com.example.pandora.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.Product;
import com.example.pandora.ui.adapter.ProductAdapter;
import com.example.pandora.address.AddressListActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderMenuActivity extends AppCompatActivity {

    private List<Product> products;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_menu);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());


        // MENU CLICK
        findViewById(R.id.btnProcessing).setOnClickListener(v -> openStatus("PROCESSING"));
        findViewById(R.id.btnDelivering).setOnClickListener(v -> openStatus("DELIVERING"));
        findViewById(R.id.btnCompleted).setOnClickListener(v -> openStatus("COMPLETED"));
        findViewById(R.id.btnFailed).setOnClickListener(v -> openStatus("FAILED"));

        // SỔ ĐỊA CHỈ
        findViewById(R.id.btnAddress).setOnClickListener(v ->
                startActivity(new Intent(this, AddressListActivity.class)));

        // GỢI Ý SẢN PHẨM
        RecyclerView recyclerProducts = findViewById(R.id.recyclerSuggest);

        products = new ArrayList<>(Arrays.asList(
                new Product(1L, "Charm Noel", "2872000", "3590000", "-20%",
                        "https://res.cloudinary.com/dnbxsm1mx/image/upload/v1762627567/charm1_on8uui.png", "CHARMS"),
                new Product(2L, "Nhẫn Pandora", "2990000", "3500000", "-15%",
                        "https://res.cloudinary.com/dnbxsm1mx/image/upload/v1762627567/charm2_qrxz1o.png", "NHẪN"),
                new Product(3L, "Vòng Tay Bạc", "3450000", "3950000", "-12%",
                        "https://res.cloudinary.com/dnbxsm1mx/image/upload/v1762627567/charm3_hosxin.png", "VÒNG TAY"),
                new Product(4L, "Hoa Tai Nữ", "2190000", "2750000", "-20%",
                        "https://res.cloudinary.com/dnbxsm1mx/image/upload/v1762627567/charm4_olyl8d.png", "HOA TAI")
        ));

        productAdapter = new ProductAdapter(this, products);

        recyclerProducts.setLayoutManager(
                new GridLayoutManager(this, 2)
        );
        recyclerProducts.setAdapter(productAdapter);
    }

    private void openStatus(String status) {
        Intent intent = new Intent(this, OrderStatusListActivity.class);
        intent.putExtra("statusGroup", status); // 🔥 sửa KEY cho đúng
        startActivity(intent);
    }
}
