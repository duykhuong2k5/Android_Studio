package com.example.pandora.ui.payment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pandora.R;

public class VnpayActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vnpay);

        webView = findViewById(R.id.webViewVnpay);
        progressBar = findViewById(R.id.progressBar);

        String paymentUrl = getIntent().getStringExtra("payment_url");

        if (paymentUrl == null) {
            Toast.makeText(this, "Không tìm thấy URL thanh toán!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // ---- WebView Settings ----
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        // Cho phép Mixed content (VNPay dùng HTTPS nhưng backend trả HTTP)
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);

                // ⬤ Xử lý khi VNPay redirect về backend: /api/payment/vnpay_return
                if (url.contains("/api/payment/vnpay_return")) {

                    if (url.contains("vnp_ResponseCode=00")) {
                        Toast.makeText(VnpayActivity.this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(VnpayActivity.this, "Thanh toán thất bại!", Toast.LENGTH_LONG).show();
                    }

                    finish(); // thoát màn hình thanh toán
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });

        webView.loadUrl(paymentUrl);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
