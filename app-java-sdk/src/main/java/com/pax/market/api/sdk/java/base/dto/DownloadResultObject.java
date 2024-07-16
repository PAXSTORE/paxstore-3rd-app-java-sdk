package com.pax.market.api.sdk.java.base.dto;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


/**
 * Created by zhangchenyang on 2018/5/16.
 */
public class DownloadResultObject extends SdkObject {

    /**
     * path that param files saved
     */
    @SerializedName("paramSavePath")
    String paramSavePath;

    @SerializedName("actionList")
    ArrayList<Long> actionList;

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
        return "DownloadResultObject{" +
                "paramSavePath='" + paramSavePath + '\'' +
                ", actionList=" + actionList +
                '}';
    }
}
