package com.pax.market.api.sdk.java.api.terminal;

import com.pax.market.api.sdk.java.api.terminal.dto.TerminalInfo;
import com.pax.market.api.sdk.java.base.api.BaseApi;
import com.pax.market.api.sdk.java.base.client.DefaultClient;
import com.pax.market.api.sdk.java.base.constant.Constants;
import com.pax.market.api.sdk.java.base.request.SdkRequest;
import com.pax.market.api.sdk.java.base.util.JsonUtils;


public class TerminalApi extends BaseApi {


    protected static String getCurrentTerminalInfoUrl = "/3rdApps/currentTerminal";

    public TerminalApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
        super(baseUrl, appKey, appSecret, terminalSN);
    }

    public TerminalInfo getCurrentTerminalInfo(){
        DefaultClient client = new DefaultClient(getBaseUrl(), getAppKey(), getAppSecret());
        SdkRequest request = new SdkRequest(getCurrentTerminalInfoUrl);
        request.setRequestMethod(SdkRequest.RequestMethod.GET);
        request.addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        return JsonUtils.fromJson(client.execute(request), TerminalInfo.class);
    }

}

