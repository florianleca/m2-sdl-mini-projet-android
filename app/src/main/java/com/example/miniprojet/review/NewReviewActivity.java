package com.example.miniprojet.review;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.miniprojet.FetchState;
import com.example.miniprojet.R;
import com.example.miniprojet.camera.CameraActivity;
import com.example.miniprojet.restaurant.Restaurant;
import com.example.miniprojet.restaurant.RestaurantActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class NewReviewActivity extends AppCompatActivity {

    private static final String TAG = NewReviewActivity.class.getSimpleName();
    private Restaurant restaurant;
    private Review review;
    private AccelerationRecorderService accelerationRecorderService;
    private SoundRecorderService soundRecorderService;
    private Bitmap currentImageBitmap;
    private Bitmap snapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_review);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerationRecorderService = new AccelerationRecorderService(this, sensorManager);

        soundRecorderService = new SoundRecorderService(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 200);
        }

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

            postReview(ReviewService.getInstance(FirebaseFirestore.getInstance()));

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
        LinearLayout imagePreviewRow = findViewById(R.id.image_preview_row);
        imagePreviewRow.removeAllViews();
        for (String imagePath : review.getImagesUrl()) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                addImageToRow(imagePath, bitmap);
            }
        }
    }

    private void addImageToRow(String imagePath, Bitmap bitmap) {
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 200);
        params.setMargins(8, 0, 8, 0);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        LinearLayout imagePreviewRow = findViewById(R.id.image_preview_row);
        imagePreviewRow.addView(imageView);

        imageView.setOnClickListener(v -> displayModal(imagePath, bitmap));
    }

    private void displayModal(String imagePath, Bitmap bitmap) {
        snapshot = bitmap;
        currentImageBitmap = bitmap;

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_photo_display);

        ImageView modalImageView = dialog.findViewById(R.id.modal_image_view);
        Button supprimerButton = dialog.findViewById(R.id.supprimer_button);
        Button flouterButton = dialog.findViewById(R.id.flouter_button);
        Button assombrirButton = dialog.findViewById(R.id.assombrir_button);
        Button reinitialiserButton = dialog.findViewById(R.id.reinitialiser_button);
        Button enregistrerButton = dialog.findViewById(R.id.enregistrer_button);

        modalImageView.setImageBitmap(bitmap);

        disableButton(reinitialiserButton);
        reinitialiserButton.setOnClickListener(v -> {
            modalImageView.setImageBitmap(snapshot);
            currentImageBitmap = snapshot;
            disableButton(reinitialiserButton);
        });

        enregistrerButton.setOnClickListener(v -> {
            review.removeImage(imagePath);
            File file;
            FileOutputStream outputStream;
            try {
                file = File.createTempFile("user_photo_", ".jpg", getCacheDir());
                outputStream = new FileOutputStream(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            currentImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            review.addImage(file.getAbsolutePath());
            loadImages();
            dialog.dismiss();
        });

        supprimerButton.setOnClickListener(v -> {
            review.removeImage(imagePath);
            loadImages();
            dialog.dismiss();
        });

        flouterButton.setOnClickListener(v -> {
            if (!accelerationRecorderService.isRecordingAcceleration()) {
                accelerationRecorderService.startRecordingAcceleration();
                flouterButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark, null));
                disableButton(supprimerButton);
                disableButton(assombrirButton);
            } else {
                currentImageBitmap = accelerationRecorderService.stopRecordingAccelerationAndApplyBlur(currentImageBitmap);
                modalImageView.setImageBitmap(currentImageBitmap);
                flouterButton.setBackgroundColor(getResources().getColor(R.color.app_main, null));
                enableButton(supprimerButton);
                enableButton(assombrirButton);
                enableButton(reinitialiserButton);
            }
        });

        assombrirButton.setOnClickListener(v -> {
            if (!soundRecorderService.isRecordingSound()) {
                soundRecorderService.startRecordingSound(getCacheDir());
                assombrirButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark, null));
                disableButton(supprimerButton);
                disableButton(flouterButton);
            } else {
                currentImageBitmap = soundRecorderService.stopRecordingSoundAndApplyObscurity(currentImageBitmap);
                modalImageView.setImageBitmap(currentImageBitmap);
                assombrirButton.setBackgroundColor(getResources().getColor(R.color.app_main, null));
                enableButton(supprimerButton);
                enableButton(flouterButton);
                enableButton(reinitialiserButton);
            }
        });

        dialog.show();
    }

    private void disableButton(Button button) {
        button.setEnabled(false);
        button.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, null));
    }

    private void enableButton(Button button) {
        button.setEnabled(true);
        button.setBackgroundColor(getResources().getColor(R.color.app_main, null));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accelerationRecorderService.unregisterListener();
    }

}
