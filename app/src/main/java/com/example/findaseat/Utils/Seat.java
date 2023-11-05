package com.example.findaseat.Utils;

public class Seat {
    private int seatId;
    private boolean isOccupied;

    public Seat(int seatId, boolean isOccupied) {
        this.seatId = seatId;
        this.isOccupied = isOccupied;
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
}
