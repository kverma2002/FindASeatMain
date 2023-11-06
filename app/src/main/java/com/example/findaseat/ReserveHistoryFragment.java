package com.example.findaseat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.example.findaseat.Utils.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;


public class ReserveHistoryFragment extends Fragment {

    private TableLayout tableLayout;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reserve_history_layout, container, false);

        tableLayout = view.findViewById(R.id.table);
        db = FirebaseFirestore.getInstance();


        User user = (User) getActivity().getApplicationContext();
        String userEmail = user.getEmail();

        // Query to retrieve reservation data for the user
        db.collection("reserve")
                .whereEqualTo("user_email", userEmail)
                .orderBy("startTime", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot reservations = task.getResult();
                        if (reservations != null) {
                            for (QueryDocumentSnapshot reservation : reservations) {
                                addReservationToTable(reservation);
                            }
                        }
                    }
                });

        return view;
    }

    private void addReservationToTable(QueryDocumentSnapshot reservation) {
        if (reservation != null) {
            String building = reservation.getString("building");
            String startTime = reservation.getString("startTime");
            String endTime = reservation.getString("endTime");
            String seatId = reservation.getString("seatId");

            // Create a new TableRow
            TableRow row = new TableRow(requireContext());

            // Create TextViews for each column and set the data
            TextView buildingTextView = new TextView(requireContext());
            buildingTextView.setText(building);

            TextView SeatTextView = new TextView(requireContext());
            buildingTextView.setText(seatId);

            TextView startTimeTextView = new TextView(requireContext());
            startTimeTextView.setText(endTime);


            // Add TextViews to the TableRow
            row.addView(buildingTextView);
            row.addView(SeatTextView);
            row.addView(startTimeTextView);

            // Add the TableRow to the TableLayout
            tableLayout.addView(row);
        }
    }
}