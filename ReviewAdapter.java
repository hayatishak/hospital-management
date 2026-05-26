package com.example.tatwa10.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatwa10.ModelClass.Review;
import com.example.tatwa10.R;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<Review> reviewList;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList != null ? reviewList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviewList.get(position);

        if (review.getComment() != null && !review.getComment().isEmpty()) {
            holder.comment.setText(review.getComment());
        } else {
            holder.comment.setText("No comment");
        }

        holder.rating.setRating(review.getRating());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList != null ? reviewList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView comment;
        RatingBar rating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            comment = itemView.findViewById(R.id.review_comment);
            rating = itemView.findViewById(R.id.review_rating);
        }
    }
}