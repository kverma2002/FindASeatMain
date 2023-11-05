package com.example.findaseat;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.findaseat.Utils.User;
import com.example.findaseat.databinding.ActivityMainBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class  MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        User user = (User) getApplicationContext();

        replaceFragment(new MapFragment());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener( item -> {

            int page =  item.getItemId();
            if (page == R.id.map) {
                replaceFragment(new MapFragment());
            } else if (page == R.id.profile) {
                if (user.getLoggedIn()) {
                    replaceFragment(new ProfileFragment());
                }
                else {
                    replaceFragment(new LogInFragment());
                }
            }

            return true;
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

//    private GoogleMap mMap;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//    }
//
//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     *
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
////        Create Bounds for USC Campus
//        double bottomBoundry = 34.018426;
//        double leftBoundry = -118.291430;
//        double topBoundry = 34.025500;
//        double rightBoundry = -118.280226;
//
//        LatLngBounds USC_CAMPUS = new LatLngBounds(
//                new LatLng(bottomBoundry, leftBoundry),
//                new LatLng(topBoundry, rightBoundry)
//        );
//        mMap.setLatLngBoundsForCameraTarget(USC_CAMPUS);
//        mMap.setMinZoomPreference(15);
//        mMap.setMaxZoomPreference(20);
//
//
//        LatLng EpsteinFamilyPlaza = new LatLng(34.020364, -118.288945);
//        mMap.addMarker(new MarkerOptions()
//                .position(EpsteinFamilyPlaza)
//                .title("Epstein Plaza Seats"));
//        LatLng USC = new LatLng(34.021188, -118.287078);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(EpsteinFamilyPlaza));
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(USC )      // Sets the center of the map to Mountain View
//                .zoom(17)                   // Sets the zoom
//                .bearing(90)                // Sets the orientation of the camera to east
//                .tilt(60)                   // Sets the tilt of the camera to 30 degrees
//                .build();                   // Creates a CameraPosition from the builder
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
//    }



}