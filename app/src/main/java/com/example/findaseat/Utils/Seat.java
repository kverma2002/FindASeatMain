package com.example.findaseat.Utils;

public class Seat {
    private int seatId;
    private boolean inside;
    private boolean isOccupied;

    private String room;

    public Seat(int seatId, boolean inside, boolean isOccupied, String room) {
        this.seatId = seatId;
        this.inside = inside;
        this.isOccupied = isOccupied;
        this.room = room;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public void reserveSeat(int id) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    public void cancelSeat(int id) {
        throw new UnsupportedOperationException("Not yet Implemented");
    }

    public boolean isInside() {
        return inside;
    }

    public void setInside(boolean inside) {
        this.inside = inside;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
