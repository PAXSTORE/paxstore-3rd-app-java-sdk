package com.pax.market.api.sdk.java.base.dto;

import com.google.gson.annotations.SerializedName;

public class LocationObject extends SdkObject{
    @SerializedName("lng")
    private float longitude;
    @SerializedName("lat")
    private float latitude;

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "LocationObject{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
