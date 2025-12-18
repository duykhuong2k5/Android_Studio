package com.example.pandora.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.address.SelectAddressActivity;
import com.example.pandora.data.entity.*;
import com.example.pandora.data.network.RetrofitClient;
import com.example.pandora.ui.adapter.CartAdapter;
import com.example.pandora.ui.payment.VnpayActivity;
import com.example.pandora.ui.profile.ProfileActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.NumberFormat;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerCart;
    private RadioGroup radioPaymentMethod;
    private Button btnCheckout;
    private CartAdapter cartAdapter;

    private List<CartItem> cartItems;

    long selectedAddressId = -1;   // <--- Lưu địa chỉ người dùng chọn

    private double voucherDiscount = 0;  // số tiền giảm từ voucher
    private static final int SHIPPING_FEE = 0;
    private String selectedVoucherCode = null;

    private TextView tvTotalPrice, tvSubtotal, tvShippingFee, tvDiscount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        tvSubtotal       = findViewById(R.id.tvSubtotal);
        tvShippingFee    = findViewById(R.id.tvShippingFee);
        tvDiscount       = findViewById(R.id.tvDiscount);
        tvTotalPrice     = findViewById(R.id.tvTotalPrice);


        ImageButton btnProfile = findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        recyclerCart = findViewById(R.id.recyclerCart);
        radioPaymentMethod = findViewById(R.id.radioPaymentMethod);
        btnCheckout = findViewById(R.id.btnCheckout);
        cartItems = new ArrayList<>(CartManager.getCartItems());
        cartAdapter = new CartAdapter(cartItems, () -> updateTotalPrice(cartItems));
        recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerCart.setAdapter(cartAdapter);
        Button btnVoucher = findViewById(R.id.btnVoucher);

        updateTotalPrice(cartItems);

        // -------------------------------
        // 1) MỞ TRANG CHỌN ĐỊA CHỈ
        // -------------------------------
        btnCheckout.setOnClickListener(v -> {
            Intent i = new Intent(this, SelectAddressActivity.class);
            startActivityForResult(i, 1001);
        });

        btnVoucher.setOnClickListener(v -> loadVoucherFromServer());


    }
    // ============================
    // LOAD VOUCHER FROM BACKEND
    // ============================
    private void loadVoucherFromServer() {
        RetrofitClient.getInstance().getApi()
                .getAllVouchers()
                .enqueue(new Callback<List<VoucherDTO>>() {
                    @Override
                    public void onResponse(Call<List<VoucherDTO>> call, Response<List<VoucherDTO>> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(CartActivity.this, "Không thể tải voucher!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        showVoucherPopupFromDB(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<VoucherDTO>> call, Throwable t) {
                        Toast.makeText(CartActivity.this, "Lỗi kết nối server!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // ============================
    // HIỂN THỊ BOTTOMSHEET VOUCHER
    // ============================
    private void showVoucherPopupFromDB(List<VoucherDTO> vouchers) {

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.voucher_bottom_sheet);

        LinearLayout container = dialog.findViewById(R.id.voucherContainer);
        Button btnCancel = dialog.findViewById(R.id.btnVoucherCancel);

        if (container == null) return;

        container.removeAllViews();

        for (VoucherDTO v : vouchers) {
            Button btn = new Button(this);
            btn.setAllCaps(false);
            btn.setText(v.getCode() + " - " + v.getDescription());
            btn.setPadding(16,16,16,16);

            btn.setOnClickListener(x -> {
                applyVoucher(v.getCode());
                dialog.dismiss();
            });

            container.addView(btn);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    // ============================
    // APPLY VOUCHER VIA API
    // ============================
    private void applyVoucher(String code) {
        double subtotal = calculateSubtotal(cartItems);

        RetrofitClient.getInstance().getApi()
                .applyVoucher(code, subtotal)
                .enqueue(new Callback<VoucherResponseDTO>() {

                    @Override
                    public void onResponse(Call<VoucherResponseDTO> call, Response<VoucherResponseDTO> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(CartActivity.this, "Không áp dụng được voucher!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        VoucherResponseDTO data = response.body();

                        if (!data.isSuccess()) {
                            Toast.makeText(CartActivity.this, data.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        selectedVoucherCode = code;
                        voucherDiscount = data.getDiscountAmount();



                        Toast.makeText(CartActivity.this, "Đã áp dụng: " + code, Toast.LENGTH_SHORT).show();

                        updateTotalPrice(cartItems);
                    }

                    @Override
                    public void onFailure(Call<VoucherResponseDTO> call, Throwable t) {
                        Toast.makeText(CartActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ==========================
    // NHẬN ĐỊA CHỈ TRẢ VỀ
    // ==========================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            selectedAddressId = data.getLongExtra("address_id", -1);

            if (selectedAddressId != -1) {
                processCheckout();  // → tiếp tục tạo đơn
            }
        }
    }

    // ==========================
    // TIẾP TỤC CHECKOUT
    // ==========================
    private void processCheckout() {

        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng đang trống", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isVnpay = radioPaymentMethod.getCheckedRadioButtonId() == R.id.radioVnPay;

        if (selectedAddressId == -1) {
            Toast.makeText(this, "Vui lòng chọn địa chỉ giao hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        createOrder(isVnpay, selectedAddressId);
    }
    // ======= TÍNH SUBTOTAL =======
    private int calculateSubtotal(List<CartItem> items) {
        int total = 0;
        for (CartItem it : items) {
            if (it.isSelected()) {
                total += parsePrice(it.getProduct().getPriceNew()) * it.getQuantity();
            }
        }
        return total;
    }

    // ======= TẠO ĐƠN =======
    private void createOrder(boolean isVnpay, long addressId) {

        long userId = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getLong("user_id", -1);

        if (userId == -1L) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        double subtotal = 0;
        List<OrderItemRequest> orderItems = new ArrayList<>();

        for (CartItem item : cartItems) {
            if (!item.isSelected()) continue;

            double price = parsePrice(item.getProduct().getPriceNew());
            subtotal += price * item.getQuantity();

            orderItems.add(new OrderItemRequest(
                    new ProductDTO(item.getProduct().getId()),
                    item.getQuantity(),
                    price
            ));
        }

        if (orderItems.isEmpty()) {
            Toast.makeText(this, "Bạn chưa chọn sản phẩm!", Toast.LENGTH_SHORT).show();
            return;
        }

        int shippingFee = 0;

        double finalTotal = subtotal + shippingFee - voucherDiscount;
        if (finalTotal < 0) finalTotal = 0;
        String status = "PENDING";


        OrderRequest orderRequest = new OrderRequest(
                new UserDTO(userId),
                finalTotal,
                status,
                orderItems
        );

        orderRequest.setAddress(new AddressDTO(addressId));
        orderRequest.setDiscount(voucherDiscount);
        orderRequest.setFreeShip(true);
        orderRequest.setShippingFee(0);


        RetrofitClient.getInstance().getApi()
                .addOrder(orderRequest)
                .enqueue(new Callback<Order>() {

                    @Override
                    public void onResponse(Call<Order> call, Response<Order> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(CartActivity.this, "Không tạo được đơn!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Order order = response.body();

                        if (!isVnpay) {
                            Toast.makeText(CartActivity.this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                            CartManager.clearCart();
                            cartItems.clear();
                            cartAdapter.notifyDataSetChanged();
                            updateTotalPrice(Collections.emptyList());
                        } else {
                            createVnpayTransaction(order);
                        }
                    }

                    @Override
                    public void onFailure(Call<Order> call, Throwable t) {
                        Toast.makeText(CartActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ==========================
    // TẠO GIAO DỊCH VNPAY
    // ==========================
    private void createVnpayTransaction(Order order) {

        TransactionCreateRequest req = new TransactionCreateRequest(
                order.getUser().getId(),
                order.getId(),
                (long) order.getTotalPrice(),
                "Thanh toán đơn hàng #" + order.getId()
        );

        RetrofitClient.getInstance().getApi()
                .createVnPayPayment(req)
                .enqueue(new Callback<ResponseDTO>() {

                    @Override
                    public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {

                        if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                            Toast.makeText(CartActivity.this, "Tạo giao dịch VNPay thất bại!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String url = response.body().getData().toString();

                        Intent intent = new Intent(CartActivity.this, VnpayActivity.class);
                        intent.putExtra("payment_url", url);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<ResponseDTO> call, Throwable t) {
                        Toast.makeText(CartActivity.this, "Không kết nối được VNPay!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ================== TIỆN ÍCH ===================== //
    private int parsePrice(String price) {
        if (price == null || price.isEmpty()) return 0;
        try {
            return (int) Double.parseDouble(price.replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private void updateTotalPrice(List<CartItem> items) {
        int subtotal = calculateSubtotal(items);
        int shipping = 0;

        int finalTotal = subtotal + shipping - (int) voucherDiscount;
        if (finalTotal < 0) finalTotal = 0;

        tvSubtotal.setText("Tổng tiền hàng: " + formatPrice(subtotal));
        tvShippingFee.setText("Phí vận chuyển: " + formatPrice(shipping));
        tvDiscount.setText("Giảm giá: " + formatPrice(voucherDiscount));
        tvTotalPrice.setText("Tổng cộng: " + formatPrice(finalTotal));
    }



    private String formatPrice(Number price) {
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return nf.format(price) + "₫";
    }



}
