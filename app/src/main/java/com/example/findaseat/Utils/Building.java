package com.example.findaseat.Utils;

import java.util.ArrayList;

public class Building {
    public String name;
    private int id;
    private ArrayList<Seat> seats;

    public Building(String name, int id, ArrayList<Seat> seats) {
        this.name = name;
        this.id = id;
        this.seats = seats;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Seat> getSeats() {
        return seats;
    }

    public void setSeats(ArrayList<Seat> seats) {
        this.seats = seats;
    }

    public void reserveSeat(int id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void cancelSeat(int id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
