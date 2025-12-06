package com.example.pandora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.widget.MediaController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.data.entity.Category;
import com.example.pandora.data.entity.Product;
import com.example.pandora.data.entity.User;
import com.example.pandora.ui.adapter.CategoryAdapter;
import com.example.pandora.ui.adapter.ProductAdapter;
import com.example.pandora.ui.adapter.SuggestionAdapter;
import com.example.pandora.ui.cart.CartActivity;
import com.example.pandora.ui.category.SubCategoryActivity;
import com.example.pandora.ui.login.LoginActivity;
import com.example.pandora.ui.product.ProductListActivity;
import com.example.pandora.ui.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.example.pandora.data.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private List<Product> products = new ArrayList<>();// ⭐ danh sách gốc
    private ProductAdapter productAdapter;
    private RecyclerView recyclerRecommended;
    private ProductAdapter recommendedAdapter;
    // 🔍 Gợi ý tìm kiếm
    private RecyclerView recyclerSearchSuggestions;
    private com.example.pandora.ui.adapter.SuggestionAdapter suggestionAdapter;
    private android.os.Handler searchHandler = new android.os.Handler();
    private Runnable searchRunnable;
    private TextView tvCountdown;
    private TextView tvProductCountdown;
    private VideoView videoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvCountdown = findViewById(R.id.tvCountdown);  // TextView cho countdown
        startCountdown();
        CardView card1 = findViewById(R.id.card1);
        CardView card2 = findViewById(R.id.card2);
        CardView card3 = findViewById(R.id.card3);
        CardView card4 = findViewById(R.id.card4);
        // Set click listener
        card1.setOnClickListener(v -> openProductList("Khoảnh Khắc Tỏa Sáng"));
        card2.setOnClickListener(v -> openProductList("Lời Gọi Biển Khơi"));
        card3.setOnClickListener(v -> openProductList("Tháng Của Xứ Nữ"));
        card4.setOnClickListener(v -> openProductList("Nơi Tình Yêu Bắt Đầu"));

        // Cũng set cho các nút bên trong
        TextView btnCard1 = findViewById(R.id.btnCard1);
        TextView btnCard2 = findViewById(R.id.btnCard2);
        TextView btnCard3 = findViewById(R.id.btnCard3);
        TextView btnCard4 = findViewById(R.id.btnCard4);

        btnCard1.setOnClickListener(v -> openProductList("Khoảnh Khắc Tỏa Sáng"));
        btnCard2.setOnClickListener(v -> openProductList("Lời Gọi Biển Khơi"));
        btnCard3.setOnClickListener(v -> openProductList("Tháng Của Xứ Nữ"));
        btnCard4.setOnClickListener(v -> openProductList("Nơi Tình Yêu Bắt Đầu"));






        TextView tvTitle = findViewById(R.id.tvTitle);

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Vì đang ở Home rồi → không cần mở lại nữa
                // Hoặc nếu muốn reload trang thì mở lại Activity
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        ImageButton btnMenu = findViewById(R.id.btnMenu);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navView = findViewById(R.id.navigationView);
        navView.setItemIconTintList(null);

        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(Gravity.START));


        // 🛒 Nút giỏ hàng & hồ sơ
        ImageButton btnCart = findViewById(R.id.btnCart);
        ImageButton btnProfile = findViewById(R.id.btnProfile);
        ImageButton btnSearch = findViewById(R.id.btnSearch);
        LinearLayout layoutSearchBar = findViewById(R.id.layoutSearchBar);
        ImageButton btnCloseSearch = findViewById(R.id.btnCloseSearch);
        EditText edtSearch = findViewById(R.id.edtSearch);

        // 🔍 RecyclerView gợi ý tìm kiếm
        recyclerSearchSuggestions = findViewById(R.id.recyclerSearchSuggestions);
        recyclerSearchSuggestions.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );
        suggestionAdapter = new SuggestionAdapter(new ArrayList<>(), keyword -> {
            // Khi user chọn 1 gợi ý
            edtSearch.setText(keyword);
            edtSearch.setSelection(keyword.length());

            layoutSearchBar.setVisibility(View.GONE);
            recyclerSearchSuggestions.setVisibility(View.GONE);

            openSearchResult(keyword);
        });
        recyclerSearchSuggestions.setAdapter(suggestionAdapter);

        // 👉 SHOW thanh tìm kiếm
        btnSearch.setOnClickListener(v -> {
            layoutSearchBar.setVisibility(View.VISIBLE);
            edtSearch.requestFocus();
        });

        // 👉 ĐÓNG thanh tìm kiếm
        btnCloseSearch.setOnClickListener(v -> {
            layoutSearchBar.setVisibility(View.GONE);
            edtSearch.setText("");
            recyclerSearchSuggestions.setVisibility(View.GONE); // ẩn gợi ý
            productAdapter.updateList(products); // ⭐ reset
        });



        btnCart.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        // 🌸 Sản phẩm nổi bật
        RecyclerView recyclerProducts = findViewById(R.id.recyclerProducts);
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

        // ❗ Fix lỗi: không tạo adapter mới ngoài productAdapter
        productAdapter = new ProductAdapter(this, products);
        recyclerProducts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerProducts.setAdapter(productAdapter);

        // 🔎 GỢI Ý REALTIME KHI GÕ (CALL API)
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();

                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> {
                    if (keyword.isEmpty()) {
                        recyclerSearchSuggestions.setVisibility(View.GONE);
                        // reset lại danh sách sp nổi bật ban đầu nếu muốn
                        productAdapter.updateList(products);
                    } else {
                        loadSuggestions(keyword);
                    }
                };

                searchHandler.postDelayed(searchRunnable, 300); // debounce 300ms
            }
        });

        // ⏎ ENTER → CHUYỂN TRANG SEARCH
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                String keyword = edtSearch.getText().toString().trim();

                if (!keyword.isEmpty()) {
                    openSearchResult(keyword);
                }
                return true;
            }
            return false;
        });

        // 🕓 Sản phẩm đã xem gần đây
        RecyclerView recyclerRecentlyViewed = findViewById(R.id.recyclerRecentlyViewed);
        List<Product> viewedProducts = new ArrayList<>(Arrays.asList(
                new Product(5L, "Charm Hoa Bạc", "1990000", "2590000", "-23%",
                        "https://res.cloudinary.com/dnbxsm1mx/image/upload/v1762627567/charm3_hosxin.png", "CHARMS"),
                new Product(6L, "Hoa Tai Bạc", "2190000", "2750000", "-20%",
                        "https://res.cloudinary.com/dnbxsm1mx/image/upload/v1762627568/hoatai_o4igeb.jpg", "HOA TAI")
        ));
        recyclerRecentlyViewed.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerRecentlyViewed.setAdapter(new ProductAdapter(this, viewedProducts, true)); // chế độ rút gọn

        // 💖 GỢI Ý SẢN PHẨM DỰA TRÊN LỊCH SỬ MUA
        recyclerRecommended = findViewById(R.id.recyclerRecommended);
        recyclerRecommended.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        // gọi API gợi ý
        loadRecommendedProducts();
        loadComboProducts();


        // 🔥 SIÊU HỘI 11.11
        RecyclerView recyclerPromo = findViewById(R.id.recyclerPromo);
        List<Product> promoProducts = new ArrayList<>(Arrays.asList(
                new Product(7L, "Vòng Bạc Pandora Moments Khóa Hoa Hồng", "2872000", "3590000", "-20%",
                        "https://res.cloudinary.com/dnbxsm1mx/image/upload/v1762627569/vongtay_kcgrau.jpg", "VÒNG TAY"),
                new Product(8L, "Charm Treo Ổ Khóa Và Chìa Khóa", "2872000", "3590000", "-20%",
                        "https://res.cloudinary.com/dnbxsm1mx/image/upload/v1762627570/charm_jfibcb.png", "CHARMS"),
                new Product(9L, "Vòng Pandora Moments Bạc Dạng Gai Dây Rút", "2872000", "3590000", "-20%",
                        "https://res.cloudinary.com/dnbxsm1mx/image/upload/v1762627570/vongtayrut_hsvuia.png", "VÒNG TAY"),
                new Product(10L, "Vòng Tay Pandora Mạ Vàng 14K", "2872000", "3590000", "-20%",
                        "https://res.cloudinary.com/dnbxsm1mx/image/upload/v1762627570/vongtaymavang_mzvnti.png", "VÒNG TAY")
        ));
        recyclerPromo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerPromo.setAdapter(new ProductAdapter(this, promoProducts));

        // 🛍️ Danh mục sản phẩm
        RecyclerView recyclerCategory = findViewById(R.id.recyclerCategory);
        List<Category> categories = new ArrayList<>(Arrays.asList(
                new Category("CHARMS", R.drawable.charm),
                new Category("NHẪN", R.drawable.nhan),
                new Category("VÒNG TAY", R.drawable.vongtay),
                new Category("HOA TAI", R.drawable.hoatai),
                new Category("DÂY CHUYỀN", R.drawable.day_chuyen),
                new Category("SẢN PHẨM MỚI", R.drawable.sanphammoi)
        ));
        recyclerCategory.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerCategory.setAdapter(new CategoryAdapter(this, categories));



        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_christmas) {
                    showToast("🎄 Giáng Sinh");
                } else if (id == R.id.nav_jewelry) {
                    showToast("💎 Trang Sức");
                } else if (id == R.id.nav_bracelet) {
                    showToast("💍 Vòng Tay Pandora");
                } else if (id == R.id.nav_charms) {
                    showToast("✨ Charms");
                } else if (id == R.id.nav_necklace) {
                    showToast("📿 Dây Chuyền");
                } else if (id == R.id.nav_earrings) {
                    showToast("🩷 Hoa Tai");
                } else if (id == R.id.nav_ring) {
                    showToast("💫 Nhẫn");
                }
                else if (id == R.id.nav_product_line) {
                    openSubCategory("DÒNG SẢN PHẨM");

                } else if (id == R.id.nav_collaboration) {
                    openSubCategory("COLLABORATION");

                } else if (id == R.id.nav_special_event) {
                    openSubCategory("DỊP ĐẶC BIỆT");

                } else if (id == R.id.nav_couple_jewelry) {
                    openSubCategory("TRANG SỨC ĐÔI");

                } else if (id == R.id.nav_combo) {
                    openSubCategory("COMBO ĐẶC BIỆT");
                }

                else if (id == R.id.nav_favorite) {
                    // 💗 Mở trang sản phẩm yêu thích
                    startActivity(new Intent(HomeActivity.this, FavoriteActivity.class));
                }
                else {
                    showToast("Danh mục chưa có nội dung.");
                }

                drawerLayout.closeDrawers();
                return true;
            }
        });
        initializeVideoPlayer();
    }
    // ⭐ Hàm lọc sản phẩm theo tên
    // ⭐ LỌC SẢN PHẨM THEO TÊN
    private void filterProducts(String keyword) {

        if (products == null || products.isEmpty()) return;

        List<Product> filtered = new ArrayList<>();

        for (Product p : products) {
            if (p.getName().toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(p);
            }
        }

        productAdapter.updateList(filtered);
    }


    // ✅ Hàm showToast
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void logout() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Toast.makeText(this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
    private void openSubCategory(String name) {
        Intent intent = new Intent(this, SubCategoryActivity.class);
        intent.putExtra("mainCategory", name);
        startActivity(intent);
    }
    // 🔍 Gọi API backend để lấy gợi ý keyword
    private void loadSuggestions(String keyword) {
        RetrofitClient.getInstance().getApi().getSuggestions(keyword)
                .enqueue(new Callback<List<String>>() {
                    @Override
                    public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            suggestionAdapter.setData(response.body());
                            recyclerSearchSuggestions.setVisibility(View.VISIBLE);
                        } else {
                            recyclerSearchSuggestions.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<String>> call, Throwable t) {
                        recyclerSearchSuggestions.setVisibility(View.GONE);
                    }
                });
    }



    private void loadRecommendedProducts() {

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String email = prefs.getString("email", null);

        if (email == null) return;  // chưa đăng nhập → không load gợi ý

        // 👉 Lấy userId
        RetrofitClient.getInstance().getApi().getUserByEmail(email)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (!response.isSuccessful() || response.body() == null) return;

                        Long userId = response.body().getId();

                        // 👉 Gọi API gợi ý
                        RetrofitClient.getInstance().getApi().getRecommendedProducts(userId)
                                .enqueue(new Callback<List<Product>>() {
                                    @Override
                                    public void onResponse(Call<List<Product>> call, Response<List<Product>> res) {

                                        if (res.isSuccessful() && res.body() != null) {
                                            List<Product> list = res.body();

                                            recommendedAdapter = new ProductAdapter(HomeActivity.this, list, true);
                                            recyclerRecommended.setAdapter(recommendedAdapter);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<Product>> call, Throwable t) {
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {}
                });
    }
    private void openSearchResult(String keyword) {
        Intent intent = new Intent(HomeActivity.this, ProductListActivity.class);
        intent.putExtra("searchKeyword", keyword);
        startActivity(intent);
    }
    private void startCountdown() {
        // Set the end time (ví dụ thời gian kết thúc là 5 ngày từ hiện tại)
        long endTime = System.currentTimeMillis() + (5 * 24 * 60 * 60 * 1000); // 5 ngày từ hiện tại
        final TextView tvCountdown = findViewById(R.id.tvCountdown);

        // Tạo một Handler để cập nhật mỗi giây
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long timeLeft = endTime - currentTime;

                if (timeLeft > 0) {
                    long days = timeLeft / (1000 * 60 * 60 * 24);
                    long hours = (timeLeft % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
                    long minutes = (timeLeft % (1000 * 60 * 60)) / (1000 * 60);
                    long seconds = (timeLeft % (1000 * 60)) / 1000;

                    String timeString = String.format("%d Ngày %d Giờ %d Phút %d Giây", days, hours, minutes, seconds);
                    tvCountdown.setText("Black Friday chỉ còn: " + timeString);

                    handler.postDelayed(this, 1000); // update every second
                } else {
                    tvCountdown.setText("Black Friday đã kết thúc!");
                }
            }
        };

        handler.post(runnable);  // Start the countdown
    }

    private void loadComboProducts() {
        // Gọi API để lấy sản phẩm combo
        RetrofitClient.getInstance().getApi().getComboProducts("COMBO_UU_DAI")
                .enqueue(new Callback<List<Product>>() {
                    @Override
                    public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Product> comboProducts = response.body();
                            displayComboProducts(comboProducts);
                        } else {
                            Toast.makeText(HomeActivity.this, "Không có sản phẩm combo", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Product>> call, Throwable t) {
                        Toast.makeText(HomeActivity.this, "Lỗi khi lấy sản phẩm combo", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayComboProducts(List<Product> comboProducts) {
        // Hiển thị các sản phẩm combo trong RecyclerView
        RecyclerView comboRecyclerView = findViewById(R.id.recyclerCombo); // Giả sử bạn đã có RecyclerView với ID này trong layout

        ProductAdapter comboAdapter = new ProductAdapter(this, comboProducts);
        comboRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        comboRecyclerView.setAdapter(comboAdapter);
    }
    private void openProductList(String cardName) {
        Intent intent = new Intent(HomeActivity.this, ProductListActivity.class);

        switch (cardName) {
            case "Khoảnh Khắc Tỏa Sáng":
                intent.putExtra("filterType", "MULTI_CATEGORY");
                intent.putExtra("categories", new String[]{"CHARMS", "VÒNG TAY"});
                intent.putExtra("categoryName", "Pandora Moments");
                break;
            case "Lời Gọi Biển Khơi":
                intent.putExtra("filterType", "MULTI_CATEGORY");
                intent.putExtra("categories", new String[]{"NHẪN", "DÂY CHUYỀN"});
                intent.putExtra("categoryName", "Pandora Signature");
                break;
            case "Tháng Của Xứ Nữ":
                intent.putExtra("filterType", "SUB_CATEGORY_NAME");
                intent.putExtra("subCategoryName", "Cung hoàng đạo");
                intent.putExtra("categoryName", "Tháng Của Xứ Nữ");
                break;
            case "Nơi Tình Yêu Bắt Đầu":
                intent.putExtra("filterType", "SUB_CATEGORY_NAME");
                intent.putExtra("subCategoryName", "Nhẫn đôi");
                intent.putExtra("categoryName", "Nơi Tình Yêu Bắt Đầu");
                break;
        }

        startActivity(intent);
    }
    private void initializeVideoPlayer() {
        videoView = findViewById(R.id.videoView);

        // Thiết lập video URL
        String videoPath = "https://res.cloudinary.com/dnbxsm1mx/video/upload/v1765022284/pandoravideo_m0lizf.mov";
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        // Thêm media controller
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Start video
        videoView.start();

        // Loop video
        videoView.setOnCompletionListener(mp -> videoView.start());
    }




}
