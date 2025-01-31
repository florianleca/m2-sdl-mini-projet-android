package com.example.miniprojet.restaurant;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.example.miniprojet.FetchState;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestaurantService {

    private static final String TAG = RestaurantService.class.getSimpleName();
    private final FirebaseFirestore dataSource;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public RestaurantService(FirebaseFirestore dataSource) {
        this.dataSource = dataSource;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void fetch(FetchState<Restaurant> state) {
        Log.d(TAG, "fetching restaurants started");
        executorService.execute(() -> {
            Log.d(TAG, "fetching restaurants in background...");
            dataSource.collection("restaurants")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        List<Restaurant> restaurants = new ArrayList<>();
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            Restaurant restaurant = doc.toObject(Restaurant.class);
                            if (restaurant != null) {
                                restaurant.setId(doc.getId()); // set the id of the restaurant, Firebase not setting it automatically
                                restaurants.add(restaurant);
                            }
                        }
                        Log.d(TAG, "Restaurants loaded successfully");
                        mainHandler.post(() -> mainHandler.post(() -> state.onSuccess(restaurants)));
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error occurred during fetching restaurants", e);
                        mainHandler.post(() -> state.onError(e));
                    });
        });
    }
}

