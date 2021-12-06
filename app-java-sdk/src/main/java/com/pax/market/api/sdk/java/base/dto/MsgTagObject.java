package com.pax.market.api.sdk.java.base.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MsgTagObject extends SdkObject{
    @SerializedName("tagNames")
    private List<String> tags;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "MsgTagObject{" +
                "tags=" + tags +
                '}';
    }
}
