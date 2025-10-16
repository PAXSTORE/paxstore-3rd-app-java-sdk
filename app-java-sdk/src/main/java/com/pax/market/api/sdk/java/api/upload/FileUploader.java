package com.pax.market.api.sdk.java.api.upload;

import com.pax.market.api.sdk.java.api.upload.dto.ApkLogDto;
import com.pax.market.api.sdk.java.base.api.BaseApi;
import com.pax.market.api.sdk.java.base.constant.Constants;
import com.pax.market.api.sdk.java.base.constant.ResultCode;
import com.pax.market.api.sdk.java.base.dto.SdkObject;
import com.pax.market.api.sdk.java.base.request.SdkRequest;
import com.pax.market.api.sdk.java.base.util.JsonUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FileUploader extends BaseApi {
    protected static final String uploadFileUrl = "v1/3rdApps/apk-logs";
    protected static final long UPLOAD_FILE_LIMIT_SIZE = 100 * 1024 * 1024;
    protected static final String ERROR_UPLOAD_FILE_TOO_LARGE = "The uploaded file is too large, the size limit is 100MB";
    public FileUploader(String baseUrl, String appKey, String appSecret, String terminalSN) {
        super(baseUrl, appKey, appSecret, terminalSN);
    }

    public void setBaseUrl(String baseUrl) {
        super.getDefaultClient().setBaseUrl(baseUrl);
    }

    public SdkObject uploadLogFile(File file, ApkLogDto apkLogDto) {
        SdkRequest request = new SdkRequest(uploadFileUrl);
        if (file.length() > UPLOAD_FILE_LIMIT_SIZE) {
            SdkObject sdkObject = new SdkObject();
            sdkObject.setBusinessCode(ResultCode.UPLOAD_FILE_TOO_LARGE.getCode());
            sdkObject.setMessage(ERROR_UPLOAD_FILE_TOO_LARGE);
            System.out.println("uploadLogFile ERROR_UPLOAD_FILE_TOO_LARGE");
            return sdkObject;
        }
        request.setRequestMethod(SdkRequest.RequestMethod.MULTIPART_POST);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("logFile", file.getName(),
                        RequestBody.create(file, MediaType.parse("multipart/form-data")))
                .addFormDataPart("terminalApkLog", null,
                        RequestBody.create(
                                JsonUtils.toJson(apkLogDto),
                                MediaType.parse("application/json")))
                .build();
        request.setEncryptBody(JsonUtils.toJson(apkLogDto));
        request.setMultipartRequestBody(requestBody);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        return JsonUtils.fromJson(call(request), SdkObject.class);
    }
}
