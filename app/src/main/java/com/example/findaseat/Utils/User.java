package com.example.findaseat.Utils;

import android.app.Application;
import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.ArrayList;

public class User extends Application {

    private String first;
    private String last;
    private String email;

    private String uscID;
    private String affiliation;
    // https://stackoverflow.com/questions/19285556/how-to-store-an-image-in-a-variable-in-java-android-development

    private Boolean loggedIn = false;

    public User(String first, String last, String email, String uscID, String affiliation, Boolean loggedIn) {
        this.first = first;
        this.last = last;
        this.email = email;
        this.uscID = uscID;
        this.affiliation = affiliation;
        this.loggedIn = loggedIn;
    }

    public User() {
        this.first = "";
        this.last = "";
        this.email = "";
        this.uscID = "";
        this.affiliation = "";
        this.loggedIn = false;
    }


    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUscID() {
        return uscID;
    }

    public void setUscID(String uscID) {
        this.uscID = uscID;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
