package com.pax.market.api.sdk.java.api.sync;

import com.pax.market.api.sdk.java.api.sync.dto.AppMsgTabSyncRequest;
import com.pax.market.api.sdk.java.base.api.BaseApi;
import com.pax.market.api.sdk.java.base.constant.Constants;
import com.pax.market.api.sdk.java.base.dto.SdkObject;
import com.pax.market.api.sdk.java.base.request.SdkRequest;
import com.pax.market.api.sdk.java.base.util.JsonUtils;

import java.util.List;

/**
 * Created by john on 2021/10/25.
 */
public class SyncMsgTabApi extends BaseApi {
    protected static String syncMsgTabUrl = "/3rdApps/tab";
    protected static int ERROR_CODE_TAB_EMPTY = 1000;

    public SyncMsgTabApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
        super(baseUrl, appKey, appSecret, terminalSN);
    }

    public void setBaseUrl(String baseUrl) {
        super.getDefaultClient().setBaseUrl(baseUrl);
    }

    /**
     *  Sync app msg tab
     * @param tabNames The msg tabs to create
     * @param deleteTabNames The msg tabs to delete
     * @return the result
     */
    public SdkObject syncMsgTab(List<String> tabNames, List<String> deleteTabNames) {
        if ((tabNames == null && deleteTabNames == null) || (tabNames != null && tabNames.isEmpty() && deleteTabNames != null && deleteTabNames.isEmpty())) {
            SdkObject sdkObject = new SdkObject();
            sdkObject.setBusinessCode(ERROR_CODE_TAB_EMPTY);
            sdkObject.setMessage("TabNames and deleteTabNames cannot both be null");
            return sdkObject;
        }
        AppMsgTabSyncRequest appMsgTabSyncRequest = new AppMsgTabSyncRequest();
        appMsgTabSyncRequest.setSerialNo(getTerminalSN());
        if (tabNames != null && !tabNames.isEmpty()) {
            appMsgTabSyncRequest.setTabNames(tabNames);
        }
        if (deleteTabNames != null && !deleteTabNames.isEmpty()) {
            appMsgTabSyncRequest.setDeleteTabNames(deleteTabNames);
        }

        SdkRequest request = new SdkRequest(syncMsgTabUrl);
        request.setRequestMethod(SdkRequest.RequestMethod.POST);
        request.addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        String requestBody = JsonUtils.toJson(appMsgTabSyncRequest);
        request.setRequestBody(requestBody);
        return JsonUtils.fromJson(call(request), SdkObject.class);
    }

    public SdkObject createMsgTab(List<String> tabNames) {
        return syncMsgTab(tabNames, null);
    }

    public SdkObject deleteMsgTab(List<String> deleteTabNames) {
        return syncMsgTab(null, deleteTabNames);
    }
}
