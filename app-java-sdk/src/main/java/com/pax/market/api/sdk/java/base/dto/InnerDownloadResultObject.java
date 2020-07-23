package com.pax.market.api.sdk.java.base.dto;

import com.google.gson.annotations.SerializedName;

/**
 * This is for aar use, not for users.
 */
public class InnerDownloadResultObject extends SdkObject {

    /**
     * path that param files saved
     */
    @SerializedName("paramSavePath")
    String paramSavePath;
    /**
     * download fail record
     */
    @SerializedName("failRecord")
    LastFailObject lastFailObject;

    public LastFailObject getLastFailObject() {
        return lastFailObject;
    }

    public void setLastFailObject(LastFailObject lastFailObject) {
        this.lastFailObject = lastFailObject;
    }

    public String getParamSavePath() {
        return paramSavePath;
    }

    public void setParamSavePath(String paramSavePath) {
        this.paramSavePath = paramSavePath;
    }

    @Override
    public String toString() {
        return "InnerDownloadResultObject{" +
                "paramSavePath='" + paramSavePath + '\'' +
                ", lastFailObject=" + lastFailObject +
                '}';
    }
}
