package com.example.findaseat.Utils;

public class MyFirestoreCallback implements FirestoreCallback {
    @Override
    public void onFirestoreQueryComplete() {
        // This method will be called when the Firestore query is complete.
        // You can implement the logic you want here.
        System.out.println("Nice");
    }
}