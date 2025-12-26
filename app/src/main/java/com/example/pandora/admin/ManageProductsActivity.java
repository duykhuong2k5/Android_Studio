package com.example.pandora.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.data.entity.Product;
import com.example.pandora.R;
import com.example.pandora.admin.adapter.ProductAdminAdapter;
import com.example.pandora.data.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerProducts;
    private FloatingActionButton btnAddProduct;
    private ProductAdminAdapter adapter;
    private List<Product> products = new ArrayList<>();
    private List<Product> filteredProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        recyclerProducts = findViewById(R.id.recyclerProducts);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        Spinner spinnerCategory = findViewById(R.id.spinnerCategory);  // Lấy Spinner danh mục
        EditText edtSearchProduct = findViewById(R.id.edtSearchProduct);

        recyclerProducts.setLayoutManager(new LinearLayoutManager(this));

        btnAddProduct.setOnClickListener(v -> showAddProductDialog());
        loadProducts();

        // Spinner để chọn danh mục
        List<String> categories = Arrays.asList("Tất cả", "DÂY CHUYỀN", "CHARMS", "VÒNG TAY", "HOA TAI");
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Lọc theo danh mục khi thay đổi
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCategory = parentView.getItemAtPosition(position).toString();
                // Lấy từ khóa tìm kiếm từ EditText
                String searchQuery = ((EditText) findViewById(R.id.edtSearchProduct)).getText().toString().trim();
                filterProducts(searchQuery, selectedCategory); // Gọi phương thức lọc sản phẩm
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
        edtSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String selectedCategory = spinnerCategory.getSelectedItem().toString();
                filterProducts(s.toString(), selectedCategory);   // gọi lọc mỗi khi gõ
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }


    private void loadProducts() {
        RetrofitClient.getInstance().getApi().getAllProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    products = response.body();
                    filteredProducts.clear();
                    filteredProducts.addAll(products);  // Thêm tất cả sản phẩm vào filteredProducts
                    adapter = new ProductAdminAdapter(ManageProductsActivity.this, filteredProducts);
                    recyclerProducts.setAdapter(adapter);
                } else {
                    Toast.makeText(ManageProductsActivity.this, "Không thể tải sản phẩm!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(ManageProductsActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void filterProducts(String query, String selectedCategory) {
        filteredProducts.clear();

        for (Product product : products) {
            boolean matchesName =
                    product.getName() != null &&
                            product.getName().toLowerCase().contains(query.toLowerCase());

            boolean matchesCategory =
                    selectedCategory.equals("Tất cả") ||
                            (product.getCategory() != null &&
                                    product.getCategory().equals(selectedCategory)); // hoặc equalsIgnoreCase

            if (matchesName && matchesCategory) {
                filteredProducts.add(product);
            }
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void showAddProductDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        android.view.View dialogView = inflater.inflate(R.layout.dialog_add_edit_product, null);

        EditText edtName = dialogView.findViewById(R.id.edtProductName);
        EditText edtPriceNew = dialogView.findViewById(R.id.edtProductPrice);
        EditText edtPriceOld = dialogView.findViewById(R.id.edtProductPriceOld);
        EditText edtImage = dialogView.findViewById(R.id.edtProductImage);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);  // Thêm Spinner

        // Danh mục sản phẩm
        List<String> categories = Arrays.asList("DÂY CHUYỀN", "CHARMS", "VÒNG TAY", "HOA TAI");

        // Sử dụng ArrayAdapter để nạp dữ liệu vào Spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  // Đảm bảo sử dụng drop-down item
        spinnerCategory.setAdapter(categoryAdapter);

        new AlertDialog.Builder(this)
                .setTitle("Thêm sản phẩm mới")
                .setView(dialogView)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String name = edtName.getText().toString().trim();
                    String priceNew = edtPriceNew.getText().toString().trim();
                    String priceOld = edtPriceOld.getText().toString().trim();
                    String imageUrl = edtImage.getText().toString().trim();
                    String category = spinnerCategory.getSelectedItem().toString();

                    if (name.isEmpty() || priceNew.isEmpty() || imageUrl.isEmpty() || category.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String discount = calculateDiscount(priceNew, priceOld);

                    Product newProduct = new Product(0L, name, priceNew, priceOld, discount, imageUrl, category);

                    RetrofitClient.getInstance().getApi().addProduct(newProduct)
                            .enqueue(new Callback<Product>() {
                                @Override
                                public void onResponse(Call<Product> call, Response<Product> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        products.add(response.body());
                                        filteredProducts.add(response.body());
                                        adapter.notifyItemInserted(filteredProducts.size() - 1);
                                        Toast.makeText(ManageProductsActivity.this, "Đã thêm sản phẩm mới!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ManageProductsActivity.this, "Không thể thêm sản phẩm!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Product> call, Throwable t) {
                                    Toast.makeText(ManageProductsActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }


    // Phương thức tính giảm giá
    private String calculateDiscount(String priceNew, String priceOld) {
        if (!priceOld.isEmpty() && Double.parseDouble(priceOld) > 0) {
            int percent = (int) (((Double.parseDouble(priceOld) - Double.parseDouble(priceNew)) / Double.parseDouble(priceOld)) * 100);
            return "-" + percent + "%";
        }
        return "0%";
    }

    // Chỉnh sửa sản phẩm
    private void showEditProductDialog(Product product, int position) {
        LayoutInflater inflater = LayoutInflater.from(this);
        android.view.View dialogView = inflater.inflate(R.layout.dialog_add_edit_product, null);

        EditText edtName = dialogView.findViewById(R.id.edtProductName);
        EditText edtPriceNew = dialogView.findViewById(R.id.edtProductPrice);
        EditText edtPriceOld = dialogView.findViewById(R.id.edtProductPriceOld);
        EditText edtImage = dialogView.findViewById(R.id.edtProductImage);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);  // Đảm bảo Spinner

        List<String> categories = Arrays.asList("DÂY CHUYỀN", "CHARMS", "VÒNG TAY", "HOA TAI");

        // Sử dụng ArrayAdapter để nạp dữ liệu vào Spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Cập nhật giá trị ban đầu cho các trường
        edtName.setText(product.getName());
        edtPriceNew.setText(product.getPriceNew());
        edtPriceOld.setText(product.getPriceOld());
        edtImage.setText(product.getImageUrl());

        // Chọn đúng danh mục sản phẩm trong Spinner
        int categoryPosition = categories.indexOf(product.getCategory());
        spinnerCategory.setSelection(categoryPosition);

        new AlertDialog.Builder(this)
                .setTitle("Chỉnh sửa sản phẩm")
                .setView(dialogView)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String name = edtName.getText().toString().trim();
                    String priceNew = edtPriceNew.getText().toString().trim();
                    String priceOld = edtPriceOld.getText().toString().trim();
                    String imageUrl = edtImage.getText().toString().trim();
                    String category = spinnerCategory.getSelectedItem().toString();

                    if (name.isEmpty() || priceNew.isEmpty() || imageUrl.isEmpty() || category.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String discount = calculateDiscount(priceNew, priceOld);

                    Product updatedProduct = new Product(
                            product.getId(),
                            name,
                            priceNew,
                            priceOld,
                            discount,
                            imageUrl,
                            category
                    );

                    RetrofitClient.getInstance().getApi().updateProduct(product.getId(), updatedProduct)
                            .enqueue(new Callback<Product>() {
                                @Override
                                public void onResponse(Call<Product> call, Response<Product> response) {
                                    if (response.isSuccessful()) {
                                        products.set(position, updatedProduct);
                                        filteredProducts.set(position, updatedProduct);
                                        adapter.notifyItemChanged(position);
                                        Toast.makeText(ManageProductsActivity.this, "Đã cập nhật sản phẩm!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ManageProductsActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Product> call, Throwable t) {
                                    Toast.makeText(ManageProductsActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
