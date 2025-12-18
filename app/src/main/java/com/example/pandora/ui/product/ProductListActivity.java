package com.example.pandora.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.Product;
import com.example.pandora.data.network.RetrofitClient;
import com.example.pandora.ui.adapter.ProductAdapter;
import com.example.pandora.ui.cart.CartActivity;
import com.example.pandora.ui.profile.ProfileActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView recyclerProducts;
    private TextView tvTitle, tvDesc, tvProductCount;
    private Button btnLoadMore;

    private ProductAdapter adapter;

    private List<Product> filteredProducts = new ArrayList<>();
    private List<Product> displayedProducts = new ArrayList<>();

    private LinearLayout layoutSearchBar;
    private EditText edtSearch;
    private ImageButton btnCloseSearch, btnSearch;
    private boolean isCombo = false;
    private String comboGift;
    private String comboRule;
    private int giftValue = 0;

    private int visibleCount = 4;
    private String searchKeyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // ===== BACK BUTTON =====
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(v -> finish());

        // ===== HEADER TITLE =====
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);

        String categoryName = getIntent().getStringExtra("categoryName");
        searchKeyword = getIntent().getStringExtra("searchKeyword");

        // Nếu đi từ search Home sang
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            tvHeaderTitle.setText("KẾT QUẢ CHO \"" + searchKeyword + "\"");
        } else {
            if (categoryName == null || categoryName.trim().isEmpty()) {
                categoryName = "SẢN PHẨM";
            }
            tvHeaderTitle.setText("ALL " + categoryName.toUpperCase());
        }


        ImageButton btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setVisibility(View.GONE);

        // ===== SEARCH BAR =====
        btnSearch = findViewById(R.id.btnSearch);
        layoutSearchBar = findViewById(R.id.layoutSearchBar);
        edtSearch = findViewById(R.id.edtSearch);
        btnCloseSearch = findViewById(R.id.btnCloseSearch);

        btnSearch.setOnClickListener(v -> {
            layoutSearchBar.setVisibility(View.VISIBLE);
            edtSearch.requestFocus();
        });

        btnCloseSearch.setOnClickListener(v -> {
            layoutSearchBar.setVisibility(View.GONE);
            edtSearch.setText("");
            restoreOriginalList();
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }
        });

        ImageButton btnFilter = findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(v -> openFilterSheet());

        // ===== MENU HEADER =====
        findViewById(R.id.btnCart).setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class))
        );
        findViewById(R.id.btnProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class))
        );
        // NHẬN THÔNG TIN COMBO
        isCombo = getIntent().getBooleanExtra("isCombo", false);
        comboGift = getIntent().getStringExtra("comboGift");
        comboRule = getIntent().getStringExtra("comboRule");
        giftValue = getIntent().getIntExtra("giftValue", 0);

        recyclerProducts = findViewById(R.id.recyclerCategoryProducts);
        tvDesc = findViewById(R.id.tvCategoryDesc);
        tvProductCount = findViewById(R.id.tvProductCount);
        btnLoadMore = findViewById(R.id.btnLoadMore);

        tvDesc.setText(getCategoryDescription(categoryName));

        recyclerProducts.setLayoutManager(new GridLayoutManager(this, 2));

        loadProductsFromServer(categoryName);
    }


    private void loadProductsFromServer(String categoryName) {

        String filterType = getIntent().getStringExtra("filterType");
        String[] multiCategories = getIntent().getStringArrayExtra("categories");

        RetrofitClient.getInstance().getApi().getAllProducts()
                .enqueue(new Callback<List<Product>>() {
                    @Override
                    public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            List<Product> all = response.body();
                            filteredProducts.clear();

                            // 🔍 NẾU ĐI TỪ SEARCH HOME SANG
                            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {

                                String kw = searchKeyword.toLowerCase();
                                for (Product p : all) {
                                    if (p.getName() != null &&
                                            p.getName().toLowerCase().contains(kw)) {
                                        filteredProducts.add(p);
                                    }
                                }

                            } else {
                                // 🔥 MULTI CATEGORY (Pandora ME, Combo, ...)
                                if ("MULTI_CATEGORY".equals(filterType) && multiCategories != null) {

                                    for (Product p : all) {
                                        if (p.getCategory() != null) {
                                            for (String cat : multiCategories) {
                                                if (p.getCategory().equalsIgnoreCase(cat)) {
                                                    filteredProducts.add(p);
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                } else {
                                    // 🔥 SINGLE CATEGORY
                                    for (Product p : all) {
                                        if (p.getCategory() != null &&
                                                p.getCategory().equalsIgnoreCase(categoryName)) {
                                            filteredProducts.add(p);
                                        }
                                    }
                                }
                            }

                            // ⭐⭐⭐ THÊM SẢN PHẨM QUÀ TẶNG ⭐⭐⭐
                            // ===== THÊM SẢN PHẨM QUÀ TẶNG (COMBO) =====
                            if (isCombo) {
                                Product gift = new Product();
                                gift.setName("🎁 Charm Quà Tặng Combo");
                                gift.setCategory("QUÀ TẶNG");
                                gift.setPriceNew("0");
                                gift.setPriceOld(giftValue + "");
                                gift.setDiscountPercent("100%");

                                // 🔥 Sửa lại URL ảnh bị dính 2 URL
                                gift.setImageUrl("https://res.cloudinary.com/dnbxsm1mx/image/upload/v1764223199/charmquatang_qejgkt.jpg");

                                filteredProducts.add(0, gift);
                            }

                            tvProductCount.setText("Hiện có " + filteredProducts.size() + " sản phẩm");

                            if (filteredProducts.isEmpty()) {
                                Toast.makeText(ProductListActivity.this,
                                        "Không có sản phẩm nào.", Toast.LENGTH_SHORT).show();
                                btnLoadMore.setVisibility(View.GONE);
                                return;
                            }

                            visibleCount = 4;
                            displayedProducts = new ArrayList<>(
                                    filteredProducts.subList(0, Math.min(4, filteredProducts.size()))
                            );

                            adapter = new ProductAdapter(
                                    ProductListActivity.this, displayedProducts, isCombo, comboGift, comboRule
                            );
                            recyclerProducts.setAdapter(adapter);

                            btnLoadMore.setOnClickListener(v -> loadMoreProducts());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Product>> call, Throwable t) {
                        Toast.makeText(ProductListActivity.this,
                                "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void filterProducts(String keyword) {
        if (keyword.trim().isEmpty()) {
            restoreOriginalList();
            return;
        }

        List<Product> result = new ArrayList<>();
        for (Product p : filteredProducts) {
            if (p.getName().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(p);
            }
        }

        displayedProducts.clear();
        displayedProducts.addAll(result);
        adapter.notifyDataSetChanged();

        btnLoadMore.setVisibility(View.GONE);
    }


    private void restoreOriginalList() {
        visibleCount = 4;
        displayedProducts.clear();
        displayedProducts.addAll(filteredProducts.subList(0,
                Math.min(4, filteredProducts.size())));
        adapter.notifyDataSetChanged();

        if (filteredProducts.size() > 4)
            btnLoadMore.setVisibility(View.VISIBLE);
    }

    private void loadMoreProducts() {
        int next = Math.min(visibleCount + 4, filteredProducts.size());
        displayedProducts.addAll(filteredProducts.subList(visibleCount, next));
        adapter.notifyDataSetChanged();
        visibleCount = next;

        if (visibleCount >= filteredProducts.size()) {
            btnLoadMore.setVisibility(View.GONE);
        }
    }

    private String getCategoryDescription(String category) {
        if (category == null) return "Khám phá những món trang sức tuyệt đẹp trong bộ sưu tập này.";
        switch (category.toUpperCase()) {
            case "CHARMS":
                return "Thể hiện phong cách riêng của bạn với những chiếc charm độc đáo.";
            case "NHẪN":
                return "Khám phá những thiết kế nhẫn Pandora thanh lịch, tinh tế.";
            case "VÒNG TAY":
                return "Khám phá vòng tay Pandora tinh xảo, phản ánh gu thẩm mỹ của bạn.";
            case "HOA TAI":
                return "Những đôi hoa tai Pandora tinh tế, tỏa sáng trong mọi khoảnh khắc.";
            case "DÂY CHUYỀN":
                return "Hoàn thiện phong cách với dây chuyền Pandora thanh lịch.";
            default:
                return "Khám phá những món trang sức tuyệt đẹp trong bộ sưu tập này.";
        }
    }


    private void openFilterSheet() {

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_filter, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);

        Spinner spinnerCategory = view.findViewById(R.id.spinnerCategory);
        Spinner spinnerPrice = view.findViewById(R.id.spinnerPrice);
        Spinner spinnerDiscount = view.findViewById(R.id.spinnerDiscount);
        MaterialButton btnApply = view.findViewById(R.id.btnApplyFilter);

        String[] categories = {"SẢN PHẨM MỚI", "CHARMS", "NHẪN", "VÒNG TAY", "HOA TAI", "DÂY CHUYỀN"};
        spinnerCategory.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, categories));

        String[] priceRanges = {"Tất cả", "Dưới 1 triệu", "1 - 3 triệu", "3 - 5 triệu", "Trên 5 triệu"};
        spinnerPrice.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, priceRanges));

        String[] discountOptions = {"Tất cả", "Trên 5%", "Trên 10%", "Trên 15%", "Trên 20%"};
        spinnerDiscount.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, discountOptions));

        btnApply.setOnClickListener(v -> {
            applyFilter(
                    spinnerCategory.getSelectedItem().toString(),
                    spinnerPrice.getSelectedItem().toString(),
                    spinnerDiscount.getSelectedItem().toString()
            );
            dialog.dismiss();
        });

        dialog.show();
    }


    private void applyFilter(String category, String priceRange, String discountRange) {

        List<Product> result = new ArrayList<>();

        for (Product p : filteredProducts) {

            boolean matchCategory =
                    category.equals("Tất cả") ||
                            p.getCategory().equalsIgnoreCase(category);

            double price = p.getParsedPrice();

            boolean matchPrice = false;
            switch (priceRange) {
                case "Tất cả": matchPrice = true; break;
                case "Dưới 1 triệu": matchPrice = price < 1_000_000; break;
                case "1 - 3 triệu": matchPrice = price >= 1_000_000 && price <= 3_000_000; break;
                case "3 - 5 triệu": matchPrice = price >= 3_000_000 && price <= 5_000_000; break;
                case "Trên 5 triệu": matchPrice = price > 5_000_000; break;
            }

            int discountValue = Integer.parseInt(
                    p.getDiscountPercent().replace("%", "").replace("-", "")
            );

            boolean matchDiscount = false;
            switch (discountRange) {
                case "Tất cả": matchDiscount = true; break;
                case "Trên 5%": matchDiscount = discountValue > 5; break;
                case "Trên 10%": matchDiscount = discountValue > 10; break;
                case "Trên 15%": matchDiscount = discountValue > 15; break;
                case "Trên 20%": matchDiscount = discountValue > 20; break;
            }

            if (matchCategory && matchPrice && matchDiscount) {
                result.add(p);
            }
        }

        displayedProducts.clear();
        displayedProducts.addAll(result);
        adapter.notifyDataSetChanged();

        btnLoadMore.setVisibility(View.GONE);
    }
}
