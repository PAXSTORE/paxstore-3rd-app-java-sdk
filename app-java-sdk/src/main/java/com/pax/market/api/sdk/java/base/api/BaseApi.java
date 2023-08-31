/*
 * *******************************************************************************
 * COPYRIGHT
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *
 *      Copyright (C) 2017 PAX Technology, Inc. All rights reserved.
 * *******************************************************************************
 */

package com.pax.market.api.sdk.java.base.api;

import com.pax.market.api.sdk.java.base.client.DefaultClient;
import com.pax.market.api.sdk.java.base.client.ProxyDelegate;
import com.pax.market.api.sdk.java.base.request.SdkRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fanjun on 2016/12/5.
 */
public class BaseApi {
    private final Logger logger = LoggerFactory.getLogger(BaseApi.class.getSimpleName());

    /**
     * The constant terminal SN.
     */
    private String terminalSN;
    private String appKey;
    private DefaultClient client;
    private String appSecret;

    public BaseApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
        this.terminalSN = terminalSN;
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.client = new DefaultClient(baseUrl, appKey, appSecret);
    }

    public String getBaseUrl() {
        return this.client.getBaseUrl();
    }

    public String getTerminalSN() {
        return terminalSN;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public <T extends BaseApi> T setProxyDelegate(ProxyDelegate proxyDelegate) {
        if(proxyDelegate != null){
            this.client.setProxy(proxyDelegate.retrieveProxy());
            this.client.setBasicAuthorization(proxyDelegate.retrieveBasicAuthorization());
            this.client.setPasswordAuthentication(proxyDelegate.retrievePasswordAuthentication());
        } else {
            logger.warn("Proxy delegate is NULL, please set it before using proxy!");
        }
        return (T) this;
    }

    protected DefaultClient getDefaultClient(){
        return client;
    }

    protected DefaultClient getDefaultDownloadClient(){
        return this.client.newBuilder().baseUrl("").build();
    }

    public String call(SdkRequest request){
        return getDefaultClient().execute(request);
    }

    public String pingHosts(String dynamicHost, String staticHost) {
        return getDefaultClient().pingHosts(dynamicHost, staticHost);
    }

    public String download(SdkRequest request){
        return getDefaultDownloadClient().execute(request);
    }
}
