package com.pax.market.api.sdk.java.base.dto;

import com.google.gson.annotations.SerializedName;

public class MerchantObject extends SdkObject{
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MerchantObject{" +
                "name='" + name + '\'' +
                ", message=" + getMessage() +
                '}';
    }
}
