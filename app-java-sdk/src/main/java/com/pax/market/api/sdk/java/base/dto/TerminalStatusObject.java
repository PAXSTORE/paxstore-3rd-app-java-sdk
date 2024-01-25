package com.pax.market.api.sdk.java.base.dto;

import com.google.gson.annotations.SerializedName;

public class TerminalStatusObject extends SdkObject{
    @SerializedName("status")
    private String status;
    @SerializedName("description")
    private String description;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TerminalStatusObject{" +
                "status='" + status + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
