package com.pax.market.api.sdk.java.base.dto;

import com.google.gson.annotations.SerializedName;



public class DataWrapperObject extends SdkObject {

    @SerializedName("data")
    private DataQueryResultObject data;

    public DataQueryResultObject getData() {
        return data;
    }

    public void setData(DataQueryResultObject data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DataWrapperObject{" +
                "data=" + data +
                '}';
    }
}