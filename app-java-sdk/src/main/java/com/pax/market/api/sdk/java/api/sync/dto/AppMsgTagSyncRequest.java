package com.pax.market.api.sdk.java.api.sync.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
/**
 * Created by john on 2021/10/25.
 */
public class AppMsgTagSyncRequest implements Serializable {
    @SerializedName("serialNo")
    private String serialNo;
    @SerializedName("attachTagNames")
    private List<String> attachTagNames;
    @SerializedName("detachTagNames")
    private List<String> detachTagNames;

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public List<String> getAttachTagNames() {
        return attachTagNames;
    }

    public void setAttachTagNames(List<String> attachTagNames) {
        this.attachTagNames = attachTagNames;
    }

    public List<String> getDetachTagNames() {
        return detachTagNames;
    }

    public void setDetachTagNames(List<String> detachTagNames) {
        this.detachTagNames = detachTagNames;
    }
}
