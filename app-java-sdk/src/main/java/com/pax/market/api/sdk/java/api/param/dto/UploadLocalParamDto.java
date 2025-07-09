package com.pax.market.api.sdk.java.api.param.dto;

import java.util.Map;

public class UploadLocalParamDto {
    Long versionCode;
    String paramTemplateName;
    Map<String,String> paramChangedList;

    public UploadLocalParamDto() {
    }

    public String getParamTemplateName() {
        return paramTemplateName;
    }

    public void setParamTemplateName(String paramTemplateName) {
        this.paramTemplateName = paramTemplateName;
    }

    public Long getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Long versionCode) {
        this.versionCode = versionCode;
    }

    public Map<String, String> getParamChangedList() {
        return paramChangedList;
    }

    public void setParamChangedList(Map<String, String> paramChangedList) {
        this.paramChangedList = paramChangedList;
    }
}
