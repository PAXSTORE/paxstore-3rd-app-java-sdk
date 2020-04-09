package com.pax.market.api.sdk.java.base.dto;

import com.google.gson.annotations.SerializedName;

public class RowObject {
    @SerializedName("colName")
    String colName;
    @SerializedName("value")
    Object value;

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Row{" +
                "colName='" + colName + '\'' +
                ", value=" + value +
                '}';
    }
}