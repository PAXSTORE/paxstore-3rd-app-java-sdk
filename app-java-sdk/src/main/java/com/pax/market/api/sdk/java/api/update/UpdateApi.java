package com.pax.market.api.sdk.java.api.update;

import com.pax.market.api.sdk.java.base.api.BaseApi;
import com.pax.market.api.sdk.java.base.constant.Constants;
import com.pax.market.api.sdk.java.base.dto.UpdateObject;
import com.pax.market.api.sdk.java.base.request.SdkRequest;
import com.pax.market.api.sdk.java.base.util.JsonUtils;

/**
 * Created by zcy on 2019/4/2 0002.
 */

public class UpdateApi extends BaseApi {

    private static final String REQ_PARAM_PACKAGE_NAME = "packageName";
    private static final String REQ_PARAM_VERSION_CODE = "versionCode";

    /**
     * The constant downloadParamUrl.
     */
    protected static String checkUpdateUrl = "/3rdApps/upgrade";

    public UpdateApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
        super(baseUrl, appKey, appSecret, terminalSN);
    }

    /**
     * check if this app has update on PAXSTORE
     *
     * @param versionCode versionCode of this app
     * @param packageName packageName of this app
     * @return
     */
    public UpdateObject checkUpdate(int versionCode, String packageName) {
        SdkRequest request = new SdkRequest(checkUpdateUrl);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        request.addRequestParam(REQ_PARAM_PACKAGE_NAME, packageName);
        request.addRequestParam(REQ_PARAM_VERSION_CODE, Integer.toString(versionCode));
        return JsonUtils.fromJson(call(request), UpdateObject.class);
    }

}
