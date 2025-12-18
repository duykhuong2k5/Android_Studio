package com.example.pandora.shipper;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.data.entity.OrderItem;
import com.example.pandora.data.network.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipperOrderDetailActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_IMAGE = 1001;

    TextView tvOrderId, tvStatus, tvCustomerName, tvAddress, tvPhone, tvTotal;
    Button btnDelivered, btnFailed, btnChooseProof,
            btnCallCustomer, btnOpenMap;
    RecyclerView recyclerItems;

    long orderId;
    String status;

    // thông tin khách hàng
    String customerName;
    String customerAddress;
    String customerPhone;

    Bitmap selectedBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_order_detail);

        // ====== Lấy dữ liệu từ Intent ======
        orderId = getIntent().getLongExtra("orderId", 0);
        status = getIntent().getStringExtra("status");

        customerName = getIntent().getStringExtra("customerName");
        customerAddress = getIntent().getStringExtra("address");
        customerPhone = getIntent().getStringExtra("phone");
        double total = getIntent().getDoubleExtra("total", 0);

        if (customerName == null || customerName.isEmpty()) {
            customerName = "Khách chưa rõ tên";
        }
        if (customerAddress == null || customerAddress.isEmpty()) {
            customerAddress = "Không rõ địa chỉ";
        }
        if (customerPhone == null || customerPhone.isEmpty()) {
            customerPhone = "Không rõ SĐT";
        }

        ArrayList<OrderItem> items =
                getIntent().getParcelableArrayListExtra("items");

        // ====== Ánh xạ view ======
        tvOrderId       = findViewById(R.id.tvDetailOrderId);
        tvStatus        = findViewById(R.id.tvDetailStatus);
        tvCustomerName  = findViewById(R.id.tvDetailCustomerName);
        tvAddress       = findViewById(R.id.tvDetailAddress);
        tvPhone         = findViewById(R.id.tvDetailPhone);
        tvTotal         = findViewById(R.id.tvDetailTotal);

        btnDelivered    = findViewById(R.id.btnDelivered);
        btnFailed       = findViewById(R.id.btnFailed);
        btnChooseProof  = findViewById(R.id.btnChooseProof);      // nút chọn ảnh minh chứng
        btnCallCustomer = findViewById(R.id.btnCallCustomer);
        btnOpenMap      = findViewById(R.id.btnOpenMap);

        recyclerItems = findViewById(R.id.recyclerItems);
        recyclerItems.setLayoutManager(new LinearLayoutManager(this));
        if (items != null) {
            recyclerItems.setAdapter(new ShipperOrderItemAdapter(this, items));
        }

        // ====== Set dữ liệu hiển thị ======
        tvOrderId.setText("Đơn #" + orderId);
        tvStatus.setText("Trạng thái: " + status);
        tvCustomerName.setText(customerName);
        tvAddress.setText(customerAddress);
        tvPhone.setText(customerPhone);
        tvTotal.setText(String.format("%,.0f₫", total));

        // ban đầu chưa chọn ảnh → disable nút giao thành công
        btnDelivered.setEnabled(false);

        setupButtonsByStatus();

        // ====== Gọi khách ======
        btnCallCustomer.setOnClickListener(v -> callCustomer());

        // ====== Xem bản đồ ======
        btnOpenMap.setOnClickListener(v -> openMap());

        // ====== Chọn ảnh minh chứng từ thư viện ======
        btnChooseProof.setOnClickListener(v -> pickImageFromGallery());
    }

    // ---------- ACTIONS ----------

    private void callCustomer() {
        if (customerPhone == null || customerPhone.equals("Không rõ SĐT")) {
            Toast.makeText(this, "Không có số điện thoại khách!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + customerPhone));
        startActivity(intent);
    }

    private void openMap() {
        if (customerAddress == null || customerAddress.equals("Không rõ địa chỉ")) {
            Toast.makeText(this, "Không có địa chỉ khách!", Toast.LENGTH_SHORT).show();
            return;
        }
        String query = Uri.encode(customerAddress);
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + query);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps"); // ưu tiên Google Maps

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // nếu máy không có Google Maps thì mở bất kỳ app map nào có thể
            startActivity(new Intent(Intent.ACTION_VIEW, gmmIntentUri));
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Toast.makeText(this, "Đã chọn ảnh minh chứng!", Toast.LENGTH_SHORT).show();
                btnDelivered.setEnabled(true); // chọn xong ảnh mới cho giao thành công
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Không đọc được ảnh!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // BASE64
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        String base64 = Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP);
        return "data:image/jpeg;base64," + base64;
    }

    /** Hiển thị / ẩn các nút theo trạng thái */
    private void setupButtonsByStatus() {
        if ("DELIVERING".equals(status)) {
            // đang giao → được chọn ảnh, giao thành công, giao thất bại
            btnDelivered.setVisibility(View.VISIBLE);
            btnFailed.setVisibility(View.VISIBLE);
            btnChooseProof.setVisibility(View.VISIBLE);

            btnDelivered.setOnClickListener(v -> completeOrder(orderId));
            btnFailed.setOnClickListener(v -> askFailReason(orderId));
        } else {
            // các trạng thái khác: chỉ xem, không thao tác
            btnDelivered.setVisibility(View.GONE);
            btnFailed.setVisibility(View.GONE);
            btnChooseProof.setVisibility(View.GONE);
        }
    }

    private void completeOrder(long id) {
        if (selectedBitmap == null) {
            Toast.makeText(this, "Bạn phải chọn ảnh minh chứng trước!", Toast.LENGTH_SHORT).show();
            return;
        }

        String base64 = bitmapToBase64(selectedBitmap);
        Map<String, String> body = new HashMap<>();
        body.put("image", base64);

        RetrofitClient.getInstance().getApi().completeOrder(id, body)
                .enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(ShipperOrderDetailActivity.this,
                                    "Upload lỗi: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(ShipperOrderDetailActivity.this,
                                "Giao thành công, ảnh đã upload!",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
                        Toast.makeText(ShipperOrderDetailActivity.this,
                                "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Hỏi lý do giao thất bại rồi gửi lên server
    private void askFailReason(long id) {
        final EditText input = new EditText(this);
        input.setHint("Nhập lý do giao thất bại");

        new AlertDialog.Builder(this)
                .setTitle("Lý do giao thất bại")
                .setView(input)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Gửi", (dialog, which) -> {
                    String reason = input.getText().toString().trim();
                    if (reason.isEmpty()) {
                        Toast.makeText(this, "Bạn phải nhập lý do!", Toast.LENGTH_SHORT).show();
                    } else {
                        failOrder(id, reason);
                    }
                })
                .show();
    }

    private void failOrder(long id, String reason) {
        Map<String, String> body = new HashMap<>();
        body.put("reason", reason);

        RetrofitClient.getInstance().getApi().failedOrder(id, body)
                .enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                        Toast.makeText(ShipperOrderDetailActivity.this,
                                "Đã báo giao thất bại!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
                        Toast.makeText(ShipperOrderDetailActivity.this,
                                "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
