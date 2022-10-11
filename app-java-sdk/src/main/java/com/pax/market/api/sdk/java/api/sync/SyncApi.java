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
import com.pax.market.api.sdk.java.api.sync.dto.AccessoryInfo;
import com.pax.market.api.sdk.java.api.sync.dto.TerminalSyncInfo;
import com.pax.market.api.sdk.java.base.api.BaseApi;
import com.pax.market.api.sdk.java.base.constant.Constants;
import com.pax.market.api.sdk.java.base.dto.LocationObject;
import com.pax.market.api.sdk.java.base.dto.MerchantObject;
import com.pax.market.api.sdk.java.base.dto.SdkObject;
import com.pax.market.api.sdk.java.base.request.SdkRequest;
import com.pax.market.api.sdk.java.base.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanjun on 2017/12/15.
 */
public class SyncApi extends BaseApi {


    /**
     * The constant downloadParamUrl.
     */
    protected static String syncTerminalInfoUrl = "/3rdApps/info";

    protected static String reBindUrl = "/3rdApps/rki/bind";

    protected static String locationUrl = "/3rdApps/location";
    protected static String merchantUrl = "/3rdApps/merchant";


    public SyncApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
        super(baseUrl, appKey, appSecret, terminalSN);
    }

    public void setBaseUrl(String baseUrl) {
        super.getDefaultClient().setBaseUrl(baseUrl);
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
     * Synchronize terminal information
     * @param infoList  the information list
     * @return Json result string
     */
    public SdkObject syncTerminalInfo(List<TerminalSyncInfo> infoList){
        SdkRequest request = new SdkRequest(syncTerminalInfoUrl);
        request.setRequestMethod(SdkRequest.RequestMethod.POST);
        request.addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        request.setRequestBody(new Gson().toJson(infoList, ArrayList.class));
        return JsonUtils.fromJson(call(request), SdkObject.class);
    }


    public SdkObject bindOrUpdateAccessory(AccessoryInfo accessoryInfo) {
        SdkRequest request = new SdkRequest(reBindUrl);
        request.setRequestMethod(SdkRequest.RequestMethod.POST);
        request.addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        request.setRequestBody(new Gson().toJson(accessoryInfo));
        return JsonUtils.fromJson(call(request), SdkObject.class);
    }

    public MerchantObject getMerchantInfo() {
        SdkRequest request = new SdkRequest(merchantUrl);
        request.setRequestMethod(SdkRequest.RequestMethod.GET);
        request.addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        return JsonUtils.fromJson(call(request), MerchantObject.class);
    }

    public LocationObject getLocationInfo() {
        SdkRequest request = new SdkRequest(locationUrl);
        request.setRequestMethod(SdkRequest.RequestMethod.GET);
        request.addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        return JsonUtils.fromJson(call(request), LocationObject.class);
    }
}

