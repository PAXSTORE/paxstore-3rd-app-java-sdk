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
package com.pax.market.api.sdk.java.base.request;


import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;

/**
 * Created by fanjun on 2016/11/10.
 */
public class SdkRequest {

    /**
     * The Request method.
     */
    protected RequestMethod requestMethod = RequestMethod.GET;
    /**
     * The Request mapping url.
     */
    protected String requestMappingUrl;
    /**
     * The Header map.
     */
    protected Map<String, String> headerMap;
    /**
     * The Request params.
     */
    protected Map<String, String> requestParams;
    /**
     * The Timestamp.
     */
    protected Long timestamp;
    /**
     * The Request body.
     */
    protected String requestBody;
    /**
     * The Save file path.
     */
    protected String saveFilePath;                                  // 文件保存路径
    protected RequestBody multipartRequestBody;
    protected String encryptBody;

    /**
     * Instantiates a new Sdk request.
     */
    public SdkRequest() {
    }

    /**
     * Instantiates a new Sdk request.
     *
     * @param requestMappingUrl the request mapping url
     */
    public SdkRequest(String requestMappingUrl) {
        this.requestMappingUrl = requestMappingUrl;
    }

    /**
     * Instantiates a new Sdk request.
     *
     * @param requestMappingUrl the request mapping url
     * @param requestMethod     the request method
     */
    public SdkRequest(String requestMappingUrl, RequestMethod requestMethod) {
        this(requestMappingUrl);
        this.requestMethod = requestMethod;
    }

    /**
     * Gets request method.
     *
     * @return the request method
     */
    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    /**
     * Sets request method.
     *
     * @param requestMethod the request method
     */
    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    /**
     * Gets request mapping url.
     *
     * @return the request mapping url
     */
    public String getRequestMappingUrl() {
        return requestMappingUrl;
    }

    /**
     * Sets request mapping url.
     *
     * @param requestMappingUrl the request mapping url
     */
    public void setRequestMappingUrl(String requestMappingUrl) {
        this.requestMappingUrl = requestMappingUrl;
    }

    /**
     * Gets header map.
     *
     * @return the header map
     */
    public Map<String, String> getHeaderMap() {
        if (this.headerMap == null) {
            this.headerMap = new HashMap<String, String>();
        }
        return headerMap;
    }

    /**
     * Sets header map.
     *
     * @param headerMap the header map
     */
    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    /**
     * Add header.
     *
     * @param key   the key
     * @param value the value
     */
    public void addHeader(String key, String value) {
        getHeaderMap().put(key, value);
    }

    /**
     * Gets request params.
     *
     * @return the request params
     */
    public Map<String, String> getRequestParams() {
        if (this.requestParams == null) {
            this.requestParams = new HashMap<String, String>();
        }
        return requestParams;
    }

    /**
     * Sets request params.
     *
     * @param requestParams the request params
     */
    public void setRequestParams(Map<String, String> requestParams) {
        this.requestParams = requestParams;
    }

    /**
     * Add request param.
     *
     * @param key   the key
     * @param value the value
     */
    public void addRequestParam(String key, String value) {
        getRequestParams().put(key, value);
    }

    /**
     * Gets timestamp.
     *
     * @return the timestamp
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets timestamp.
     *
     * @param timestamp the timestamp
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets request body.
     *
     * @return the request body
     */
    public String getRequestBody() {
        return requestBody;
    }

    /**
     * Sets request body.
     *
     * @param requestBody the request body
     */
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    /**
     * The enum Request method.
     */
    public enum RequestMethod {
        /**
         * Get request method.
         */
        GET("GET"),
        /**
         * Post request method.
         */
        POST("POST"),
        MULTIPART_POST("MULTIPART_POST"),
        /**
         * Put request method.
         */
        PUT("PUT"),
        /**
         * Delete request method.
         */
        DELETE("DELETE");

        private String value;

        RequestMethod(String value) {
            this.value = value;
        }

        /**
         * Gets value.
         *
         * @return the value
         */
        public String getValue() {
            return value;
        }
    }

    /**
     * Gets save file path.
     *
     * @return the save file path
     */
    public String getSaveFilePath() {
        return saveFilePath;
    }

    /**
     * Sets save file path.
     *
     * @param saveFilePath the save file path
     */
    public void setSaveFilePath(String saveFilePath) {
        this.saveFilePath = saveFilePath;
    }

    public RequestBody getMultipartRequestBody() {
        return multipartRequestBody;
    }

    public void setMultipartRequestBody(RequestBody multipartRequestBody) {
        this.multipartRequestBody = multipartRequestBody;
    }

    public String getEncryptBody() {
        return encryptBody;
    }

    public void setEncryptBody(String encryptBody) {
        this.encryptBody = encryptBody;
    }
}
