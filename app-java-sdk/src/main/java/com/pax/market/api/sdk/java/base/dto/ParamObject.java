/*
 * *
 *     * ********************************************************************************
 *     * COPYRIGHT
 *     *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *     *   This software is supplied under the terms of a license agreement or
 *     *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *     *   or disclosed except in accordance with the terms in that agreement.
 *     *
 *     *      Copyright (C) 2017 PAX Technology, Inc. All rights reserved.
 *     * ********************************************************************************
 *
 */

package com.pax.market.api.sdk.java.base.dto;

import com.google.gson.annotations.SerializedName;


/**
 * Created by zcy on 2016/12/6 0006.
 */
public class ParamObject extends SdkObject {

    @SerializedName("actionId")
    private long actionId;
    @SerializedName("appId")
    private long appId;
    @SerializedName("versionCode")
    private int versionCode;
    @SerializedName("downloadUrl")
    private String downloadUrl;
    @SerializedName("paramVariables")
    private String paramVariables;
    @SerializedName("md")
    private String md;
    @SerializedName("wifiOnly")
    private boolean wifiOnly;

    @SerializedName("downloadUrlDetail")
    private CookieObject cookieDto; //下载cookies

    @SerializedName("sha256")
    private String sha256;

    @SerializedName("signature")
    private String signature;




    public long getActionId() {
        return actionId;
    }

    public void setActionId(long actionId) {
        this.actionId = actionId;
    }

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getParamVariables() {
        return paramVariables;
    }

    public void setParamVariables(String paramVariables) {
        this.paramVariables = paramVariables;
    }

    public String getMd() {
        return md;
    }

    public void setMd(String md) {
        this.md = md;
    }

    public boolean isWifiOnly() {
        return wifiOnly;
    }

    public void setWifiOnly(boolean wifiOnly) {
        this.wifiOnly = wifiOnly;
    }

    public String getCookieSignature() {
        if (cookieDto != null) {
            return cookieDto.getCookieSignature();
        }
        return null;
    }

    public String getCookieExpires() {
        if (cookieDto != null) {
            return cookieDto.getCookieExpires();
        }
        return null;
    }

    public String getCookieKeyPairId() {
        if (cookieDto != null) {
            return cookieDto.getCookieKeyPairId();
        }
        return null;
    }

    public CookieObject getCookieDto() {
        return cookieDto;
    }

    public void setCookieDto(CookieObject cookieDto) {
        this.cookieDto = cookieDto;
    }


    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "ParamObject{" +
                "actionId=" + actionId +
                ", appId=" + appId +
                ", versionCode=" + versionCode +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", paramVariables='" + paramVariables + '\'' +
                ", md='" + md + '\'' +
                ", wifiOnly=" + wifiOnly +
                ", cookieDto=" + cookieDto +
                '}';
    }
}
