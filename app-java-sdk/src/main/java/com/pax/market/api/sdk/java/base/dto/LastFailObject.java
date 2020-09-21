package com.pax.market.api.sdk.java.base.dto;

import java.io.Serializable;

public class LastFailObject implements Serializable {
    private long actionId;
    private int retryCount;
    private long firstTryTime;

    public long getActionId() {
        return actionId;
    }

    public void setActionId(long actionId) {
        this.actionId = actionId;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public long getFirstTryTime() {
        return firstTryTime;
    }

    public void setFirstTryTime(long firstTryTime) {
        this.firstTryTime = firstTryTime;
    }

    @Override
    public String toString() {
        return "LastFailTask{" +
                "actionId=" + actionId +
                ", retryCount=" + retryCount +
                ", firstTryTime=" + firstTryTime +
                '}';
    }
}
