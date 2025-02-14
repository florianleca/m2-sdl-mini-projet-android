package com.example.miniprojet.review;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.miniprojet.FetchState;
import com.example.miniprojet.R;
import com.example.miniprojet.camera.CameraActivity;
import com.example.miniprojet.restaurant.Restaurant;
import com.example.miniprojet.restaurant.RestaurantActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class NewReviewActivity extends AppCompatActivity {

    private static final String TAG = NewReviewActivity.class.getSimpleName();
    private Restaurant restaurant;
    private Review review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_review);
        restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");
        review = (Review) getIntent().getSerializableExtra("review");
        initToolbar();
        initPhotoButton();
        initSendButton();
        loadCurrentReview();
        loadImages();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle("Laisser un avis sur " + restaurant.getName());
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, RestaurantActivity.class);
            intent.putExtra("restaurant", restaurant);
            startActivity(intent);
        });
    }

    private void initPhotoButton() {
        Button takePhotoButton = findViewById(R.id.take_photo_button);
        takePhotoButton.setOnClickListener(v -> {
            saveCurrentReview();
            Intent intent = new Intent(this, CameraActivity.class);
            intent.putExtra("review", review);
            intent.putExtra("restaurant", restaurant);
            startActivity(intent);
        });
    }

    private void initSendButton() {
        Button sendButton = findViewById(R.id.send_review_button);
        sendButton.setOnClickListener(v -> {
            saveCurrentReview();
            review.setRestaurantId(restaurant.getId());
            review.setDate(new Date());

            postReview(new ReviewService(FirebaseFirestore.getInstance()));

            Intent intent = new Intent(this, RestaurantActivity.class);
            intent.putExtra("restaurant", restaurant);
            startActivity(intent);
            Toast.makeText(this, "Votre avis a été publié !", Toast.LENGTH_SHORT).show();
        });
    }

    private void postReview(ReviewService reviewService) {
        reviewService.postReview(review, new FetchState<>() {
            @Override
            public void onSuccess(List<Review> data) {
                Log.d(TAG, "Review posted successfully with ID: " + review.getId());
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error occurred while posting review", e);
            }
        });
    }

    private void saveCurrentReview() {
        EditText editName = findViewById(R.id.edit_name);
        EditText editReview = findViewById(R.id.edit_review);
        RatingBar ratingBar = findViewById(R.id.rating_bar);
        review.setStars(ratingBar.getRating());
        review.setContent(editReview.getText().toString());
        review.setUserName(editName.getText().toString());
    }

    private void loadCurrentReview() {
        EditText editName = findViewById(R.id.edit_name);
        EditText editReview = findViewById(R.id.edit_review);
        RatingBar ratingBar = findViewById(R.id.rating_bar);
        if (review.getUserName() != null) editName.setText(review.getUserName());
        if (review.getContent() != null) editReview.setText(review.getContent());
        if (review.getStars() != 0) ratingBar.setRating(review.getStars());
    }

    private void loadImages() {
        for (String imagePath : review.getImagesUrl()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                addImageToRow(bitmap);
            }
        }
    }

    private void addImageToRow(Bitmap bitmap) {
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 200);
        params.setMargins(8, 0, 8, 0);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        LinearLayout imagePreviewRow = findViewById(R.id.image_preview_row);
        imagePreviewRow.addView(imageView);
    }

}
