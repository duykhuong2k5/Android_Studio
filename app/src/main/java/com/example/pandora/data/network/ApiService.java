package com.example.pandora.data.network;

import com.example.pandora.data.entity.Address;
import com.example.pandora.data.entity.ChatMessage;
import com.example.pandora.data.entity.CreateStoryRequest;
import com.example.pandora.data.entity.FavoriteDTO;
import com.example.pandora.data.entity.GameFinishRequest;
import com.example.pandora.data.entity.GameSessionResponse;
import com.example.pandora.data.entity.GameStartRequest;
import com.example.pandora.data.entity.LoginRequest;
import com.example.pandora.data.entity.ProductImage;
import com.example.pandora.data.entity.ProductSize;
import com.example.pandora.data.entity.ResponseDTO;
import com.example.pandora.data.entity.RevenueSummaryResponse;
import com.example.pandora.data.entity.ReviewRequest;
import com.example.pandora.data.entity.StoryResponse;
import com.example.pandora.data.entity.TextToSpeechRequest;
import com.example.pandora.data.entity.TransactionCreateRequest;
import com.example.pandora.data.entity.User;
import com.example.pandora.data.entity.Product;
import com.example.pandora.data.entity.ProductDetail;
import com.example.pandora.data.entity.Order;
import com.example.pandora.data.entity.OrderRequest;
import com.example.pandora.data.entity.FavoriteRequest;
import com.example.pandora.data.entity.Review;
import com.example.pandora.data.entity.VocabularyWordResponse;
import com.example.pandora.data.entity.VoucherDTO;
import com.example.pandora.data.entity.VoucherResponseDTO;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiService {
    @POST("v1/game/sessions/start")
    Call<ApiResponse<GameSessionResponse>> startGame(
            @Body GameStartRequest request
    );

    @POST("v1/game/sessions/{sessionId}/finish")
    Call<ApiResponse<GameSessionResponse>> finishGame(
            @Path("sessionId") long sessionId,
            @Body GameFinishRequest request
    );

    // ===== VOCABULARY =====

    @GET("v1/vocab")
    Call<ApiResponse<List<VocabularyWordResponse>>> getVocab(
            @Query("topic") String topic,
            @Query("level") int level
    );


    // 🧍‍♂️ Tài khoản người dùng
    @POST("users/register")
    Call<Map<String, String>> registerUser(@Body User user);

    @POST("users/login")
    Call<Map<String, String>> loginUser(@Body LoginRequest request);
    @GET("users/email")
    Call<User> getUserByEmail(@Query("email") String email);

    @GET("users/{id}")
    Call<User> getUserById(@Path("id") Long id);

    // 🧑‍💻 Lấy danh sách người dùng (chỉ ADMIN)
    @GET("users/all")
    Call<List<User>> getAllUsers(@Query("email") String email);

    // ✅ Cập nhật & xóa người dùng
    @PUT("users/{id}")
    Call<User> updateUser(@Path("id") Long id, @Body User user);

    //Tạo người dùng
    @POST("users/admin-create")
    Call<User> createUserByAdmin(
            @Query("adminEmail") String adminEmail,
            @Body User user
    );


    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") Long id);


    // 🛍️ Sản phẩm
    @GET("products/all")
    Call<List<Product>> getAllProducts();

    @POST("products/add")
    Call<Product> addProduct(@Body Product product);

    @PUT("products/{id}")
    Call<Product> updateProduct(@Path("id") Long id, @Body Product product);

    @DELETE("products/{id}")
    Call<Void> deleteProduct(@Path("id") Long id);

    @GET("products/{id}/detail")
    Call<ProductDetail> getProductDetail(@Path("id") Long id);

    @PUT("products/{id}/detail")
    Call<ProductDetail> updateProductDetail(@Path("id") Long id, @Body ProductDetail detail);

    @GET("products/related/{category}/{excludeId}")
    Call<List<Product>> getRelatedProducts(
            @Path("category") String category,
            @Path("excludeId") Long excludeId
    );
    // ⭐ Lấy số lượng đã bán của 1 sản phẩm
    @GET("products/{id}/sold")
    Call<Long> getProductSold(@Path("id") Long productId);

    @GET("products/recommend/{userId}")
    Call<List<Product>> getRecommendedProducts(@Path("userId") Long userId);


    @GET("products/suggestions")
    Call<List<String>> getSuggestions(@Query("q") String keyword);

    @GET("products/search")
    Call<List<Product>> searchProducts(@Query("q") String keyword);

    @GET("products/{id}/images")
    Call<List<ProductImage>> getProductImages(@Path("id") long productId);
    @GET("products/{id}/sizes")
    Call<List<ProductSize>> getProductSizes(@Path("id") Long productId);

    @GET("products/combo")
    Call<List<Product>> getComboProducts(@Query("category") String comboCategory);

    // 📦 Đơn hàng (Lịch sử mua hàng)
    @GET("orders/{userId}")
    Call<List<Order>> getOrdersByUser(@Path("userId") Long userId);

    // 🔥 Thêm API lấy chi tiết 1 đơn
    @GET("orders/detail/{id}")
    Call<Order> getOrderDetail(@Path("id") long id);

    // Hủy đơn
    @PUT("orders/{id}/cancel")
    Call<Map<String, String>> cancelOrder(
            @Path("id") long id,
            @Body Map<String, String> reason
    );

    @POST("orders/add")
    Call<Order> addOrder(@Body OrderRequest orderRequest);

    // 📦 Quản lý đơn hàng (ADMIN)
    @GET("orders/all")
    Call<List<Order>> getAllOrders();

    @PUT("orders/{id}/status")
    Call<Order> updateOrderStatus(@Path("id") Long id, @Body Map<String, String> status);

    @DELETE("orders/{id}")
    Call<Void> deleteOrder(@Path("id") Long id);



    // ❤️ Sản phẩm yêu thích
    @GET("favorites/{userId}")
    Call<List<FavoriteDTO>> getFavoritesByUser(@Path("userId") Long userId);

    @POST("favorites/add")
    Call<FavoriteDTO> addFavorite(@Body FavoriteRequest favoriteRequest);

    @DELETE("favorites/{id}")
    Call<Void> deleteFavorite(@Path("id") Long id);




    // 📝 Đánh giá sản phẩm
    @GET("products/{id}/reviews")
    Call<List<Review>> getReviewsByProduct(@Path("id") Long id);

    @POST("products/{id}/reviews")
    Call<Review> addReview(@Path("id") Long id, @Body Review review);

    @GET("reviews/all")
    Call<List<Review>> getAllReviews();

    @DELETE("reviews/{id}")
    Call<Void> deleteReview(@Path("id") Long id);
    @POST("reviews/{productId}/reviews")
    Call<Review> addReview(
            @Path("productId") Long productId,
            @Body ReviewRequest request
    );
    @GET("reviews/admin")
    Call<List<Review>> getAllReviewsForAdmin();




    // 💬 Chat
    // 💬 Gửi tin nhắn
    @POST("chat/send")
    Call<ChatMessage> sendMessage(@Body ChatMessage message);

    // Lấy tin nhắn theo sản phẩm
    @GET("chat/product/{productId}")
    Call<List<ChatMessage>> getMessagesByProduct(@Path("productId") long productId);

    // Lấy admin ID
    @GET("chat/admin/id")
    Call<Long> getAdminId();

    // Auto gửi tin nhắn chào khi user vào xem sản phẩm
    @POST("chat/auto-start")
    Call<ChatMessage> autoStartChat(
            @Query("userId") long userId,
            @Query("productId") long productId
    );

    // Danh sách chat cho admin
    @GET("chat/list/details")
    Call<List<Object[]>> getChatListDetails();



    // 📊 Thống kê
    @GET("orders/stats/{userId}")
    Call<Map<String, Object>> getUserStats(@Path("userId") Long userId);

    @GET("orders/stats")
    Call<Map<String, Object>> getOrderStats();

    @POST("auth/verify-otp")
    Call<Map<String, String>> verifyOtp(@Body Map<String, String> data);

    @POST("auth/forgot-password")
    Call<Map<String, String>> sendOtp(@Body Map<String, String> data);

    @POST("auth/reset-password")
    Call<Map<String, String>> resetPassword(@Body Map<String, String> data);

    @GET("addresses")
    Call<List<Address>> getAddresses(@Query("userId") long userId);

    @POST("addresses")
    Call<Map<String, String>> addAddress(@Body Map<String, Object> body);

    @PUT("addresses/{id}")
    Call<Map<String, String>> updateAddress(@Path("id") long id,
                                            @Body Map<String, Object> body);

    @DELETE("addresses/{id}")
    Call<Map<String, String>> deleteAddress(@Path("id") long id);

    @PUT("addresses/{id}/default")
    Call<Map<String, String>> setDefaultAddress(@Path("id") long id);

    // ---------------- SHIPPER WORKFLOW ----------------

    // Manager duyệt đơn → WAITING_SHIPPER
    @PUT("orders/{id}/approve")
    Call<Map<String, String>> approveOrder(@Path("id") Long id);

    // Shipper nhận đơn → DELIVERING
    @PUT("orders/{id}/shipper-accept")
    Call<Map<String, String>> shipperAccept(@Path("id") Long id);
    @PUT("orders/{id}/deliver")
    Call<Map<String, Object>> deliverOrder(@Path("id") long id);


    // Shipper hoàn thành giao hàng → COMPLETED
    // 🔥 gửi Base64 trong body
    @PUT("orders/{id}/complete")
    Call<Map<String, String>> completeOrder(
            @Path("id") Long id,
            @Body Map<String, String> body
    );

    // Giao thất bại → FAILED (có lý do)
    @PUT("orders/{id}/failed")
    Call<Map<String, String>> failedOrder(
            @Path("id") Long id,
            @Body Map<String, String> body
    );


    // ----------------- VNPAY PAYMENT -----------------

    // Tạo URL thanh toán
    @POST("payment/create")
    Call<ResponseDTO> createVnPayPayment(@Body TransactionCreateRequest request);

    // Lấy kết quả thanh toán (optional, dùng nếu cần FE xem kết quả)
    @GET("payment/vnpay_return")
    Call<ResponseDTO> handleVnPayReturn(@QueryMap Map<String, String> params);


    // Lấy toàn bộ voucher
    @GET("vouchers")
    Call<List<VoucherDTO>> getAllVouchers();

    // Kiểm tra + áp dụng voucher
    @GET("vouchers/apply")
    Call<VoucherResponseDTO> applyVoucher(
            @Query("code") String code,
            @Query("total") double total
    );
    @GET("orders/revenue/summary")
    Call<RevenueSummaryResponse> getRevenueSummary(
            @Query("year") String year,
            @Query("month") String month
    );

    @POST("stories/create")
    Call<ApiResponse<StoryResponse>> createStory(@Body CreateStoryRequest request);

    @GET("stories/{id}")
    Call<ApiResponse<StoryResponse>> getStoryById(@Path("id") int id);

    @GET("stories/user/{userId}")
    Call<ApiResponse<List<StoryResponse>>> getUserStories(@Path("userId") int userId);

    @DELETE("stories/{id}")
    Call<ApiResponse<Void>> deleteStory(@Path("id") int id);

    @GET("stories/topics")
    Call<ApiResponse<List<String>>> getTopics();

    @GET("stories/styles")
    Call<ApiResponse<List<String>>> getStyles();

    @POST("stories/audio")
    Call<ApiResponse<String>> generateAudio(@Body TextToSpeechRequest request);

}
