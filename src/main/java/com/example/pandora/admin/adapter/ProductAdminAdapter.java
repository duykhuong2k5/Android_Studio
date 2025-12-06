package com.example.pandora.admin.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pandora.data.entity.Product;
import com.example.pandora.R;
import com.example.pandora.data.entity.ProductDetail;
import com.example.pandora.data.network.RetrofitClient;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAdminAdapter extends RecyclerView.Adapter<ProductAdminAdapter.ProductViewHolder> {

    private final Context context;
    private final List<Product> products;

    public ProductAdminAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvPrice, tvDiscount;
        Button btnEdit, btnDelete;

        public ProductViewHolder(View view) {
            super(view);
            imgProduct = view.findViewById(R.id.imgProduct);
            tvName = view.findViewById(R.id.tvName);
            tvPrice = view.findViewById(R.id.tvPrice);
            tvDiscount = view.findViewById(R.id.tvDiscount);  // Add the discount view here
            btnEdit = view.findViewById(R.id.btnEdit);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_admin, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getPriceNew() + "₫");

        // Show discount only if applicable
        String discount = product.getDiscountPercent();
        if (discount != null && !discount.isEmpty()) {
            holder.tvDiscount.setVisibility(View.VISIBLE);
            holder.tvDiscount.setText(discount);
        } else {
            holder.tvDiscount.setVisibility(View.GONE);
        }

        // Load image using Glide
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .into(holder.imgProduct);

        // Set up the Edit button
        holder.btnEdit.setOnClickListener(v -> showEditDialog(product, position));

        // Set up the Delete button
        holder.btnDelete.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc muốn xóa sản phẩm '" + product.getName() + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteProduct(product, position))
                .setNegativeButton("Hủy", null)
                .show());
    }

    private void showEditDialog(Product product, int position) {
        // Inflate the dialog for editing product
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_product, null);

        // Khởi tạo các trường nhập liệu
        EditText edtName = dialogView.findViewById(R.id.edtProductName);
        EditText edtPrice = dialogView.findViewById(R.id.edtProductPrice);  // Giá mới
        EditText edtPriceOld = dialogView.findViewById(R.id.edtProductPriceOld);  // Giá cũ
        EditText edtDiscount = dialogView.findViewById(R.id.edtProductDiscount);  // Giảm giá
        EditText edtImage = dialogView.findViewById(R.id.edtProductImage);  // Hình ảnh sản phẩm
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);  // Danh mục sản phẩm
        EditText edtDescription = dialogView.findViewById(R.id.edtProductDescription);  // Mô tả sản phẩm
        EditText edtShippingPolicy = dialogView.findViewById(R.id.edtShippingPolicy);  // Chính sách vận chuyển

        // Set initial values for the fields
        edtName.setText(product.getName());  // Tên sản phẩm
        edtPrice.setText(product.getPriceNew());  // Giá mới
        edtPriceOld.setText(product.getPriceOld());  // Giá cũ
        edtDiscount.setText(product.getDiscountPercent());  // Giảm giá
        edtImage.setText(product.getImageUrl());  // Hình ảnh

        // Prepare categories for spinner
        List<String> categories = Arrays.asList("DÂY CHUYỀN", "CHARMS", "VÒNG TAY", "HOA TAI");
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Set the selected category in the spinner
        int categoryPosition = categories.indexOf(product.getCategory());
        spinnerCategory.setSelection(categoryPosition);

        // Gọi API để lấy thông tin chi tiết sản phẩm (nếu cần)
        RetrofitClient.getInstance().getApi().getProductDetail(product.getId())
                .enqueue(new Callback<ProductDetail>() {
                    @Override
                    public void onResponse(Call<ProductDetail> call, Response<ProductDetail> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ProductDetail productDetail = response.body();
                            // Hiển thị mô tả sản phẩm
                            if (productDetail.getDescription() != null) {
                                edtDescription.setText(productDetail.getDescription());
                            }
                            // Hiển thị chính sách vận chuyển
                            if (productDetail.getShippingPolicy() != null) {
                                edtShippingPolicy.setText(productDetail.getShippingPolicy());
                            }
                        } else {
                            Toast.makeText(context, "Không thể lấy chi tiết sản phẩm", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductDetail> call, Throwable t) {
                        Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Show the edit product dialog
        new AlertDialog.Builder(context)
                .setTitle("Chỉnh sửa sản phẩm")
                .setView(dialogView)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    // Tạo đối tượng Product mới với dữ liệu chỉnh sửa
                    Product updatedProduct = new Product(
                            product.getId(),
                            edtName.getText().toString(),
                            edtPrice.getText().toString(),
                            edtPriceOld.getText().toString(),
                            edtDiscount.getText().toString(),
                            edtImage.getText().toString(),
                            spinnerCategory.getSelectedItem().toString()
                    );

                    // Gửi yêu cầu cập nhật sản phẩm đến API
                    RetrofitClient.getInstance().getApi().updateProduct(product.getId(), updatedProduct)
                            .enqueue(new Callback<Product>() {
                                @Override
                                public void onResponse(Call<Product> call, Response<Product> response) {
                                    if (response.isSuccessful()) {
                                        // Cập nhật sản phẩm trong danh sách và thông báo
                                        products.set(position, updatedProduct);
                                        notifyItemChanged(position);
                                        Toast.makeText(context, "Đã cập nhật sản phẩm!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Product> call, Throwable t) {
                                    Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }



    private void deleteProduct(Product product, int position) {
        RetrofitClient.getInstance().getApi().deleteProduct(product.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            products.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Không thể xóa!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
