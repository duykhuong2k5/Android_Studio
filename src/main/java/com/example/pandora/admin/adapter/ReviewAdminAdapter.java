package com.example.pandora.admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pandora.R;
import com.example.pandora.admin.ManageReviewsActivity;
import com.example.pandora.data.entity.Review;

import java.util.List;

public class ReviewAdminAdapter extends RecyclerView.Adapter<ReviewAdminAdapter.ViewHolder> {

    private final Context context;
    private final List<Review> reviews;

    public ReviewAdminAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvProductName, tvComment, tvDate;
        RatingBar ratingBar;
        Button btnDelete;

        public ViewHolder(View view) {
            super(view);
            tvUsername    = view.findViewById(R.id.tvUsername);
            tvProductName = view.findViewById(R.id.tvProductName);   // 👈 mới thêm
            tvComment     = view.findViewById(R.id.tvComment);
            ratingBar     = view.findViewById(R.id.ratingBar);
            tvDate        = view.findViewById(R.id.tvDate);
            btnDelete     = view.findViewById(R.id.btnDelete);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_review_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Review review = reviews.get(position);

        // Tên người dùng
        String username = review.getUsername() != null
                ? review.getUsername()
                : "Ẩn danh";
        holder.tvUsername.setText(username);

        // Tên sản phẩm
        String productName = review.getProductName() != null
                ? review.getProductName()
                : "Sản phẩm không xác định";
        holder.tvProductName.setText(productName);

        // Nội dung + rating + ngày giờ
        holder.tvComment.setText(review.getComment());
        holder.ratingBar.setRating((float) review.getRating());
        holder.tvDate.setText(review.getCreatedAt() != null ? review.getCreatedAt() : "");

        // Xóa
        holder.btnDelete.setOnClickListener(v -> {
            if (context instanceof ManageReviewsActivity) {
                ((ManageReviewsActivity) context).deleteReview(review, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}
