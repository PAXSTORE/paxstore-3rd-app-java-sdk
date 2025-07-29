package com.pax.market.api.sdk.java.base.dto;

import java.io.Serializable;

public class DownloadConfig implements Serializable {


    private boolean verifySha256;
    private boolean applyResultNeeded;
    private boolean separateFolder;


    private DownloadConfig() {
    }

    protected DownloadConfig(Builder builder) {
        this.verifySha256 = builder.verifySha256;
        this.applyResultNeeded = builder.applyResultNeeded;
        this.separateFolder = builder.separateFolder;
    }


    public boolean isVerifySha256() {
        return verifySha256;
    }

    public boolean isApplyResultNeeded() {
        return applyResultNeeded;
    }

    public boolean isSeparateFolder() {
        return separateFolder;
    }

    public static class Builder{
        boolean verifySha256 = false; // default false
        boolean applyResultNeeded = false; //default false
        boolean separateFolder = true; //default true

        /**
         * default false
         * when it is set true, sha256 will be verified after param is downloaded
         * @param verifySha256
         * @return
         */
        public Builder enableVerifySha256(boolean verifySha256) {
            this.verifySha256 = verifySha256;
            return this;
        }

        /**
         * default false
         * when it is set true, app will need to update task final result.
         * It can update whether the parameters are applied successfully
         * @param needApplyResult
         * @return
         */
        public Builder enableNeedApplyResult(boolean needApplyResult) {
            this.applyResultNeeded = needApplyResult;
            return this;
        }

        /**
         * default true
         * this should be set true, when your application supports parameter partial download
         * @param separateFolder
         * @return
         */
        public Builder enableSeparateFolder(boolean separateFolder) {
            this.separateFolder = separateFolder;
            return this;
        }

        public DownloadConfig build(){
            return new DownloadConfig(this);
        }




    }

}
