package com.pax.market.api.sdk.java.api.terminal.dto;

import com.pax.market.api.sdk.java.base.dto.SdkObject;

public class TerminalInfo extends SdkObject {
    private String TID;
    private String name;
    private String serialNo;
    private String modelName;
    private String factoryName;
    private String merchantName;
    private String status;

    public String getTID() {
        return TID;
    }

    public void setTID(String TID) {
        this.TID = TID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TerminalInfo{" +
                "TID='" + TID + '\'' +
                ", name='" + name + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", modelName='" + modelName + '\'' +
                ", factoryName='" + factoryName + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
