package com.pax.market.api.sdk.java.base.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class DataQueryResultObject extends SdkObject {

    @SerializedName("worksheetName")
    private String worksheetName;
    @SerializedName("columns")
    private List<Column> columns;
    @SerializedName("rows")
    private List<List<RowObject>> rows;
    @SerializedName("hasNext")
    private boolean hasNext;
    @SerializedName("offset")
    private long offset;
    @SerializedName("limit")
    private int limit;

    public static class Column {
        @SerializedName("colName")
        String colName;
        @SerializedName("displayName")
        String displayName;

        public String getColName() {
            return colName;
        }

        public void setColName(String colName) {
            this.colName = colName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return "Column{" +
                    "colName='" + colName + '\'' +
                    ", displayName='" + displayName + '\'' +
                    '}';
        }
    }

    public String getWorksheetName() {
        return worksheetName;
    }

    public void setWorksheetName(String worksheetName) {
        this.worksheetName = worksheetName;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public List<List<RowObject>> getRows() {
        return rows;
    }

    public void setRows(List<List<RowObject>> rows) {
        this.rows = rows;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "DataQueryResultDTO{" +
                "worksheetName='" + worksheetName + '\'' +
                ", columns=" + columns +
                ", rows=" + rows +
                ", hasNext=" + hasNext +
                ", offset=" + offset +
                ", limit=" + limit +
                '}';
    }
}