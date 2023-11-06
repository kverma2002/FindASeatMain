package com.example.findaseat.Utils;

import com.google.android.gms.maps.model.LatLng;

public class build {
    public String name;
    public LatLng latLng;

    public build(String name, LatLng latLng) {
        this.name = name;
        this.latLng = latLng;
    }
}
