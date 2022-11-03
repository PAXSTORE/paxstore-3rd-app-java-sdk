package com.pax.market.api.sdk.java.base.dto;

import com.google.gson.annotations.SerializedName;

public class LocationObject extends SdkObject{
    @SerializedName("lng")
    private float longitude;
    @SerializedName("lat")
    private float latitude;
    //This value is not returned by the server and is manually set by the sdk
    private Long lastLocateTime;

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

    public Long getLastLocateTime() {
        return lastLocateTime;
    }

    public void setLastLocateTime(Long lastLocateTime) {
        this.lastLocateTime = lastLocateTime;
    }

    @Override
    public String toString() {
        return "LocationObject{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", lastLocateTime=" + lastLocateTime +
                ", message=" + getMessage() +
                '}';
    }
}
