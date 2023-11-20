package com.example.findaseat;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.findaseat.Utils.Building;
import com.example.findaseat.Utils.Seat;
import com.example.findaseat.Utils.SeatAdapter;
import com.example.findaseat.Utils.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class BuildingFragment extends Fragment {

    String buildingName = "";


    private View loadingView; // The loading screen layout.
    private View contentView;


    private FirebaseFirestore db;
    StorageReference storageReference;

    ImageView iv;

    private RecyclerView seatRecyclerView;

    SeatAdapter adapter;

    List<Seat> seatList;

    private CollectionReference buildingsCollection;

    private ViewGroup container;

    Building building;

    String id = "";

    ProgressBar pB;

    private MutableLiveData<Building> buildingLiveData = new MutableLiveData<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_building, container, false);

        loadingView = inflater.inflate(R.layout.loading_screen, container, false);
        storageReference = FirebaseStorage.getInstance().getReference();
        iv = view.findViewById(R.id.buildingPicture);
        contentView = view.findViewById(R.id.mainLayout);


        this.container = container;

        pB = view.findViewById(R.id.progress);
        pB.setVisibility(View.VISIBLE);



        this.container.addView(loadingView);

        Bundle args = getArguments();
        if (args != null) {
            buildingName = args.getString("name");
        }

        db = FirebaseFirestore.getInstance();

        buildingsCollection = db.collection("buildings");

        buildingLiveData.observe(getViewLifecycleOwner(), building -> {
            if (building != null) {
                // Access the TextViews by their IDs and set their text values.
                TextView buildingNameTextView = getView().findViewById(R.id.buildingName);
                TextView addressTextView = getView().findViewById(R.id.textAddress);
                TextView descriptionTextView = getView().findViewById(R.id.textDescription);
                TextView hours = getView().findViewById(R.id.hours);

                buildingNameTextView.setText(building.getName());
                addressTextView.setText(building.getAddress());
                descriptionTextView.setText(building.getDescription());
                hours.setText(building.getHours());

                this.building = building;
                building.setId(this.id);
                User user = (User) getActivity().getApplicationContext();

                seatRecyclerView = view.findViewById(R.id.seatRecyclerView);
                seatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                List<Seat> seatList = new ArrayList<>();
                adapter = new SeatAdapter(seatList, getFragmentManager(), this.getContext(), user, this.building);
                seatRecyclerView.setAdapter(adapter);
                CollectionReference buildingsCollection = db.collection("buildings");
                DocumentReference buildingRef = buildingsCollection.document(buildingName);
                CollectionReference seatsCollection = buildingRef.collection("seats");
                seatsCollection.get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Seat seat = document.toObject(Seat.class);
                                seat.setRoom(document.getString("room"));
                                seat.setId(Long.valueOf(document.getId()));
                                seat.setNumber(document.getLong("number"));
                                seatList.add(seat);
                            }
                            adapter.notifyDataSetChanged();


                        })
                        .addOnFailureListener(e -> {
                            // Handle errors or show a message if the query fails.
                            Log.e("Firestore", "Error fetching seat data", e);
                        });

            } else {
                // Handle the case where the building is not found.
                // You can show an error message or take appropriate action.
            }
        });
        fetchBuildingDataFromFirestore(buildingName);




        // Inflate the layout for this fragment
        return view;
    }



    //Refactored getBuildingInfo() for testing
    private void fetchBuildingDataFromFirestore(String buildingName) {
        System.out.println("Hello1");
        DocumentReference buildingRef = buildingsCollection.document(buildingName);
        buildingRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Document with the specified ID (building name) exists.
                        Building specificBuilding = getBuildingInfo(documentSnapshot);
                        buildingLiveData.setValue(specificBuilding);
                        getUserImage();
                        showMainContent();
                    } else {
                        // Document with the specified ID does not exist (building name not found).
                        buildingLiveData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors or show a message if the query fails.
                    Log.e("Firestore", "Error fetching building data", e);
                    showMainContent();
                });
    }

    Building getBuildingInfo(DocumentSnapshot documentSnapshot){
        Building specificBuilding = documentSnapshot.toObject(Building.class);
        long l = documentSnapshot.getLong("open");
        specificBuilding.setOpen(l);
        this.id = (documentSnapshot.getId());
        System.out.println(specificBuilding.getOpenTime());
        return specificBuilding;// Update the LiveData with the retrieved building data.
    }

    private void getUserImage() {
        User user = (User) getActivity().getApplicationContext();
        final StorageReference fileref = storageReference.child("buildings/" + this.id+".jpeg");
        fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(iv);
            }
        });

        pB.setVisibility(View.GONE);

    }

    private void showMainContent() {
        // Replace the loading screen with your main content.
        ViewGroup parent = (ViewGroup) loadingView.getParent();
        if (parent != null) {
            parent.removeView(loadingView);
        }

        // Check if contentView is already attached to a parent.
        if (contentView.getParent() == null) {
            container.addView(contentView);
        }
    }




}