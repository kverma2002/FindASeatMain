package com.example.findaseat.Utils;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

public class Map {
    private GoogleMap mMap;
    private ArrayList<Building> buildingList;

    public Map(GoogleMap mMap, ArrayList<Building> buildingList) {
        this.mMap = mMap;
        this.buildingList = buildingList;
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    public ArrayList<Building> getBuildingList() {
        return buildingList;
    }

    public void setBuildingList(ArrayList<Building> buildingList) {
        this.buildingList = buildingList;
    }

    public void openBuilding(int buildingId) {
        throw new UnsupportedOperationException("Not yet Implemented");
    }
}
