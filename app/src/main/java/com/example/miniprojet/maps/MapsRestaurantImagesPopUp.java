package com.example.miniprojet.maps;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.miniprojet.FetchState;
import com.example.miniprojet.R;
import com.example.miniprojet.restaurant.Restaurant;
import com.example.miniprojet.restaurant.RestaurantActivity;
import com.example.miniprojet.review.ReviewService;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class MapsRestaurantImagesPopUp {
    private static final String TAG = MapsRestaurantImagesPopUp.class.getSimpleName();
    private final Context context;
    private final Restaurant restaurant;

    public MapsRestaurantImagesPopUp(Context context, Restaurant restaurant) {
        this.context = context;
        this.restaurant = restaurant;
    }

    public void show(boolean showContext) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.popup_restaurant_images);
        dialog.setCancelable(true);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(window.getAttributes());
            params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.7);
            window.setAttributes(params);
        }

        TextView nameView = dialog.findViewById(R.id.dialog_restaurant_name);
        ImageView restaurantImage = dialog.findViewById(R.id.dialog_restaurant_image);
        LinearLayout imageGallery = dialog.findViewById(R.id.dialog_image_gallery);

        nameView.setText(restaurant.getName());
        Picasso.get().load(restaurant.getImageUrl()).into(restaurantImage);

        if (showContext) {
            ReviewService reviewService = ReviewService.getInstance(FirebaseFirestore.getInstance());
            reviewService.fetchReviewImagesByRestaurant(restaurant.getId(), new FetchState<>() {
                @Override
                public void onSuccess(List<String> imageUrls) {
                    Log.i(TAG, "Images récupérées : " + imageUrls.size());
                    imageUrls = imageUrls.stream().filter(imagePath -> imagePath.contains("/storage/emulated/"))
                            .collect(Collectors.toList());
                    if (!imageUrls.isEmpty()) {
                        for (String imagePath : imageUrls) {
                            Log.i(TAG, "Chargement de l'image : " + imagePath);
                            ImageView imageView = new ImageView(context);
                            Picasso.get().load(new File(imagePath))
                                    .error(R.drawable.ic_launcher_background)
                                    .into(imageView);

                            int imageWidth = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.35);
                            int imageHeight = (int) (imageWidth * 0.6);

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageWidth, imageHeight);
                            layoutParams.setMargins(8, 8, 8, 8);
                            imageView.setLayoutParams(layoutParams);
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            imageGallery.addView(imageView);
                        }
                    } else {
                        TextView noImagesText = new TextView(context);
                        noImagesText.setText(R.string.no_review_with_text);
                        noImagesText.setPadding(16, 16, 16, 16);
                        imageGallery.addView(noImagesText);
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Erreur de récupération des images : " + e);
                }
            });
        }

        Button viewRestaurantButton = dialog.findViewById(R.id.dialog_see_restaurant_button);
        viewRestaurantButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, RestaurantActivity.class);
            intent.putExtra("restaurant", restaurant);
            context.startActivity(intent);
            dialog.dismiss();
        });

        Button closeDialogButton = dialog.findViewById(R.id.dialog_close_button);
        closeDialogButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
