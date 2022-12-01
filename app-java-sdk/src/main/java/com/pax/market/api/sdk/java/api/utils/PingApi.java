package com.pax.market.api.sdk.java.api.utils;

import com.pax.market.api.sdk.java.base.api.BaseApi;
import com.pax.market.api.sdk.java.base.constant.Constants;
import com.pax.market.api.sdk.java.base.dto.SdkObject;
import com.pax.market.api.sdk.java.base.request.SdkRequest;
import com.pax.market.api.sdk.java.base.util.JsonUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingApi extends BaseApi {

    private final Logger logger = LoggerFactory.getLogger(PingApi.class.getSimpleName());

    /**
     * The constant downloadParamUrl.
     */
    protected static String pingUrl = "v1/3rdApps/ping";

    public PingApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
        super(baseUrl, appKey, appSecret, terminalSN);
    }

    public void setBaseUrl(String baseUrl) {
        super.getDefaultClient().setBaseUrl(baseUrl);
    }

    /**
     * check if this app has update on PAXSTORE
     *
     * @return the update result
     */
    public SdkObject ping() {
        logger.info("Check ping >>> ");
        SdkRequest request = new SdkRequest(pingUrl);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        return JsonUtils.fromJson(call(request), SdkObject.class);
    }

}