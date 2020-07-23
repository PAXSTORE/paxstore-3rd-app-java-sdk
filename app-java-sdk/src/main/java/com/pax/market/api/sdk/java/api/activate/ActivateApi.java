package com.pax.market.api.sdk.java.api.activate;

import com.pax.market.api.sdk.java.base.api.BaseApi;
import com.pax.market.api.sdk.java.base.constant.Constants;
import com.pax.market.api.sdk.java.base.dto.SdkObject;
import com.pax.market.api.sdk.java.base.request.SdkRequest;
import com.pax.market.api.sdk.java.base.util.JsonUtils;

public class ActivateApi extends BaseApi {

    public ActivateApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
        super(baseUrl, appKey, appSecret, terminalSN);
    }

    private static final String REQ_TID = "X-Terminal-TID";

    /**
     * The constant downloadParamUrl.
     */
    protected static String checkUpdateUrl = "/3rdApps/active/terminal";


    /**
     *  Activate terminal with TID
     * @param tid
     * @return
     */
    public SdkObject initByTID(String tid) {
        SdkRequest request = new SdkRequest(checkUpdateUrl);
        request.setRequestMethod(SdkRequest.RequestMethod.PUT);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        request.addHeader(REQ_TID, tid);
        return JsonUtils.fromJson(call(request), SdkObject.class);
    }
}
