package com.example.findaseat.Utils;

import java.io.Serializable;

public class Seat implements Serializable {
    private long id;

    private String description;

    private boolean inside;

    private String room;

    private long number;

    public Seat() {
    }

    public Seat(long seatId, String description, boolean inside, String room, long number) {
        this.id = seatId;
        this.description = description;
        this.inside = inside;
        this.room = room;
        this.number = number;
    }

    public long getSeatNumber() {
        return number;
    }

    public String getDescription() {
        return description;
    }

    public boolean isInside() {
        return inside;
    }

    public String getRoomName() {
        return room;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInside(boolean inside) {
        this.inside = inside;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public long getId() {
        return id;
    }

    public void setNumber(long number) {
        this.number = number;
    }
}
