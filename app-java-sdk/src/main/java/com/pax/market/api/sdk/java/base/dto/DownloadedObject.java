package com.pax.market.api.sdk.java.base.dto;

import java.io.Serializable;

public class DownloadedObject implements Serializable {
    private static final long serialVersionUID = 1L;

    private long actionId;
    private String path;
    private boolean isPartial;
    private Long effectiveTime;

    public long getActionId() {
        return actionId;
    }

    public void setActionId(long actionId) {
        this.actionId = actionId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isPartial() {
        return isPartial;
    }

    public void setPartial(boolean partial) {
        isPartial = partial;
    }

    public Long getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Long effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    @Override
    public String toString() {
        return "DownloadedObject{" +
                "actionId=" + actionId +
                ", path='" + path + '\'' +
                ", isPartial=" + isPartial +
                ", effectiveTime=" + effectiveTime +
                '}';
    }
}
