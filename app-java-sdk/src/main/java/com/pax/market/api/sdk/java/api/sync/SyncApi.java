/*
 * ******************************************************************************
 * COPYRIGHT
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *
 *      Copyright (C) 2017 PAX Technology, Inc. All rights reserved.
 * ******************************************************************************
 */

package com.pax.market.api.sdk.java.api.sync;

import com.google.gson.Gson;
import com.pax.market.api.sdk.java.api.sync.dto.TerminalSyncBizData;
import com.pax.market.api.sdk.java.api.sync.dto.TerminalSyncInfo;
import com.pax.market.api.sdk.java.base.api.BaseApi;
import com.pax.market.api.sdk.java.base.client.DefaultClient;
import com.pax.market.api.sdk.java.base.constant.Constants;
import com.pax.market.api.sdk.java.base.dto.SdkObject;
import com.pax.market.api.sdk.java.base.request.SdkRequest;
import com.pax.market.api.sdk.java.base.util.JsonUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanjun on 2017/12/15.
 */
public class SyncApi extends BaseApi {

    private final Logger logger = LoggerFactory.getLogger(SyncApi.class.getSimpleName());

    /**
     * The constant downloadParamUrl.
     */
    protected static String syncTerminalInfoUrl = "/3rdApps/info";
    protected static String syncBusinessDataUrl = "/3rdApps/bizData";
    protected static int MAX_MB = 2;
    protected static int ERROR_CODE_PARAMETER_MANDATORY = 1000;
    protected static int ERROR_CODE_GETBYTES_FAILED = 1001;
    protected static int ERROR_CODE_EXCEED_MAX_SIZE = 1002;

    public SyncApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
        super(baseUrl, appKey, appSecret, terminalSN);
    }

    public interface SyncType {
        /**
         * Application Info
         */
        int APPLICATION = 1;

        /**
         * Device Info
         */
        int DEVICE = 2;

        /**
         * Hardware Info
         */
        int HARDWARE = 3;

        /**
         * Application Install History
         */
        int INSTALL_HISTORY = 4;
    }

    /**
     * 同步终端信息
     *
     * @return Json result string
     */
    public SdkObject syncTerminalInfo(List<TerminalSyncInfo> infoList){
        DefaultClient client = new DefaultClient(getBaseUrl(), getAppKey(), getAppSecret());
        SdkRequest request = new SdkRequest(syncTerminalInfoUrl);
        request.setRequestMethod(SdkRequest.RequestMethod.POST);
        request.addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        request.setRequestBody(new Gson().toJson(infoList, ArrayList.class));
        return JsonUtils.fromJson(client.execute(request), SdkObject.class);
    }

    /**
     * 同步终端业务数据 synchronize terminal business data
     *
     * @param bizDataList the list of business data to sync.
     * @return SdkObject
     * SdkObject.getBusinessCode: businessCode = 0, successful, others error.
     * SdkObject.getMessage: the response message
     */
    public SdkObject syncTerminalBizData(List bizDataList) {
        if (bizDataList == null || bizDataList.isEmpty()){
            SdkObject sdkObject = new SdkObject();
            sdkObject.setBusinessCode(ERROR_CODE_PARAMETER_MANDATORY);
            sdkObject.setMessage("Parameter bizDataList should not be null");
            return sdkObject;
        }

        DefaultClient client = new DefaultClient(getBaseUrl(), getAppKey(), getAppSecret());
        SdkRequest request = new SdkRequest(syncBusinessDataUrl);
        request.setRequestMethod(SdkRequest.RequestMethod.POST);
        request.addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        TerminalSyncBizData terminalSyncBizData = new TerminalSyncBizData();
        terminalSyncBizData.setBizDataList(bizDataList);
        String bizDataString = new Gson().toJson(terminalSyncBizData);
        int stringByteSize = 0;
        try {
            stringByteSize = bizDataString.getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            logger.error(e.toString());
            SdkObject sdkObject = new SdkObject();
            sdkObject.setBusinessCode(ERROR_CODE_GETBYTES_FAILED);
            sdkObject.setMessage("getBytes() error :" + e.toString());
            return sdkObject;
        }
        double stringMBSize = stringByteSize/1024.0d/1024d;
        if(stringMBSize > MAX_MB){
            SdkObject sdkObject = new SdkObject();
            sdkObject.setBusinessCode(ERROR_CODE_EXCEED_MAX_SIZE);
            sdkObject.setMessage("list exceeds the max size allow, max list size limits to 2MB");
            return sdkObject;
        }
        request.setRequestBody(bizDataString);
        return JsonUtils.fromJson(client.execute(request), SdkObject.class);
    }

}

