package com.example.pandora.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.pandora.data.entity.ProductImage;
import com.example.pandora.data.entity.ProductSize;
import com.example.pandora.data.entity.ReviewRequest;
import com.example.pandora.ui.adapter.ImageSlideUrlAdapter;
import com.example.pandora.R;
import com.example.pandora.chat.ChatActivity;
import com.example.pandora.data.entity.CartItem;
import com.example.pandora.data.entity.Product;
import com.example.pandora.data.entity.ProductDetail;
import com.example.pandora.data.entity.Review;
import com.example.pandora.data.network.RetrofitClient;
import com.example.pandora.ui.adapter.ImageSlideAdapter;
import com.example.pandora.ui.adapter.ProductAdapter;
import com.example.pandora.ui.adapter.ReviewAdapter;
import com.example.pandora.ui.adapter.SizeAdapter;
import com.example.pandora.ui.cart.CartManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import androidx.recyclerview.widget.GridLayoutManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvProductName, tvProductPrice, tvPolicy, tvCompatibilityText, tvReviews;
    private ImageView imgCompatibility;
    private RecyclerView recyclerRelated;
    private List<Product> relatedList = new java.util.ArrayList<>();
    private ProductAdapter relatedAdapter;
    private RecyclerView recyclerReviews;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList = new ArrayList<>();
    private ViewPager2 viewPager;
    private TabLayout tabIndicator;
    private ImageSlideUrlAdapter imageAdapter;
    private List<String> imageUrls = new ArrayList<>();

    // ----- SIZE -----
    private RecyclerView recyclerSizes;
    private SizeAdapter sizeAdapter;
    private List<ProductSize> sizeList = new ArrayList<>();
    private String selectedSizeLabel;
    private ImageView ivToggleSize;
    private View cardSize;
    private boolean isSizeExpanded = false;
    private LinearLayout containerDescription;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ImageButton btnCart = findViewById(R.id.btnCart);
        ImageButton btnProfile = findViewById(R.id.btnProfile);
        viewPager = findViewById(R.id.viewPagerImages);
        tabIndicator = findViewById(R.id.tabIndicator);

        Button btnBuyNow = findViewById(R.id.btnBuyNow);
        Button btnAddToCart = findViewById(R.id.btnAddToCart);
        FloatingActionButton btnChatSupport = findViewById(R.id.btnChatSupport);

        tvProductName = findViewById(R.id.tvProductNameDetail);
        tvProductPrice = findViewById(R.id.tvProductPriceDetail);
        //tvDescription = findViewById(R.id.tvProductDescription);
        containerDescription = findViewById(R.id.containerDescription);
        tvPolicy = findViewById(R.id.tvPolicy);
        tvCompatibilityText = findViewById(R.id.tvCompatibilityText);
        imgCompatibility = findViewById(R.id.imgCompatibility);
        tvReviews = findViewById(R.id.tvReviews);

        // ----- SIZE VIEW -----
        cardSize = findViewById(R.id.cardSize);
        ivToggleSize = findViewById(R.id.ivToggleSize);
        recyclerSizes = findViewById(R.id.recyclerSizes);
        sizeAdapter = new SizeAdapter(sizeList, size -> {
            if (size == null) {
                // user vừa bấm lại để bỏ chọn
                selectedSizeLabel = null;
            } else {
                selectedSizeLabel = size.getSizeLabel();
            }
        });


        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerSizes.setLayoutManager(gridLayoutManager);
        recyclerSizes.setAdapter(sizeAdapter);

        // ban đầu ẩn bảng size
        recyclerSizes.setVisibility(View.GONE);
        isSizeExpanded = false;

        // click dấu +
        ivToggleSize.setOnClickListener(v -> {
            isSizeExpanded = !isSizeExpanded;
            recyclerSizes.setVisibility(isSizeExpanded ? View.VISIBLE : View.GONE);

            // đổi icon + / - nếu bạn có
            ivToggleSize.setImageResource(
                    isSizeExpanded ? R.drawable.ic_remove_24 : R.drawable.ic_add_24
            );
        });



        btnCart.setOnClickListener(v -> startActivity(new Intent(this, com.example.pandora.ui.cart.CartActivity.class)));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, com.example.pandora.ui.profile.ProfileActivity.class)));

        btnChatSupport.setOnClickListener(v -> {
            long userId = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    .getLong("user_id", -1L);
            long productId = getIntent().getLongExtra("productId", 0L);

            if (userId == -1L) {
                Toast.makeText(this, "Vui lòng đăng nhập để chat hỗ trợ!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (productId <= 0) {
                Toast.makeText(this, "Không xác định được sản phẩm để chat!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("currentUserId", userId);  // 👈 trùng với ChatActivity
            intent.putExtra("productId", productId);
            intent.putExtra("isAdmin", false);         // khách hàng
            startActivity(intent);
        });


        recyclerReviews = findViewById(R.id.recyclerReviews);
        reviewAdapter = new ReviewAdapter(reviewList);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerReviews.setAdapter(reviewAdapter);


        // ✅ Lấy dữ liệu sản phẩm
        // Lấy dữ liệu Intent
        long productId = getIntent().getLongExtra("productId", 0L);
        String category = getIntent().getStringExtra("productCategory");
        Log.d("PRODUCT_DETAIL", "CATEGORY nhận được = " + category);

    // 1️⃣ Setup RecyclerView trước
        recyclerRelated = findViewById(R.id.recyclerRelatedProducts);
        relatedAdapter = new ProductAdapter(this, relatedList);
        recyclerRelated.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerRelated.setAdapter(relatedAdapter);

    // 2️⃣ Sau đó mới load sản phẩm liên quan
        loadRelatedProducts(category, productId);




        String productName = getIntent().getStringExtra("productName");
        String productPrice = getIntent().getStringExtra("productPrice");
        String productImageUrl = getIntent().getStringExtra("productImageUrl");

        tvProductName.setText(productName);
        tvProductPrice.setText(formatPrice(productPrice));

        // 👉 Chuẩn bị list URL ảnh
        imageUrls.clear();
        if (productImageUrl != null && !productImageUrl.isEmpty()) {
            imageUrls.add(productImageUrl);     // ảnh chính
        }

        // Adapter dùng URL
        imageAdapter = new ImageSlideUrlAdapter(this, imageUrls);
        viewPager.setAdapter(imageAdapter);

        new TabLayoutMediator(tabIndicator, viewPager, (tab, position) -> {}).attach();


        tabIndicator.post(() -> {
            ViewGroup tabLayout = (ViewGroup) tabIndicator.getChildAt(0);
            for (int i = 0; i < tabLayout.getChildCount(); i++) {
                View tab = tabLayout.getChildAt(i);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
                params.width = 6;     // nhỏ hơn
                params.height = 6;
                params.setMargins(4, 0, 4, 0);
                tab.setLayoutParams(params);
            }
        });


        // ✅ Load chi tiết & review
        loadProductDetail(productId);
        loadProductReviews(productId);
        loadProductImages(productId);
        loadProductSizes(productId);

        // ===== BUTTONS =====
        btnBuyNow.setOnClickListener(v -> {
            if (!sizeList.isEmpty() &&
                    (selectedSizeLabel == null || selectedSizeLabel.isEmpty())) {
                Toast.makeText(this, "Vui lòng chọn size trước khi mua!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Mua ngay thành công!", Toast.LENGTH_SHORT).show();
        });



        btnAddToCart.setOnClickListener(v -> {

            // Nếu sản phẩm có size mà user chưa chọn size
            if (!sizeList.isEmpty() &&
                    (selectedSizeLabel == null || selectedSizeLabel.isEmpty())) {
                Toast.makeText(this, "Vui lòng chọn size!", Toast.LENGTH_SHORT).show();
                return;
            }

            Product product = new Product(
                    productId, productName, productPrice, "", "-20%", productImageUrl, "CHARMS"
            );
            CartItem item = new CartItem(product);

            // ⚠️ Nhớ thêm field sizeLabel vào CartItem nếu chưa có
            item.setSizeLabel(selectedSizeLabel);

            CartManager.addToCart(item);
            Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
        });

        RatingBar ratingBar = findViewById(R.id.ratingBar);
        EditText etReviewContent = findViewById(R.id.etReviewContent);
        Button btnSubmitReview = findViewById(R.id.btnSubmitReview);

        btnSubmitReview.setOnClickListener(v -> {
            int rating = (int) ratingBar.getRating();
            String comment = etReviewContent.getText().toString().trim();

            if (comment.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập nội dung đánh giá!", Toast.LENGTH_SHORT).show();
                return;
            }

            long userId = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    .getLong("user_id", -1L);

            if (userId == -1L) {
                Toast.makeText(this, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
                return;
            }

            ReviewRequest request = new ReviewRequest(
                    userId,
                    rating,
                    comment,
                    null
            );

            RetrofitClient.getInstance().getApi()
                    .addReview(productId, request)
                    .enqueue(new Callback<Review>() {
                        @Override
                        public void onResponse(Call<Review> call, Response<Review> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(ProductDetailActivity.this,
                                        "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_SHORT).show();
                                etReviewContent.setText("");
                                ratingBar.setRating(5f);
                                loadProductReviews(productId);
                            } else {
                                String msg = "Không thể gửi đánh giá!";
                                if (response.code() == 403) {
                                    msg = "Bạn chỉ có thể đánh giá sản phẩm đã mua và đơn hàng đã hoàn thành.";
                                }
                                Toast.makeText(ProductDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Review> call, Throwable t) {
                            Toast.makeText(ProductDetailActivity.this,
                                    "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

    }

    private void loadProductDetail(long productId) {
        RetrofitClient.getInstance().getApi().getProductDetail(productId)
                .enqueue(new Callback<ProductDetail>() {
                    @Override
                    public void onResponse(Call<ProductDetail> call, Response<ProductDetail> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ProductDetail detail = response.body();
                            String desc = detail.getDescription();
                            if (desc == null || desc.trim().isEmpty()) {
                                renderDescription("Không có mô tả chi tiết.");
                            } else {
                                renderDescription(desc);
                            }
                            tvPolicy.setText(detail.getShippingPolicy() != null ? detail.getShippingPolicy() : "Pandora miễn phí vận chuyển toàn quốc cho mọi đơn hàng.");
                            tvCompatibilityText.setText(detail.getCompatibility() != null ? detail.getCompatibility() : "");
                            if (detail.getCompatibilityImageUrl() != null && !detail.getCompatibilityImageUrl().isEmpty()) {
                                Glide.with(ProductDetailActivity.this)
                                        .load(detail.getCompatibilityImageUrl())
                                        .placeholder(R.drawable.placeholder)
                                        .into(imgCompatibility);
                            } else {
                                imgCompatibility.setVisibility(View.GONE);
                            }
                        } else {
                            Log.e("API_DETAIL", "Không tải được chi tiết sản phẩm");
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductDetail> call, Throwable t) {
                        Log.e("API_ERROR", "Lỗi: " + t.getMessage());
                    }
                });
    }
    private void renderDescription(String text) {
        containerDescription.removeAllViews();
        if (text == null || text.trim().isEmpty()) return;

        String[] lines = text.split("\n");

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // Dòng tiêu đề (kết thúc bằng ":" hoặc viết hoa đầu câu)
            if (line.endsWith(":") || line.matches("^[A-ZÀÁẠÂĂĐÊÔƠƯ].*")) {
                TextView tv = new TextView(this);
                tv.setText(line);
                tv.setTextSize(15);
                tv.setTextColor(getColor(R.color.onSurface));
                tv.setTypeface(tv.getTypeface(), android.graphics.Typeface.BOLD);
                tv.setPadding(0, 16, 0, 4);
                containerDescription.addView(tv);
            }
            // Dòng kiểu "Key: Value"
            else if (line.contains(":")) {
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setPadding(0, 4, 0, 4);

                String[] parts = line.split(":", 2);

                TextView key = new TextView(this);
                key.setText(parts[0] + ": ");
                key.setTextSize(14);
                key.setTypeface(key.getTypeface(), android.graphics.Typeface.BOLD);
                key.setTextColor(getColor(R.color.onSurface));

                TextView value = new TextView(this);
                value.setText(parts[1].trim());
                value.setTextSize(14);
                value.setTextColor(getColor(R.color.onSurfaceVariant));

                row.addView(key);
                row.addView(value);
                containerDescription.addView(row);
            }
            // Dòng mô tả thường
            else {
                TextView tv = new TextView(this);
                tv.setText(line);
                tv.setTextSize(14);
                tv.setTextColor(getColor(R.color.onSurfaceVariant));
                tv.setLineSpacing(4, 1f);
                tv.setPadding(0, 4, 0, 4);
                containerDescription.addView(tv);
            }
        }
    }


    private void loadProductReviews(long productId) {
        RetrofitClient.getInstance().getApi().getReviewsByProduct(productId)
                .enqueue(new Callback<List<Review>>() {
                    @Override
                    public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Review> reviews = response.body();

                            // ⚡ Cập nhật RecyclerView danh sách đánh giá
                            reviewList.clear();
                            reviewList.addAll(reviews);
                            reviewAdapter.notifyDataSetChanged();

                            // ⚡ Tính rating trung bình
                            if (!reviews.isEmpty()) {
                                int total = reviews.size();
                                int sum = 0;
                                for (Review r : reviews) {
                                    sum += r.getRating();
                                }
                                double avg = (double) sum / total;
                                tvReviews.setText("⭐ " + String.format("%.1f", avg) + " / 5 từ " + total + " đánh giá");
                            } else {
                                tvReviews.setText("Chưa có đánh giá nào");
                            }
                        } else {
                            tvReviews.setText("Không thể tải đánh giá");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Review>> call, Throwable t) {
                        tvReviews.setText("Không thể tải đánh giá");
                        Log.e("API_ERROR", "Lỗi review: " + t.getMessage());
                    }
                });
    }

    private void loadRelatedProducts(String category, long excludeId) {
        Log.d("RELATED_API", "Gọi API với category = " + category);
        RetrofitClient.getInstance().getApi()
                .getRelatedProducts(category, excludeId)
                .enqueue(new Callback<List<Product>>() {
                    @Override
                    public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            relatedList.clear();
                            relatedList.addAll(response.body());
                            relatedAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Product>> call, Throwable t) {}
                });
    }

    // ----- LOAD SIZES -----
    private void loadProductSizes(long productId) {
        RetrofitClient.getInstance().getApi()
                .getProductSizes(productId)
                .enqueue(new Callback<List<ProductSize>>() {
                    @Override
                    public void onResponse(Call<List<ProductSize>> call,
                                           Response<List<ProductSize>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            sizeList.clear();
                            sizeList.addAll(response.body());
                            sizeAdapter.notifyDataSetChanged();

                            if (sizeList.isEmpty()) {
                                cardSize.setVisibility(View.GONE);
                            } else {
                                cardSize.setVisibility(View.VISIBLE);

                            }
                        } else {
                            cardSize.setVisibility(View.GONE);
                            Log.e("API_SIZE", "Không tải được size");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ProductSize>> call, Throwable t) {
                        cardSize.setVisibility(View.GONE);
                        Log.e("API_SIZE", "Lỗi size: " + t.getMessage());
                    }
                });
    }



    private String formatPrice(Object price) {
        if (price == null) return "";
        try {
            String cleaned = price.toString().replaceAll("[^0-9.]", "");
            double num = Double.parseDouble(cleaned);
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            return nf.format(num) + "₫";
        } catch (Exception e) {
            return price.toString();
        }
    }
    private void loadProductImages(long productId) {
        RetrofitClient.getInstance().getApi()
                .getProductImages(productId)
                .enqueue(new Callback<List<ProductImage>>() {
                    @Override
                    public void onResponse(Call<List<ProductImage>> call,
                                           Response<List<ProductImage>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<ProductImage> list = response.body();

                            // thêm URL ảnh phụ vào list, tránh trùng ảnh chính
                            for (ProductImage img : list) {
                                if (img.getImageUrl() != null
                                        && !img.getImageUrl().isEmpty()
                                        && !imageUrls.contains(img.getImageUrl())) {
                                    imageUrls.add(img.getImageUrl());
                                }
                            }

                            imageAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("API_IMAGES", "Không tải được ảnh phụ");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ProductImage>> call, Throwable t) {
                        Log.e("API_IMAGES", "Lỗi ảnh phụ: " + t.getMessage());
                    }
                });
    }

}
