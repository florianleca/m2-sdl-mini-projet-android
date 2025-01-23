package com.example.miniprojet.review;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.miniprojet.R;
import com.example.miniprojet.FetchState;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReviewService {

    private static final String TAG = ReviewService.class.getSimpleName();
    private final AppCompatActivity activity;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public ReviewService(AppCompatActivity activity) {
        this.activity = activity;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void fetchReviews(FetchState<Review> state) {
        Log.d(TAG, "started fetching reviews");
        executorService.execute(() -> {
            Log.d(TAG, "fetching restaurants in background...");
            ObjectMapper om = new ObjectMapper();
            try (InputStream is = activity.getResources().openRawResource(R.raw.reviews)) {
                List<Review> reviews = om.readValue(is, new TypeReference<>() {
                });
                Log.d(TAG, "Background task completed, reviews loaded");
                mainHandler.post(() -> {
                    Log.d(TAG, "Posting results to main thread");
                    state.onSuccess(reviews);
                });
            } catch (Exception e) {
                Log.e(TAG, "Error occurred while fetching reviews", e);
                mainHandler.post(() -> state.onError(e));
            }
        });
    }

}
