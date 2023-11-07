package com.example.findaseat.Utils;

import android.widget.ProgressBar;

import java.io.Serializable;
import java.util.ArrayList;

public class Building implements Serializable {
    private long close;
    private long open;
    private String name;
    private String description;
    private String address;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Building() {
        // Default constructor required for Firestore.
    }

    public long getClose() {
        return close;
    }

    public String closeTime() {return MilitaryTimeMapper.getMilitaryTime((int)close);}

    public void setClose(long close) {
        this.close = close;
    }

    public long getOpenTime() {
        return open;
    }

    public void setOpen(long open) {
        this.open = open;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHours() {

        String o = Time.timeMap.get(this.open);
        String c = Time.timeMap.get(this.close);
        return (o + " - " + c);

    }
}