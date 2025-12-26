package com.example.pandora.data.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.pandora.data.network.ApiService;
public class RetrofitClient {

    // ⚠️ Nếu dùng giả lập Android Studio: 10.0.2.2
    // Nếu dùng điện thoại thật: thay bằng IP thật của máy tính, ví dụ: 192.168.1.5
    private static final String BASE_URL = "http://10.0.2.2:8080/api/";

    private static RetrofitClient instance;
    private final ApiService apiService;

    private RetrofitClient() {
        // Ghi log request/response để dễ debug
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    // Singleton pattern
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public ApiService getApi() {
        return apiService;
    }
}
