package com.example.findaseat;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.findaseat.Utils.BoolResPair;
import com.example.findaseat.Utils.Building;
import com.example.findaseat.Utils.Reservation;
import com.example.findaseat.Utils.ReservationAdapter;
import com.example.findaseat.Utils.ReservationSeatAdapter;
import com.example.findaseat.Utils.Seat;
import com.example.findaseat.Utils.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class SeatFragment extends Fragment {



    Seat seat;

    Building building;

    private Spinner startTimeSpinner;
    private Spinner durationSpinner;

    private FirebaseFirestore db;
    private List<Reservation> reservationsList;
    private RecyclerView recyclerView;
    private ReservationSeatAdapter adapter;

    MaterialButton reserve;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seat, container, false);

        Bundle args = getArguments();
        if (args != null) {
            seat = (Seat) args.getSerializable("seat");
            building = (Building) args.getSerializable("building");
        }


        startTimeSpinner = view.findViewById(R.id.startTimeSpinner);
        durationSpinner = view.findViewById(R.id.durationSpinner);

        setText(view);
        setSprinner();
        setSpinnerDuration();

        db = FirebaseFirestore.getInstance();
        reservationsList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.currentReservation);
        adapter = new ReservationSeatAdapter(reservationsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        long seatId = seat.getId();
//        Log.d("Poop", Long.toString(seatId));
        Timestamp firestoreTimestamp = Timestamp.now();
        db.collection("reservations")
                .whereEqualTo("seatId", seatId) // Filter by the current seat
                .whereGreaterThan("endTime", firestoreTimestamp) // Reservations that haven't ended yet
//                .orderBy("startTime", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot reservations = task.getResult();
                        if (reservations != null) {
                            for (QueryDocumentSnapshot reservation : reservations) {
                                Reservation r = createReservation(reservation);
                                if (r != null) {
                                        if (!r.isCancelled()) {
                                            reservationsList.add(r);
//                                            Log.d("Poop", "Debug message: Activity created.");
                                        }
                                    }
                                }
                            adapter.notifyDataSetChanged();
                            }
                        }
                });



        reserve = view.findViewById(R.id.reserve);
        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timestamp firestoreTimestamp = Timestamp.now(); // Replace with the selected date
                Long selectedSeatId = seat.getId() ; // Replace with the selected seat ID
                String selectedStartTime = startTimeSpinner.toString(); // Replace with the selected start time
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
                try {
                    // Parse the selected military time string into a Date object
                    Date parsedTime = timeFormat.parse(selectedStartTime);

                    // Get the current date
                    Calendar calendar = Calendar.getInstance();
                    Date currentDate = calendar.getTime();

                    // Combine the current date with the parsed time to create a Timestamp
                    Timestamp timestamp = new Timestamp(currentDate.getTime() + parsedTime.getTime());

                    // Now you can use 'timestamp' to store in Firestore
                    // For example, you can store it in a Firestore document field
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }





        return view;
    }



    private Reservation createReservation(QueryDocumentSnapshot reservation) {
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
        boolean c= reservation.getBoolean("cancelled");

        String id = reservation.getId();
        return  new Reservation(startTime, endTime,room, building, formattedDate, id, c);

    }

    private void setSpinnerDuration() {
        List<String> durationOptions = new ArrayList<>();
        for (int minutes = 30; minutes <= 120; minutes += 30) {
            int hours = minutes / 60;
            int mins = minutes % 60;
            String duration = String.format("%02d:%02d", hours, mins);
            durationOptions.add(duration);
        }

        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, durationOptions);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durationSpinner.setAdapter(durationAdapter);
    }

    public void setText(View view) {
        TextView buildingTextView = view.findViewById(R.id.buildingSeat);
        buildingTextView.setText(building.getName());
        TextView roomTextView = view.findViewById(R.id.roomSeat);
        roomTextView.setText(seat.getRoomName());
        TextView seatNumberTextView = view.findViewById(R.id.seatNumberSeat);
        seatNumberTextView.setText(Long.toString(seat.getSeatNumber()));
        TextView descriptionTextView = view.findViewById(R.id.descriptionSeat);
        descriptionTextView.setText(seat.getDescription());
    }

    public void setSprinner() {
//        Log.e("Firestore", "Error seat data", e);
        Log.e("Firestore", String.valueOf(building.getOpenTime()));
        float openTime = building.getOpenTime() / 2;
        float closeTime = building.getClose() / 2;
        int startTimeInMinutes = (int)(openTime * 60); // 8:00 AM (8 * 60)
        int endTimeInMinutes = (int)(closeTime * 60);
        System.out.println(startTimeInMinutes);
        System.out.println(endTimeInMinutes);
        List<String> timeOptions = new ArrayList<>();
        for (int minutes = startTimeInMinutes; minutes <= endTimeInMinutes; minutes += 30) {
            int hours = minutes / 60;
            int mins = minutes % 60;
            String time = String.format("%02d:%02d", hours, mins);
            timeOptions.add(time);
        }
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, timeOptions);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startTimeSpinner.setAdapter(timeAdapter);

    }
}