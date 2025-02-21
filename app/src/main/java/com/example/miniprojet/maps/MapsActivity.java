package com.example.miniprojet.maps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.miniprojet.R;
import com.example.miniprojet.restaurant.Restaurant;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.*;

public class MapsActivity extends AppCompatActivity {
    private static final String TAG = MapsActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private static final double TOULOUSE_LAT = 43.6043;
    private static final double TOULOUSE_LON = 1.4437;
    private static final double RADIUS_KM = 5.0;
    private final Random random= new Random();
    private final HashMap<String, double[]> restaurantPositions = new HashMap<>();
    private final HashMap<String, Boolean> markerClickState = new HashMap<>();
    private  MapView mapView;
    private ArrayList<Restaurant> restaurants;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(getApplicationContext().getPackageName());

        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        setContentView(R.layout.activity_maps);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        restaurants = (ArrayList<Restaurant>) getIntent().getSerializableExtra("restaurants");

        if (restaurants == null) {
            Log.e("OSM", "Aucun restaurant reçu via Intent !");
            return;
        }

        generateRandomPositions();

        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        GeoPoint startPoint = new GeoPoint(TOULOUSE_LAT, TOULOUSE_LON);
        mapView.getController().setCenter(startPoint);
        mapView.getController().setZoom(13);

        addRestaurantMarkers();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void generateRandomPositions() {
        for (Restaurant restaurant : restaurants) {
            double[] randomCoords = getRandomLocation();
            restaurantPositions.put(restaurant.getId(), randomCoords);
        }
    }

    private double[] getRandomLocation() {
        double radiusInDegrees = RADIUS_KM / 111.32;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double deltaLat = w * Math.cos(t);
        double deltaLon = w * Math.sin(t) / Math.cos(Math.toRadians(TOULOUSE_LAT));

        double newLat = TOULOUSE_LAT + deltaLat;
        double newLon = TOULOUSE_LON + deltaLon;

        return new double[]{newLat, newLon};
    }

    private void addRestaurantMarkers() {
        for (Restaurant restaurant : restaurants) {
            double[] coords = restaurantPositions.get(restaurant.getId());
            if (coords == null) continue;

            GeoPoint point = new GeoPoint(coords[0], coords[1]);

            Marker marker = new Marker(mapView);
            marker.setPosition(point);
            marker.setTitle("Restaurant : " + restaurant.getName());
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setSnippet("Cliquez pour plus d'infos");

            Drawable iconDrawable = ContextCompat.getDrawable(this, R.drawable.custom_marker);
            Bitmap bitmap = ((BitmapDrawable) iconDrawable).getBitmap();
            Drawable resizedIcon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 35, 50, true));
            marker.setIcon(resizedIcon);

            markerClickState.put(restaurant.getId(), true);

            marker.setOnMarkerClickListener((marker1, mapView) -> {
                boolean isClicked = markerClickState.getOrDefault(restaurant.getId(), true);

                MapsRestaurantImagesPopUp imagesPopUp = new MapsRestaurantImagesPopUp(this, restaurant);
                imagesPopUp.show(isClicked);

                markerClickState.put(restaurant.getId(), !isClicked);

                return true;
            });

            mapView.getOverlays().add(marker);
        }

        mapView.invalidate();
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Permission refusée !");
                    return;
                }
            }
            Log.d(TAG, "Toutes les permissions sont accordées.");
        }
    }
}