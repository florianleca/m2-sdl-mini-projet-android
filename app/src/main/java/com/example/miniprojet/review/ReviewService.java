package com.example.miniprojet.review;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.miniprojet.FetchState;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
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
                            mainHandler.post(() -> state.onSuccess(reviews));
                        })
                        .addOnFailureListener(e -> mainHandler.post(() -> state.onError(e)))
        );
    }

    public void postReview(Review review, FetchState<Review> state) {
        Log.d(TAG, "Posting review for restaurantId: " + review.getRestaurantId());

        executorService.execute(() ->
                dataSource.collection("reviews")
                        .add(review)
                        .addOnSuccessListener(documentReference -> mainHandler.post(() -> {
                            review.setId(documentReference.getId());
                            state.onSuccess(Collections.singletonList(review));
                        }))
                        .addOnFailureListener(e -> mainHandler.post(() -> state.onError(e)))
        );
    }

}
