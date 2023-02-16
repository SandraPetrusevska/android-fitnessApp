package com.example.fitnessapp;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.fitnessapp.databinding.ActivityMapsBinding;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    SQLiteDatabase db;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    ArrayList<LatLng> list;
    ArrayList<String> list2;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = openOrCreateDatabase("fitnessApp", MODE_PRIVATE, null);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        list = new ArrayList<>();
        list2 = new ArrayList<>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        int i;

       Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
       Cursor c1 = db.rawQuery("SELECT DISTINCT username FROM userTrainings", null);
        if(c1.moveToFirst()) {
            do {
                Cursor c = db.rawQuery("SELECT * FROM userTrainings WHERE id = " + id, null);
                if(c.moveToFirst()) {
                    do {
                        list2.add(c.getString(1));
                        LatLng loc = new LatLng(c.getDouble(2), c.getDouble(3));
                        list.add(loc);
                    } while (c.moveToNext());
                    c.close();
                }
            } while (c1.moveToNext());
            c1.close();
        }

        for (i = 0; i < list.size(); i++) {
            String username = list2.get(i);
            mMap.addMarker(new MarkerOptions().position(list.get(i)).title("Marker for user: " + username));
            float zoomLevel = 10.0f; //This goes up to 21
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(list.get(i), zoomLevel));
        }
    }
}