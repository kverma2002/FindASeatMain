package com.example.findaseat.Utils;

import java.util.Date;

public class Reservation {
    private String startTime;
    private String endTime;
    private String room;
    private String building;

    private String date;

    private String id;

    boolean cacelled;

    public Reservation() {
        // Default constructor required for Firestore
    }

    public Reservation(String startTime, String endTime, String room, String building, String date, String id, boolean cacelled) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.building = building;
        this.date = date;
        this.id = id;
        this.cacelled = cacelled;
    }



    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getRoom() {
        return room;
    }

    public String getBuilding() {
        return building;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public boolean isCancelled() {return cacelled;}



}
