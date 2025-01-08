package com.pax.market.api.sdk.java.base.dto;

import com.google.gson.annotations.SerializedName;

public class PendingTaskObject extends SdkObject{
    @SerializedName("hasPendingTask")
    private Boolean hasPendingTask;

    public Boolean getHasPendingTask() {
        return hasPendingTask;
    }

    public void setHasPendingTask(Boolean hasPendingTask) {
        this.hasPendingTask = hasPendingTask;
    }

    @Override
    public String toString() {
        return "PendingTaskObject{" +
                "hasPendingTask=" + hasPendingTask + '\'' +
                "businessCode=" + getBusinessCode() + '\'' +
                "message=" + getMessage() +
                '}';
    }
}
