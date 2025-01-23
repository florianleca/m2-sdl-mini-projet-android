package com.example.miniprojet.home;

import android.content.Intent;
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
import com.example.miniprojet.R;
import com.example.miniprojet.FetchState;
import com.example.miniprojet.restaurant.Restaurant;
import com.example.miniprojet.restaurant.RestaurantActivity;
import com.example.miniprojet.restaurant.RestaurantService;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private LinearLayout restaurantsContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        restaurantsContent = findViewById(R.id.restaurantsContent);

        fetchAndPushRestaurantsInView(new RestaurantService(this));
    }

    private void fetchAndPushRestaurantsInView(RestaurantService service) {
        service.fetchRestaurants(new FetchState<>() {
            @Override
            public void onSuccess(List<Restaurant> restaurants) {
                Log.d(TAG, "Restaurants loaded successfully, count: " + restaurants.size());
                pushRestaurantsInView(restaurants);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to load restaurants", e);
                Toast.makeText(HomeActivity.this, "Failed to load restaurants", LENGTH_SHORT).show();
            }
        });
    }

    private void pushRestaurantsInView(List<Restaurant> restaurants) {
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Restaurant restaurant : restaurants) {
            View view = inflater.inflate(R.layout.component_restaurant_mini, restaurantsContent, false);

            ImageView imageView = view.findViewById(R.id.imageView);
            TextView nameView = view.findViewById(R.id.restau_name);
            RatingBar ratingBar = view.findViewById(R.id.rating_bar);
            TextView descriptionView = view.findViewById(R.id.restau_desc);

            Picasso.get().load(restaurant.getImageUrl()).into(imageView);
            imageView.setContentDescription(String.format("%s img", restaurant.getName()));
            nameView.setText(restaurant.getName());

            ratingBar.setRating(restaurant.getRating());
            descriptionView.setText(restaurant.getDescription());

            // Click listener to restaurant detailed page
            view.setOnClickListener(v -> {
                Intent intent = new Intent(this, RestaurantActivity.class);
                intent.putExtra("restaurant", restaurant);
                startActivity(intent);
            });

            restaurantsContent.addView(view);
        }
    }
}