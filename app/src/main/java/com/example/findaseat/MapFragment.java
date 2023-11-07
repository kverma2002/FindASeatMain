package com.example.findaseat;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.findaseat.Utils.BuildingInformation;
import com.example.findaseat.Utils.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapFragment extends Fragment {

    private GoogleMap mMap;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment supportMapFragment=(SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.gmap);
        User user = (User) getActivity().getApplicationContext();

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                try {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    boolean success = googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    getActivity(), R.raw.style_json));

                } catch (Resources.NotFoundException e) {

                }

                this.createMarkers(mMap);

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        // Add Reservation/Buidling Information connector here
                        // replaceFragment(new ReservationFragment())
                        // Or you can start new activity
                        // And pass intent of name with marker.getTitle()
//                        if (user.getLoggedIn()) {
                            Bundle bundle = new Bundle();
                            bundle.putString("name", marker.getTitle());
                            BuildingFragment bf = new BuildingFragment();
                            bf.setArguments(bundle);
                            replaceFragment(bf);

//                        } else {
//                            Toast.makeText(getActivity(), "Log In First", Toast.LENGTH_SHORT).show();
//                        }
                        return false;

                    }
                });
                ////        Create Bounds for USC Campus
                double bottomBoundry = 34.018426;
                double leftBoundry = -118.291430;
                double topBoundry = 34.025500;
                double rightBoundry = -118.280226;

                LatLngBounds USC_CAMPUS = new LatLngBounds(
                        new LatLng(bottomBoundry, leftBoundry),
                        new LatLng(topBoundry, rightBoundry)
                );
                mMap.setLatLngBoundsForCameraTarget(USC_CAMPUS);
                mMap.setMinZoomPreference(15);
                mMap.setMaxZoomPreference(20);




                LatLng USC = new LatLng(34.021188, -118.287078);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(USC));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(USC )      // Sets the center of the map to Mountain View
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(60)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));




            }



            private void createMarkers(GoogleMap mMap) {
                BuildingInformation bi = new BuildingInformation();
                for(int i = 0; i < bi.information.length; i++) {
                    mMap.addMarker(new MarkerOptions()
                            .position(bi.information[i].latLng)
                            .title(bi.information[i].name));
                }
            }




        });
        return view;
    }

    private void replaceFragment(Fragment newFragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, newFragment);
        fragmentTransaction.addToBackStack(null); // Add to back stack for back navigation
        fragmentTransaction.commit();
    }



}