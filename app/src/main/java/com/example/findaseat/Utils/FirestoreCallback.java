package com.example.findaseat.Utils;


public interface FirestoreCallback {
    void onFirestoreQueryComplete();

    void onFirestoreQueryComplete(boolean b);
}

