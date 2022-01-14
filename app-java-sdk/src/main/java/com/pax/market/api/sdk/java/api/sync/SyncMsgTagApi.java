package com.pax.market.api.sdk.java.api.sync;

import com.pax.market.api.sdk.java.api.sync.dto.AppMsgTagSyncRequest;
import com.pax.market.api.sdk.java.base.api.BaseApi;
import com.pax.market.api.sdk.java.base.constant.Constants;
import com.pax.market.api.sdk.java.base.dto.MsgTagObject;
import com.pax.market.api.sdk.java.base.dto.SdkObject;
import com.pax.market.api.sdk.java.base.request.SdkRequest;
import com.pax.market.api.sdk.java.base.util.JsonUtils;

import java.util.List;

/**
 * Created by john on 2021/10/25.
 */
public class SyncMsgTagApi extends BaseApi {
    protected static String syncMsgTagUrl = "/3rdApps/tag";
    protected static int ERROR_CODE_TAB_EMPTY = 1000;

    public SyncMsgTagApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
        super(baseUrl, appKey, appSecret, terminalSN);
    }

    public void setBaseUrl(String baseUrl) {
        super.getDefaultClient().setBaseUrl(baseUrl);
    }

    /**
     *  Sync app msg tab
     * @param attachTagNames The msg tabs to create
     * @param detachTagNames The msg tabs to delete
     * @return the result
     */
    public SdkObject syncMsgTag(List<String> attachTagNames, List<String> detachTagNames) {
        if ((attachTagNames == null && detachTagNames == null) || (attachTagNames != null && attachTagNames.isEmpty() && detachTagNames != null && detachTagNames.isEmpty())) {
            SdkObject sdkObject = new SdkObject();
            sdkObject.setBusinessCode(ERROR_CODE_TAB_EMPTY);
            sdkObject.setMessage("attachTagNames and detachTagNames cannot both be null");
            return sdkObject;
        }
        AppMsgTagSyncRequest appMsgTabSyncRequest = new AppMsgTagSyncRequest();
        appMsgTabSyncRequest.setSerialNo(getTerminalSN());
        if (attachTagNames != null && !attachTagNames.isEmpty()) {
            appMsgTabSyncRequest.setAttachTagNames(attachTagNames);
        }
        if (detachTagNames != null && !detachTagNames.isEmpty()) {
            appMsgTabSyncRequest.setDetachTagNames(detachTagNames);
        }

        SdkRequest request = new SdkRequest(syncMsgTagUrl);
        request.setRequestMethod(SdkRequest.RequestMethod.POST);
        request.addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        String requestBody = JsonUtils.toJson(appMsgTabSyncRequest);
        request.setRequestBody(requestBody);
        return JsonUtils.fromJson(call(request), SdkObject.class);
    }

    public MsgTagObject getAllTag() {
        SdkRequest request = new SdkRequest(syncMsgTagUrl);
        request.addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        return JsonUtils.fromJson(call(request), MsgTagObject.class);
    }


    public SdkObject attachMsgTag(List<String> tagNames) {
        return syncMsgTag(tagNames, null);
    }

    public SdkObject detachMsgTag(List<String> tagNames) {
        return syncMsgTag(null, tagNames);
    }
}
