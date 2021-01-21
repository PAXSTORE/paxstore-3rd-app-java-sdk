package com.pax.market.api.sdk.java.api.activate;

import com.pax.market.api.sdk.java.base.api.BaseApi;
import com.pax.market.api.sdk.java.base.constant.Constants;
import com.pax.market.api.sdk.java.base.dto.ActivateObject;
import com.pax.market.api.sdk.java.base.dto.SdkObject;
import com.pax.market.api.sdk.java.base.request.SdkRequest;
import com.pax.market.api.sdk.java.base.util.JsonUtils;

public class ActivateApi extends BaseApi {
    String model;

    public ActivateApi(String baseUrl, String appKey, String appSecret, String terminalSN, String model) {
        super(baseUrl, appKey, appSecret, terminalSN);
        this.model = model;
    }

    private static final String REQ_TID = "X-Terminal-TID";

    /**
     * The constant downloadParamUrl.
     */
    protected static String checkUpdateUrl = "/3rdApps/init";

    public void setBaseUrl(String baseUrl) {
        super.getDefaultClient().setBaseUrl(baseUrl);
    }

    /**
     *  Activate terminal with TID
     * @param tid
     * @return
     */
    public SdkObject initByTID(String tid) {
        SdkRequest request = new SdkRequest(checkUpdateUrl);
        ActivateObject activateObject = new ActivateObject();
        activateObject.setTid(tid);
        String requestBody = JsonUtils.toJson(activateObject);
        request.setRequestMethod(SdkRequest.RequestMethod.PUT);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        request.addHeader(Constants.REQ_HEADER_MODEL, model);
        request.setRequestBody(requestBody);
        return JsonUtils.fromJson(call(request), SdkObject.class);
    }
}
