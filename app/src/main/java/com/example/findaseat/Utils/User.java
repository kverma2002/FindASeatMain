package com.example.findaseat.Utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.ArrayList;

public class User {

    private int userID;
    private String password;
    private int affiliation;
    // https://stackoverflow.com/questions/19285556/how-to-store-an-image-in-a-variable-in-java-android-development
    private Bitmap image;
    private ImageView profilePicture;
    private ArrayList<Reservation> seatHistory;

    private Boolean loggedIn;

    public User(int userID, String password, int affiliation, Bitmap image, ImageView profilePicture, ArrayList<Reservation> seatHistory) {
        this.userID = userID;
        this.password = password;
        this.affiliation = affiliation;
        this.image = image;
        this.profilePicture = profilePicture;
        this.seatHistory = seatHistory;
        this.loggedIn = true;
    }
    public User() {
        this.userID = 0;
        this.password = "password";
        this.affiliation = 0;
        this.image = null;
        this.profilePicture = null;
        this.seatHistory = null;
        this.loggedIn = false;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(int affiliation) {
        this.affiliation = affiliation;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public ImageView getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ImageView profilePicture) {
        this.profilePicture = profilePicture;
    }

    public ArrayList<Reservation> getSeatHistory() {
        return seatHistory;
    }

    public void setSeatHistory(ArrayList<Reservation> seatHistory) {
        this.seatHistory = seatHistory;
    }

    public boolean isLoggedIn() {return loggedIn; }


}
