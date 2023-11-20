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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findaseat.Utils.Building;
import com.example.findaseat.Utils.MyFirestoreCallback;
import com.example.findaseat.Utils.Reservation;
import com.example.findaseat.Utils.ReservationSeatAdapter;
import com.example.findaseat.Utils.Seat;
import com.example.findaseat.Utils.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


public class SeatFragment extends Fragment {



    Seat seat;

    Building building;

    Spinner startTimeSpinner;
    private Spinner durationSpinner;

    private FirebaseFirestore db;
    private List<Reservation> reservationsList;
    private RecyclerView recyclerView;
    private ReservationSeatAdapter adapter;

    private boolean success = false;

    MaterialButton reserve;

    ProgressBar progressBar;

    boolean check = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seat, container, false);

        Bundle args = getArguments();
        if (args != null) {
            seat = (Seat) args.getSerializable("seat");
            building = (Building) args.getSerializable("building");
        }

        progressBar = view.findViewById(R.id.progress);

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
                progressBar.setVisibility(View.VISIBLE);
                Timestamp reservationStart = getTime(0);
                Timestamp currentTimestamp = Timestamp.now();
                if (currentTimestamp.compareTo(reservationStart) > 0) {
                    Toast.makeText(getActivity(), "Reservation Passed.", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                String selectedDuration = durationSpinner.getSelectedItem().toString();
                String[] durationParts = selectedDuration.split(":");
                int durationHours = Integer.parseInt(durationParts[0]);
                int durationMinutes = Integer.parseInt(durationParts[1]);
                int totalDurationInMinutes = (durationHours * 60) + durationMinutes;
                Timestamp reservationEnd = getTime(totalDurationInMinutes);

                Timestamp buildingClose = close(building.closeTime());


                if (reservationEnd.compareTo(buildingClose) > 0) {
                    Toast.makeText(getActivity(), "Building will Close at " + building.closeTime(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                applyReservation(reservationStart, reservationEnd);


            }
        });





        return view;
    }

    private boolean applyReservation(Timestamp reservationStart, Timestamp reservationEnd) {
        Long selectedSeatId = seat.getId();
        db = FirebaseFirestore.getInstance();
        CollectionReference reservationsCollection = db.collection("reservations");
        AtomicBoolean isOverlap = new AtomicBoolean(false);


        MyFirestoreCallback callback = new MyFirestoreCallback() {
            @Override
            public void onFirestoreQueryComplete(boolean b) {
                if(b) {
                    writeDate(reservationStart, reservationEnd);
                    progressBar.setVisibility(View.GONE);

                } else {
                    Toast.makeText(getActivity(), "Fail", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }

            }
        };

        fetchDataFromFirestore(callback, reservationStart, reservationEnd, selectedSeatId, db);



        return true;


    }

    private void writeDate(Timestamp reservationStart, Timestamp reservationEnd) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reservationsCollection = db.collection("reservations");
        User user = (User) getActivity().getApplicationContext();

        Map<String, Object> reservationData = new HashMap<>();
        reservationData.put("building", this.building.getId());
        reservationData.put("cancelled", false);
        reservationData.put("created", Timestamp.now());
        reservationData.put("endTime", reservationEnd); // Replace with your end time
        reservationData.put("room", seat.getRoomName());
        reservationData.put("seatId", seat.getId());
        reservationData.put("startTime", reservationStart); // Replace with your start time
        reservationData.put("user", user.getEmail());
        reservationsCollection.add(reservationData)
                .addOnSuccessListener(documentReference -> {
                    // Data added successfully
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
                    // Handle success, if needed
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Fail", Toast.LENGTH_SHORT).show();
                    // Error occurred
                    // Handle the error, if needed
                });

        Timestamp firestoreTimestamp = Timestamp.now();
        reservationsList.clear();
        db.collection("reservations")
                .whereEqualTo("seatId", seat.getId()) // Filter by the current seat
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
    }

    private void fetchDataFromFirestore(final MyFirestoreCallback callback, Timestamp reservationStart, Timestamp reservationEnd, Long selectedSeatId, FirebaseFirestore db) {
        Timestamp currentTimestamp = Timestamp.now();
        Date currentDate = currentTimestamp.toDate();
        db.collection("reservations")
                .whereGreaterThanOrEqualTo("endTime", currentDate)
                .whereEqualTo("cancelled", false)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    QuerySnapshot reservations = queryDocumentSnapshots;
                    if (reservations != null) {
                        boolean allPass = true;
                        for (QueryDocumentSnapshot reservation : reservations) {
                            if (reservation != null) {
                                if (checkOverlap(reservation, reservationStart, reservationEnd)) {
                                    callback.onFirestoreQueryComplete(false);
                                    allPass = false;
                                }

                            }
                        }
                        if (allPass) { callback.onFirestoreQueryComplete(true); }

                    } else {callback.onFirestoreQueryComplete(true);}
                }).addOnFailureListener(e -> {
                    // An error occurred during the query
                    // You can print the error message for debugging or provide user feedback

                    Log.e("QueryError", "Error querying reservations: " + e.getMessage());

                    // Handle the error or show an error message to the user
                    // For example, you can display a toast or update the UI with an error message
                });
        Log.e("Check", "Did we get here");
    }

    //Refactored showToast() for testing
    void showToast(String k){
        Toast.makeText(getActivity(), k, Toast.LENGTH_SHORT).show();
    }
    boolean checkOverlap(QueryDocumentSnapshot reservation, Timestamp reservationStart, Timestamp reservationEnd) {
//        Log.e("Timecheck", reservation.getTimestamp("startTime").toString());
//        Log.e("Timecheck", reservation.getTimestamp("endTime").toString());
//        Log.e("Timecheck", reservationStart.toString());
//        Log.e("Timecheck", reservationEnd.toString());
        Timestamp sT = reservation.getTimestamp("startTime");
        Timestamp eT = reservation.getTimestamp("endTime");

        long existingStart = sT.getSeconds();
        long existingEnd = eT.getSeconds();
        long newStart = reservationStart.getSeconds();
        long newEnd = reservationEnd.getSeconds();
//        Log.e("Timecheck", Long.toString(existingStart));
//        Log.e("Timecheck", Long.toString(existingEnd));
//        Log.e("Timecheck", Long.toString(newStart));
//        Log.e("Timecheck", Long.toString(newEnd));
        User user = (User) getActivity().getApplicationContext();

        String s = reservation.getString("user");
        String u = user.getEmail();


        if (s.equals(u)) {
            showToast("Multiple Reservations Made by User");
            return true;
        }

        if (reservation.getLong("seatId") != seat.getId()) {
            return false;
        }

        if (existingStart == newStart || existingEnd == newEnd) {
            showToast("Overlapping Reservation");
            return true;
        }
        if (newStart < existingStart && newEnd > existingStart) {
            showToast("Overlapping Reservation");
            return true;
        }
        if (newStart > existingStart && newEnd < existingStart) {
            showToast("Overlapping Reservation");
            return true;
        }
        if (newStart > existingStart && newStart < existingEnd){
            showToast("Overlapping Reservation");
            return true;
        }
        return false;
    }

    Timestamp getTime(int minutes) {
        Timestamp firestoreTimestamp = Timestamp.now(); // Replace with the selected date
        String selectedStartTime = startTimeSpinner.getSelectedItem().toString(); // Replace with the selected start time
//        System.out.println(selectedStartTime);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
        try {
            // Parse the selected military time string into a Date object
            Date parsedTime = timeFormat.parse(selectedStartTime);

            // Get the current date
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();

            Calendar reservationCalendar = Calendar.getInstance();
            reservationCalendar.setTime(currentDate); // Set the date portion
            reservationCalendar.set(Calendar.HOUR_OF_DAY, parsedTime.getHours()); // Set the hours
            reservationCalendar.set(Calendar.MINUTE, parsedTime.getMinutes()); // Set the minutes
            reservationCalendar.set(Calendar.SECOND, 0);
            reservationCalendar.add(Calendar.MINUTE, minutes);
            // Get the Date from the combined Calendar
            Date reservationDate = reservationCalendar.getTime();

            // Create a Firestore Timestamp from the Date
            Timestamp timestamp = new Timestamp(reservationDate);

//            Log.d("TimeStartCreation", timestamp.toDate().toString());
            return timestamp;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    Timestamp close(String time) {
        System.out.println(time);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US); // 24-hour format
        try {
            Date closingTimeDate = timeFormat.parse(time);

            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();

            Calendar reservationCalendar = Calendar.getInstance();
            reservationCalendar.setTime(currentDate); // Set the date portion
            reservationCalendar.set(Calendar.HOUR_OF_DAY, closingTimeDate.getHours()); // Set the hours
            reservationCalendar.set(Calendar.MINUTE, closingTimeDate.getMinutes()); // Set the minutes
            reservationCalendar.set(Calendar.SECOND, 0);

            // Get the Date from the combined Calendar
            Date reservationDate = reservationCalendar.getTime();

            // Create a Firestore Timestamp from the Date
            Timestamp timestamp = new Timestamp(reservationDate);
//            Log.d("CloseCreation", timestamp.toDate().toString());
            return timestamp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    Reservation createReservation(QueryDocumentSnapshot reservation) {
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
        roomTextView.setText("Room: " + seat.getRoomName());
        TextView seatNumberTextView = view.findViewById(R.id.seatNumberSeat);
        seatNumberTextView.setText("Seat Number " + Long.toString(seat.getSeatNumber()));
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
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(startTimeSpinner);

            // Set popupWindow height to 500px
            popupWindow.setHeight(300);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
            Log.e("Spinner Fail", "SettingLength");
        }
    }
}