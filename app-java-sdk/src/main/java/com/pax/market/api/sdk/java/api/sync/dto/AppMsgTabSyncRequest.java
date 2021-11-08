package com.pax.market.api.sdk.java.api.sync.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
/**
 * Created by john on 2021/10/25.
 */
public class AppMsgTabSyncRequest implements Serializable {
    @SerializedName("serialNo")
    private String serialNo;
    @SerializedName("tabNames")
    private List<String> tabNames;
    @SerializedName("deleteTabNames")
    private List<String> deleteTabNames;

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public List<String> getTabNames() {
        return tabNames;
    }

    public void setTabNames(List<String> tabNames) {
        this.tabNames = tabNames;
    }

    public List<String> getDeleteTabNames() {
        return deleteTabNames;
    }

    public void setDeleteTabNames(List<String> deleteTabNames) {
        this.deleteTabNames = deleteTabNames;
    }
}
