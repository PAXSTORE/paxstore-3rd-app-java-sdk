package com.pax.market.api.sdk.java.api.sync.dto;

/**
 * Created by fojut on 2018/9/29.
 */
public class TerminalPurchaseInfo {
    private String terminalPurchaseRecord;

    public TerminalPurchaseInfo() {
    }

    public TerminalPurchaseInfo(String terminalPurchaseRecord) {
        this.terminalPurchaseRecord = terminalPurchaseRecord;
    }

    public String getTerminalPurchaseRecord() {
        return terminalPurchaseRecord;
    }

    public void setTerminalPurchaseRecord(String terminalPurchaseRecord) {
        this.terminalPurchaseRecord = terminalPurchaseRecord;
    }
}
