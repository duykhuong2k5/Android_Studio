package com.example.pandora.ui.category;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.ui.product.ProductListActivity;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubCategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<String> subList = new ArrayList<>();
    private String mainCategory;

    // ⭐ Map SubCategory → Category thật trong DB
    private final Map<String, String[]> subCategoryMap = new HashMap<String, String[]>() {{
        put("Pandora Moments",     new String[]{"CHARMS", "VÒNG TAY"});
        put("Pandora ME",          new String[]{"CHARMS", "NHẪN"});
        put("Pandora Essence",     new String[]{"DÂY CHUYỀN"});
        put("Pandora Timeless",    new String[]{"NHẪN", "HOA TAI"});
        put("Pandora Signature",   new String[]{"NHẪN", "DÂY CHUYỀN"});

        put("Disney x Pandora",       new String[]{"CHARMS"});
        put("Marvel x Pandora",       new String[]{"CHARMS"});
        put("Stranger Things x Pandora", new String[]{"CHARMS"});
        put("Game of Thrones x Pandora", new String[]{"CHARMS"});
        put("UNICEF x Pandora",       new String[]{"CHARMS"});

        put("Nhẫn đôi", new String[]{"NHẪN"});
        put("Vòng đôi", new String[]{"VÒNG TAY"});
        put("Charm đôi", new String[]{"CHARMS"});

        put("Sinh nhật", new String[]{"NHẪN", "CHARMS"});
        put("Cung hoàng đạo", new String[]{"CHARMS"});
        put("Chữ cái", new String[]{"DÂY CHUYỀN"});
        put("Tốt nghiệp", new String[]{"CHARMS"});
        put("Dịp đặc biệt", new String[]{"NHẪN", "DÂY CHUYỀN"});

        // COMBO: map thẳng đến category combo trong DB
        put("Combo quà tặng 1", new String[]{"COMBO_UU_DAI"});
        put("Combo quà tặng 2", new String[]{"COMBO_QUA_TANG"});

    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerSubCategory);
        TextView tvTitle = findViewById(R.id.tvTitle);

        mainCategory = getIntent().getStringExtra("mainCategory");
        tvTitle.setText(mainCategory);

        loadSubCategories(mainCategory);

        SubCategoryAdapter adapter = new SubCategoryAdapter(subList, name -> {

            if (subCategoryMap.containsKey(name)) {

                String[] mappedCats = subCategoryMap.get(name);
                Intent intent = new Intent(this, ProductListActivity.class);

                // luôn dùng MULTI_CATEGORY (dù 1 hay nhiều category) để tái sử dụng code cũ
                intent.putExtra("filterType", "MULTI_CATEGORY");
                intent.putExtra("categories", mappedCats);
                intent.putExtra("categoryName", name); // dùng để hiển thị tiêu đề

                // 🔥 CHỈ GẮN FLAG COMBO CHO 2 MỤC COMBO
                if ("Combo quà tặng 1".equals(name)) {
                    intent.putExtra("isCombo", true);
                    intent.putExtra("comboGift", "Tặng 1 charm trị giá 1.000.000đ");
                    intent.putExtra("comboRule", "Mua 1 Nhẫn + 1 Vòng Tay");
                    intent.putExtra("giftValue", 1000000);
                } else if ("Combo quà tặng 2".equals(name)) {
                    intent.putExtra("isCombo", true);
                    intent.putExtra("comboGift", "Tặng 1 charm trị giá 1.500.000đ");
                    intent.putExtra("comboRule", "Mua Vòng Tay + Dây Chuyền tặng 1 Charm");
                    intent.putExtra("giftValue", 1500000);
                }

                startActivity(intent);

            } else {
                // ⭐ Nếu name là category thật (CHARMS, VÒNG TAY, ...) → lọc category đơn
                Intent intent = new Intent(this, ProductListActivity.class);
                intent.putExtra("categoryName", name);
                startActivity(intent);
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadSubCategories(String category) {

        subList.clear();

        switch (category) {

            case "DÒNG SẢN PHẨM":
                subList.add("Pandora Moments");
                subList.add("Pandora ME");
                subList.add("Pandora Essence");
                subList.add("Pandora Timeless");
                subList.add("Pandora Signature");
                break;

            case "COLLABORATION":
                subList.add("Disney x Pandora");
                subList.add("Marvel x Pandora");
                subList.add("Stranger Things x Pandora");
                subList.add("Game of Thrones x Pandora");
                subList.add("UNICEF x Pandora");
                break;

            case "TRANG SỨC ĐÔI":
                subList.add("Nhẫn đôi");
                subList.add("Vòng đôi");
                subList.add("Charm đôi");
                break;

            case "DỊP ĐẶC BIỆT":
                subList.add("Sinh nhật");
                subList.add("Cung hoàng đạo");
                subList.add("Chữ cái");
                subList.add("Tốt nghiệp");
                subList.add("Dịp đặc biệt");
                break;

            case "COMBO ĐẶC BIỆT":
                subList.add("Combo quà tặng 1");
                subList.add("Combo quà tặng 2");
                break;
        }
    }
}
