package com.example.findaseat.Utils;

import java.util.Date;

public class Reservation {
    private int seatID;
    private Date startTime;
    private Date endTime;
    private boolean isActive;
    private boolean isModified;

    public Reservation(int seatID, Date startTime, Date endTime, boolean isActive, boolean isModified) {
        this.seatID = seatID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isActive = isActive;
        this.isModified = isModified;
    }

    public int getSeatID() {
        return seatID;
    }

    public void setSeatID(int seatID) {
        this.seatID = seatID;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }

    public int getDuration() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void cancelReservation(int id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void modifyReservation(int id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
