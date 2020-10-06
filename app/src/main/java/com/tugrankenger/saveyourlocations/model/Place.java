package com.tugrankenger.saveyourlocations.model;

import java.io.Serializable;

public class Place implements Serializable {

    public String name;
    public double latitude;
    public double longitude;

    public Place(String name, Double latitude, Double longitude){
        this.name=name;
        this.latitude =latitude;
        this.longitude=longitude;
    }
}
