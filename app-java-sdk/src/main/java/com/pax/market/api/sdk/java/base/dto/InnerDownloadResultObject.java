package com.pax.market.api.sdk.java.base.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.LinkedList;

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
    @SerializedName("actionList")
    ArrayList<Long> actionList;

    @SerializedName("paramList")
    LinkedList<DownloadedObject> downloadedParamList;


    public LinkedList<DownloadedObject> getDownloadedParamList() {
        return downloadedParamList;
    }

    public void setDownloadedParamList(LinkedList<DownloadedObject> downloadedParamList) {
        this.downloadedParamList = downloadedParamList;
    }

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

    public void setActionList(ArrayList<Long> actionList) {
        this.actionList = actionList;
    }

    public ArrayList<Long> getActionList() {
        return actionList;
    }


    @Override
    public String toString() {
        return "InnerDownloadResultObject{" +
                "paramSavePath='" + paramSavePath + '\'' +
                ", lastFailObject=" + lastFailObject +
                ", actionList=" + actionList +
                '}';
    }
}
