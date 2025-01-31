package com.example.miniprojet.review;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.miniprojet.R;
import com.example.miniprojet.FetchState;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReviewService {

    private static final String TAG = ReviewService.class.getSimpleName();
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final FirebaseFirestore dataSource;

    public ReviewService(FirebaseFirestore dataSource) {
        this.dataSource = dataSource;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void fetchByRestaurant(String restaurantId, FetchState<Review> state) {
        Log.d(TAG, "Fetching reviews for restaurantId: " + restaurantId);

        executorService.execute(() ->
                dataSource.collection("reviews")
                        .whereEqualTo("restaurantId", restaurantId)
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            List<Review> reviews = new ArrayList<>();
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                Review review = doc.toObject(Review.class);
                                if (review != null) {
                                    review.setId(doc.getId()); // set the id of the review, Firebase not setting it automatically
                                    reviews.add(review);
                                }
                            }
                            Log.d(TAG, "Reviews loaded successfully");
                            mainHandler.post(() -> state.onSuccess(reviews));
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error occurred while fetching reviews", e);
                            mainHandler.post(() -> state.onError(e));
                        })
        );
    }

}
