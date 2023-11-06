package com.example.findaseat;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.findaseat.Utils.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReserveFragment extends Fragment {

    private int currentDay = 0; // 0 means today
    private ArrayList<Seat> seats;

    private ArrayList<Integer> indoor;
    private ArrayList<Integer> outdoor;
    private String currentuser = "lj@usc.edu";
    private ArrayList<View> selectedGrids;

    private Date selectedStart;
    private Date selectedEnd;

    private int selectedID;
    private ArrayList<Reservation> testreserves = new ArrayList<Reservation>();

    private Reservation currentReserve;
    private String building;
    private int openTime;
    private int closeTime;
    private String buildingName;

    private Button nextButton;
    private Button previousButton;
    private Button reserveButton;
    private Button cancelButton;

    private FirebaseFirestore db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.reserve, container, false);
        building = "Doheny"; //for testing
        db = FirebaseFirestore.getInstance();
        seats = new ArrayList<>();
        indoor = new ArrayList<>();
        outdoor = new ArrayList<>();

//functions to be implemented:
        //1. when loading this page, check if user has active reservation
        //2. implement the user









        selectedGrids = new ArrayList<>();

        //Reservation object for testing
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.DATE, 1); // Add one day to get tomorrow
        startCalendar.set(Calendar.HOUR_OF_DAY, 14); // Set hour to 14:00
        startCalendar.set(Calendar.MINUTE, 0); // Set minute to 0
        startCalendar.set(Calendar.SECOND, 0); // Set second to 0
        startCalendar.set(Calendar.MILLISECOND, 0); // Set millisecond to 0
        Date startTime = startCalendar.getTime();

        Calendar endCalendar = (Calendar) startCalendar.clone();
        endCalendar.add(Calendar.MINUTE, 30); // Add 30 minutes to get the end time
        Date endTime = endCalendar.getTime();

        Reservation reservation = new Reservation(1, startTime, endTime, true, false);
        testreserves.add(reservation);

        TableLayout tableLayout = view.findViewById(R.id.tableLayout);
        nextButton = view.findViewById(R.id.nextButton);
        previousButton = view.findViewById(R.id.previousButton);
        reserveButton = view.findViewById(R.id.reserveButton);
        cancelButton = view.findViewById(R.id.clearSelection);
        cancelButton.setEnabled(false);

        TextView reminder = view.findViewById(R.id.reminder);
        if(currentReserve != null){
            reserveButton.setEnabled(false);
            reminder.setText("You already have an active reservation. You can have at most 1 reservation at a time");
        }
        //set onClick listeners of the previous and next buttons
        nextButton.setOnClickListener(v -> {
            currentDay++;
            previousButton.setEnabled(true);
            if(currentDay == 6){
                nextButton.setEnabled(false);
            }
            updateTableDate(view,currentDay);
        });
        previousButton.setOnClickListener(v -> {
            currentDay--;
            nextButton.setEnabled(true);
            if(currentDay == 0){
                previousButton.setEnabled(false);
            }
            updateTableDate(view,currentDay);
        });

        reserveButton.setOnClickListener(v -> {
            if (selectedGrids.size() <= 4 && selectedValid()) {
                Reservation newreservation = new Reservation(selectedID, selectedStart, selectedEnd, true, false);
                testreserves.add(newreservation);
                currentReserve = newreservation;
                reserveButton.setEnabled(false);
                reminder.setText("You have an active reservation. At most 1 reservation at a time");
                updateTableDate(view, currentDay);
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("You can only reserve for one seat at a time. Max period (consecutive) is 2 hours");
                builder.setCancelable(true); // Ensure dialog is cancellable

                builder.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        cancelButton.setOnClickListener(v -> {
            selectedGrids.clear();
            updateTableDate(view,currentDay);
        });

        ;
        db.collection("buildings").document("Doheny")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot buildingDocument = task.getResult();
                    if (buildingDocument.exists()) {
                        buildingName = buildingDocument.getString("name");
                        openTime = buildingDocument.getLong("open").intValue();
                        closeTime = buildingDocument.getLong("close").intValue();

                    } else {
                        System.err.println("Building does not exist");
                    }
                } else {
                    System.err.println("Error in retrieving building");
                }
            }
        });

        db.collection("building").document("Doheny")
                .collection("seats").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot seatDocument : task.getResult()) {
                        int seatId = seatDocument.getLong("id").intValue();
                        String room = seatDocument.getString("room");
                        boolean isInside = seatDocument.getBoolean("inside");
                        if(isInside){
                            indoor.add(seatId);
                        }else{
                            outdoor.add(seatId);
                        }
                        Seat newseat = new Seat(seatId,isInside,false,room);
                        seats.add(newseat);
                    }
                } else {
                    System.err.println("Error in retrieving seats");
                }
            }
        });

        //setting up the table
        for (int i = 0; i <= seats.size(); i++) {
            TableRow row = new TableRow(view.getContext());

            for (int j = 0; j < 49; j++) {
                TextView textView = new TextView(view.getContext());
                String tag = "R" + i + "C" + j;  // R1C2
                textView.setTag(tag);
                // set the rwo header, time slots of 30 minutes
                if (i == 0 && j != 0) {
                    textView.setTextColor(Color.YELLOW);
                    String content;
                    if (j % 2 == 1) {
                        // Odd indices
                        content = String.format("%d:00 - %d:30", j/2, j/2);

                    } else {
                        // Even indices
                        content = String.format("%d:30 - %d:00", j/2 - 1, j/2);
                    }
                    content += String.format("\nIndoor: %d", 0);
                    content += String.format("\nOutdoor: %d", 0);
                    textView.setBackgroundColor(Color.RED);
                    textView.setText(content);
                }
                // set the column header, IDs of seats in one building
                else if (j == 0 && i != 0) {
                    textView.setText(seats.get(i-1).getRoom() + "/" + seats.get(i-1).getSeatId());
                    textView.setTextColor(Color.RED);
                    textView.setBackgroundColor(Color.YELLOW);
                }
                // the (1,1) entry in the table is left blank
                else if (i == 0 && j == 0) {
                    textView.setTextColor(Color.YELLOW);
                    textView.setText("Room ID\n\n");
                    textView.setBackgroundColor(Color.RED);
                }
                else {
                    textView.setText("");
                }
                textView.setPadding(20, 20, 20, 20);
                row.addView(textView);
            }
            tableLayout.addView(row);
        }
        updateTableDate(view,0); //show the schedules of today

        return view;

    }

    private int TimeToColumn(Date time) {
        // Convert the given time to the corresponding column index, in
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return hour * 2 + (minute >= 30 ? 1 : 0) + 1;  // start time is either x:00 or x:30
    }

    private void updateTableDate(View root, int day) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, day); // Based on currentDay, figure out the date
        Date currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
        String dateString = dateFormat.format(currentDate);
        TextView currentdayText = root.findViewById(R.id.date);
        currentdayText.setText(dateString);//the date current table is showing

        // Clear previous colors and set all to green
        setDefaultTable(root);

        // Update colors based on reservations for currentDate
        db.collection("reservations")
                .whereEqualTo("building",building)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot reservation : task.getResult()) {
                                if (isSameDay(reservation.getTimestamp("startTime").toDate(), currentDate)) {
                                    int seat = reservation.getLong("seatId").intValue();
                                    int Colstart = TimeToColumn(reservation.getTimestamp("startTime").toDate());
                                    int Colend = TimeToColumn(reservation.getTimestamp("endTime").toDate());
                                    String user = reservation.getString("user");
                                    while(Colstart < Colend){
                                        String tag = "R" + seat + "C" + Colstart;
                                        TextView cellView = root.findViewById(R.id.tableLayout).findViewWithTag(tag);
                                        if (cellView != null) {
                                            updateheader(root,seat,Colstart,true);
                                            if(user.equals(currentuser)){
                                                cellView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.dark_orange));
                                            }else{
                                                cellView.setBackgroundColor(Color.RED);
                                            }

                                            cellView.setEnabled(false);
                                        }
                                        Colstart++;
                                    }

                                }
                            }
                        } else {
                            System.err.println("Error in retrieving reservations");
                        }
                    }
                });


    }

    private void setDefaultTable(View root) {
        TableLayout tableLayout = root.findViewById(R.id.tableLayout);


        //set indoor/outdoor seat number
        for(int j = 1; j < 49; j++){
            if(j >= openTime + 1 && j < closeTime + 1) {
                String tag = "R" + 0 + "C" + j;
                TextView header = tableLayout.findViewWithTag(tag);
                String content = header.getText().toString();
                String[] lines = content.split("\n");
                lines[1] = "Indoor: " + indoor.size();
                lines[2] = "Outdoor: " + outdoor.size();
                content = TextUtils.join("\n", lines);
                header.setText(content);
            }
        }
        // for each time slot, first row and column excluded
        for (int i = 1; i <= seats.size(); i++) {
            for (int j = 1; j < 49; j++) {
                String tag = "R" + i + "C" + j;
                TextView cellView = tableLayout.findViewWithTag(tag);
                if (cellView != null) {
                    boolean b = Math.abs(i - j) % 2 == 0;
                    if(j < openTime + 1 || j >= closeTime + 1){
                        if(b){
                            cellView.setBackgroundColor(Color.LTGRAY);
                        }
                        else{
                            cellView.setBackgroundColor(Color.GRAY);
                        }
                    }
                    else if (b) {
                        cellView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
                        cellView.setOnClickListener(v -> {
                            if (selectedGrids.contains(v)) {
                                v.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
                                selectedGrids.remove(v);
                            } else {
                                v.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_orange));
                                selectedGrids.add(v);
                            }
                            if(!selectedGrids.isEmpty()){
                                cancelButton.setEnabled(true);
                            }
                        });
                    } else {
                        cellView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.darkgreen));
                        cellView.setOnClickListener(v -> {
                            if (selectedGrids.contains(v)) {
                                v.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.darkgreen));
                                selectedGrids.remove(v);
                            } else {
                                v.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_orange)); // This is orange
                                selectedGrids.add(v);
                            }
                        });
                    }
                }
            }
        }
    }


    private boolean selectedValid() {
        if (selectedGrids.isEmpty()) return false;
        ArrayList<Integer> seatNumbers = new ArrayList<>();
        ArrayList<Integer> timeSlots = new ArrayList<>();

        for (View grid : selectedGrids) {
            String tag = (String) grid.getTag();
            Log.d("ReserveCheck", "Grid tag: " + tag);
            String[] parts = tag.split("C"); // Tag format is like R1C2
            int seatNum = Integer.parseInt(parts[0].substring(1)); // Get the seat number
            int timeSlot = Integer.parseInt(parts[1]) - 1; // Get the time slot
            Log.d("ReserveCheck", "Seat number: " + seatNum + ", Time slot: " + timeSlot);
            seatNumbers.add(seatNum);
            timeSlots.add(timeSlot);
        }

        // must be one seat
        int firstSeatNumber = seatNumbers.get(0);
        for (int seatNumber : seatNumbers) {
            if (seatNumber != firstSeatNumber)
                return false;
        }
        selectedID = firstSeatNumber;
        // time should be consecutive
        Collections.sort(timeSlots);
        for (int i = 0; i < timeSlots.size() - 1; i++) {
            if (timeSlots.get(i) + 1 != timeSlots.get(i + 1))
                return false;
        }
        Calendar start = Calendar.getInstance();
        start.add(Calendar.DATE, currentDay);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        start.set(Calendar.HOUR_OF_DAY, timeSlots.get(0)/2);
        start.set(Calendar.MINUTE, timeSlots.get(0) % 2 * 30);
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.MINUTE, timeSlots.size()*30); // get end time

        selectedStart = start.getTime();
        selectedEnd = end.getTime();
        return true;
    }


    private void updateheader(View root,int seatID, int col, boolean decrease){
        if(indoor.contains(seatID)){
            String tag = "R" + 0 + "C" + col;
            TextView header = root.findViewById(R.id.tableLayout).findViewWithTag(tag);
            String newContent = header.getText().toString();
            String[] lines = newContent.split("\n");
            int num = Integer.parseInt(lines[1].split(" ")[1]);
            if(decrease){
                num --;
            }else{
                num ++;
            }
            lines[1] = "Indoor: " + num;
            newContent = TextUtils.join("\n",lines);
            header.setText(newContent);
        }else{
            String tag = "R" + 0 + "C" + col;
            TextView header = root.findViewById(R.id.tableLayout).findViewWithTag(tag);
            String newContent = header.getText().toString();
            String[] lines = newContent.split("\n");
            int num = Integer.parseInt(lines[2].split(" ")[1]);
            if(decrease){
                num --;
            }else{
                num ++;
            }
            lines[2] = "Outdoor: " + num;
            newContent = TextUtils.join("\n",lines);
            header.setText(newContent);
        }
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public String IntToTime(int time) {
        int hours = time / 2;
        int minutes = (time % 2) * 30;
        return String.format("%02d:%02d", hours, minutes);
    }

}