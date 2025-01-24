package com.example.miniprojet.restaurant;

import static android.widget.Toast.LENGTH_SHORT;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.miniprojet.FetchState;
import com.example.miniprojet.R;
import com.example.miniprojet.review.Review;
import com.example.miniprojet.review.ReviewService;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RestaurantActivity extends AppCompatActivity {

    private static final String TAG = RestaurantActivity.class.getSimpleName();
    private final SimpleDateFormat reviewDateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_restaurant_page);

        restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");

        initToolbar();
        fetchReviews(new ReviewService(this));
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle(restaurant.getName());
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void fetchReviews(ReviewService service) {
        service.fetchReviews(new FetchState<>() {
            @Override
            public void onSuccess(List<Review> reviews) {
                Log.d(TAG, "Reviews loaded successfully, count: " + reviews.size());
                pushReviewsInView(reviews);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to load reviews", e);
                Toast.makeText(RestaurantActivity.this, "Failed to load restaurants", LENGTH_SHORT).show();
            }
        });
    }

    private void pushReviewsInView(List<Review> reviews) {
        LinearLayout reviewsContent = findViewById(R.id.reviewsContent);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Review review : reviews) {
            View view = inflater.inflate(R.layout.component_review_mini, reviewsContent, false);

            TextView userNameView = view.findViewById(R.id.user_name);
            RatingBar ratingBar = view.findViewById(R.id.rating_bar);
            TextView reviewContentView = view.findViewById(R.id.review_content);
            TextView dateView = view.findViewById(R.id.date);

            loadReviewImages(view, review);
            userNameView.setText(review.getUserName());
            ratingBar.setRating(review.getStars());
            reviewContentView.setText(review.getContent());
            dateView.setText(reviewDateFormatter.format(review.getDate()));

            reviewsContent.addView(view);
        }
    }

    private void loadReviewImages(View view, Review review) {
        for (String url : review.getImagesUrl()) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
            params.setMargins(8, 8, 8, 8); // Margins between images
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            Picasso.get()
                    .load(url)
                    .error(R.drawable.ic_launcher_background)
                    .into(imageView);

            LinearLayout imageRow = view.findViewById(R.id.image_row);
            imageRow.addView(imageView);
        }

    }

}
