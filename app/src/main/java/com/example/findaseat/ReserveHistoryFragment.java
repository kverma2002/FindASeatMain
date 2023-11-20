package com.example.findaseat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findaseat.Utils.BoolResPair;
import com.example.findaseat.Utils.Reservation;
import com.example.findaseat.Utils.ReservationAdapter;
import com.example.findaseat.Utils.ReservationAdapterUpcoming;
import com.example.findaseat.Utils.ReservationSeatAdapter;
import com.example.findaseat.Utils.Time;
import com.example.findaseat.Utils.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReserveHistoryFragment extends Fragment {


    private FirebaseFirestore db;
    private RecyclerView recyclerView;

    private RecyclerView recyclerViewUpcoming;

    private List<Reservation> upcomingReservation;

    private List<Reservation> reservationsList;
    private ReservationAdapter adapter;

    private ReservationAdapterUpcoming adapterUpcoming;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reserve_history_layout, container, false);


        db = FirebaseFirestore.getInstance();
        reservationsList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.pastInfo);
        adapter = new ReservationAdapter(reservationsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        upcomingReservation = new ArrayList<>();
        recyclerViewUpcoming = view.findViewById(R.id.upcomingReservation);
        adapterUpcoming = new ReservationAdapterUpcoming(upcomingReservation);
        recyclerViewUpcoming.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewUpcoming.setAdapter(adapterUpcoming);


        User user = (User) getActivity().getApplicationContext();
        String userEmail = user.getEmail();

        // Query to retrieve reservation data for the user
        db.collection("reservations")
                .whereEqualTo("user", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot reservations = task.getResult();
                        if (reservations != null) {
                            for (QueryDocumentSnapshot reservation : reservations) {
                                BoolResPair<Boolean, Reservation> r = addReservationToTable(reservation);
                                if (r != null) {
                                    if (r.getFirst()) {
                                        upcomingReservation.add(r.getSecond());
                                    } else {
                                        reservationsList.add(r.getSecond());
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

        Button cancelButton = view.findViewById(R.id.cancel); // Replace with your actual button ID
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (upcomingReservation.size() != 0) {
                    showConfirmationDialog();
                }
            }
        });



        return view;
    }


    BoolResPair addReservationToTable(QueryDocumentSnapshot reservation) {
        if (reservation != null) {
            String building = reservation.getString("building");
            String room = reservation.getString("room");

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            Timestamp firestoreTimestamp = reservation.getTimestamp("startTime");
            Timestamp currentTimestamp = Timestamp.now();
            Date firestoreDate = firestoreTimestamp.toDate();
            Date currentDate = currentTimestamp.toDate();



            Date startDate = firestoreTimestamp.toDate();
            String startTime = timeFormat.format(startDate);
            String formattedDate = dateFormat.format(startDate);


            Timestamp firestoreTimestampEnd = reservation.getTimestamp("endTime");
            Date endDate = firestoreTimestampEnd.toDate();
            String endTime = timeFormat.format(endDate);

            String id = reservation.getId();

            boolean c= reservation.getBoolean("cancelled");

            if (firestoreDate.after(currentDate) && !c) {
                // Future
                return new BoolResPair<>(true, new Reservation(startTime, endTime,room, building, formattedDate, id, c));
            } else {
                return new BoolResPair<>(false, new Reservation(startTime, endTime,room, building, formattedDate, id, c));
            }


        }
        return null;
    }
    public void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure?");

        // Add "Yes" button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Implement the cancel logic here when the user clicks "Yes"
                // For example, navigate back or perform the desired action.
                // You can use getActivity() to access the parent activity and perform fragment transactions.
                db = FirebaseFirestore.getInstance();
                Reservation r = upcomingReservation.remove(0);
                String reservationId = r.getId();
                Map<String, Object> updates = new HashMap<>();
                updates.put("cancelled", true);
                DocumentReference docRef = db.collection("reservations").document(reservationId);
                docRef.update(updates)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Document successfully deleted
                                // You can implement any success handling here, like updating UI
                                Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                                replaceFragment(new ReserveHistoryFragment());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // An error occurred while trying to delete the document
                                // Handle the error, log it, or display an error message
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });

        // Add "No" button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog when the user clicks "No" (you can also do nothing here).
                dialog.dismiss();
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void replaceFragment(Fragment newFragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, newFragment);
        fragmentTransaction.addToBackStack(null); // Add to back stack for back navigation
        fragmentTransaction.commit();
    }
}