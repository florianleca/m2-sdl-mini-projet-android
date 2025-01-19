package com.example.miniprojet.restaurant;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.miniprojet.FetchState;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.miniprojet.R;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestaurantService {

    private static final String TAG = RestaurantService.class.getSimpleName();
    private final AppCompatActivity activity;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public RestaurantService(AppCompatActivity activity) {
        this.activity = activity;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void fetchRestaurants(FetchState<Restaurant> state) {
        Log.d(TAG, "fetching restaurants started");
        executorService.execute(() -> {
            Log.d(TAG, "fetching restaurants in background...");
            ObjectMapper om = new ObjectMapper();
            try (InputStream is = activity.getResources().openRawResource(R.raw.restaurants)) {
                List<Restaurant> restaurants = om.readValue(is, new TypeReference<>() {
                });
                Log.d(TAG, "Restaurants loaded successfully");
                mainHandler.post(() -> mainHandler.post(() -> state.onSuccess(restaurants)));
            } catch (Exception e) {
                Log.e(TAG, "Error occurred during fetching restaurants", e);
                mainHandler.post(() -> state.onError(e));
            }
        });
    }
}

