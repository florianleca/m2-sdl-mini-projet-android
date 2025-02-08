package com.example.miniprojet.maps;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.miniprojet.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            Log.e(TAG, "Impossible de configurer la Toolbar");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
            Log.d(TAG, "Chargement de la maps...");
        } else {
            Log.e(TAG, "Échec du chargement de la maps");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        GoogleMap map;
        Log.d(TAG, "Carte prête à être utilisée");

        try {
            map = googleMap;

            LatLng defaultLocation = new LatLng(48.8566, 2.3522);
            map.addMarker(new MarkerOptions().position(defaultLocation).title("Paris"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));

        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'affichage de la maps", e);
        }
    }

}
