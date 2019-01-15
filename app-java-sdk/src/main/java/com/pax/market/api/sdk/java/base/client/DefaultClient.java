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
package com.pax.market.api.sdk.java.base.client;


import com.pax.market.api.sdk.java.Version;
import com.pax.market.api.sdk.java.base.constant.Constants;
import com.pax.market.api.sdk.java.base.constant.ResultCode;
import com.pax.market.api.sdk.java.base.request.SdkRequest;
import com.pax.market.api.sdk.java.base.util.CryptoUtils;
import com.pax.market.api.sdk.java.base.util.HttpUtils;
import com.pax.market.api.sdk.java.base.util.JsonUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Proxy;
import java.security.GeneralSecurityException;

/**
 * 客户端
 */
public class DefaultClient {

	private static final Logger logger = LoggerFactory.getLogger(DefaultClient.class.getSimpleName());

    /**
     * The Base url.
     */
    protected String baseUrl;
    /**
     * The App key.
     */
    protected String appKey;
    /**
     * The App secret.
     */
    protected String appSecret;
    /**
     * The Sign method.
     */
    protected String signMethod = Constants.SIGN_METHOD_HMAC;
    /**
     * The Connect timeout.
     */
    protected int connectTimeout = 30000; 			// 默认连接超时时间为30秒
    /**
     * The Read timeout.
     */
    protected int readTimeout = 30000; 				// 默认响应超时时间为30秒
	/**
	 * The proxy setting
	 */
	private Proxy proxy;
	/**
	 * The proxy authorization
	 */
	private String proxyAuthorization;

    /**
     * Instantiates a new Default client.
     *
     * @param baseUrl   the base url
     * @param appKey    the app key
     * @param appSecret the app secret
     */
    public DefaultClient(String baseUrl, String appKey, String appSecret) {
		this.appKey = appKey;
		this.appSecret = appSecret;
		this.baseUrl = baseUrl;
	}

	public DefaultClient(DefaultClient.Builder builder){
		this.baseUrl = builder.baseUrl;
		this.appKey = builder.appKey;
		this.appSecret = builder.appSecret;
		this.signMethod = builder.signMethod;
		this.connectTimeout = builder.connectTimeout;
		this.readTimeout = builder.readTimeout;
		this.proxy = builder.proxy;
		this.proxyAuthorization = builder.proxyAuthorization;
	}

    /**
     * Execute string.
     *
     * @param request the request
     * @return the string
     */
    public String execute(SdkRequest request) {
		try {
			return _execute(request);
		} catch (IOException e) {
			logger.error("IOException occurred when execute request. Details: {}", e.toString());
			return JsonUtils.getSdkJson(ResultCode.SDK_RQUEST_EXCEPTION.getCode(), e.getMessage());
		} catch (GeneralSecurityException e) {
			logger.error("GeneralSecurityException occurred when execute request. Details: {}", e.toString());
			return JsonUtils.getSdkJson(ResultCode.SDK_RQUEST_EXCEPTION.getCode(), e.getMessage());
		}
	}

	private String _execute(SdkRequest request) throws IOException, GeneralSecurityException {
		String response;

		if(appKey != null) {
			request.addHeader(Constants.REQ_HEADER_APP_KEY, appKey);
		}
		request.addHeader(Constants.REQ_HEADER_SDK_VERSION, Version.getVersion());
		if(proxy != null && proxy.type() != Proxy.Type.DIRECT && proxyAuthorization != null){
			request.addHeader(Constants.REQ_HEADER_PROXY_AUTHORIZATION, proxyAuthorization);
		}

//		Long timestamp = request.getTimestamp();
//		if(timestamp == null){
//			timestamp = System.currentTimeMillis();
//		}
//		request.addRequestParam(Constants.TIMESTAMP, Long.toString(timestamp));

		String query = HttpUtils.buildQuery(request.getRequestParams(), Constants.CHARSET_UTF8);
		if(appSecret != null) {
			String signature = CryptoUtils.signRequest(query, request.getRequestBody(), appSecret, signMethod);
			request.addHeader(Constants.SIGNATURE, signature);
		}
		String requestUrl = HttpUtils.buildRequestUrl(baseUrl + request.getRequestMappingUrl(), query);
		logger.info(" --> {} {}", request.getRequestMethod().getValue(), requestUrl);

		if(!request.isCompressData()){
			response = HttpUtils.request(requestUrl, request.getRequestMethod().getValue(), connectTimeout, readTimeout, request.getRequestBody(), request.getHeaderMap(), request.getSaveFilePath(), proxy);
		} else {
			response = HttpUtils.compressRequest(requestUrl, request.getRequestMethod().getValue(), connectTimeout, readTimeout, request.getRequestBody(), request.getHeaderMap(), request.getSaveFilePath(), proxy);
		}
		return response;
	}

    /**
     * 设置API请求的连接超时时间
     *
     * @param connectTimeout the connect timeout
     */
    public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

    /**
     * 设置API请求的读超时时间
     *
     * @param readTimeout the read timeout
     */
    public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	public void setProxyAuthorization(String proxyAuthorization) {
		this.proxyAuthorization = proxyAuthorization;
	}

	public DefaultClient.Builder newBuilder() {
		return new DefaultClient.Builder(this);
	}

	public static final class Builder{
		String baseUrl;
		String appKey;
		String appSecret;
		String signMethod;
		int connectTimeout;
		int readTimeout;
		Proxy proxy;
		String proxyAuthorization;

		public Builder(){
			this.signMethod =  Constants.SIGN_METHOD_HMAC;
			this.connectTimeout = 3000;
			this.readTimeout = 3000;
		}

		Builder(DefaultClient client) {
			this.baseUrl = client.baseUrl;
			this.appKey = client.appKey;
			this.appSecret = client.appSecret;
			this.signMethod = client.signMethod;
			this.connectTimeout = client.connectTimeout;
			this.readTimeout = client.readTimeout;
			this.proxy = client.proxy;
			this.proxyAuthorization = client.proxyAuthorization;
		}

		public DefaultClient.Builder baseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
			return this;
		}

		public DefaultClient.Builder appKey(String appKey) {
			this.appKey = appKey;
			return this;
		}

		public DefaultClient.Builder appSecret(String appSecret) {
			this.appSecret = appSecret;
			return this;
		}

		public DefaultClient.Builder signMethod(String signMethod) {
			this.signMethod = signMethod;
			return this;
		}

		public DefaultClient.Builder connectTimeout(int timeout) {
			this.connectTimeout = timeout;
			return this;
		}

		public DefaultClient.Builder readTimeout(int timeout) {
			this.readTimeout = timeout;
			return this;
		}

		public DefaultClient.Builder proxy(Proxy proxy) {
			this.proxy = proxy;
			return this;
		}

		public DefaultClient.Builder proxyAuthorization(String proxyAuthorization) {
			this.proxyAuthorization = proxyAuthorization;
			return this;
		}

		public DefaultClient build() {
			return new DefaultClient(this);
		}
	}

}
