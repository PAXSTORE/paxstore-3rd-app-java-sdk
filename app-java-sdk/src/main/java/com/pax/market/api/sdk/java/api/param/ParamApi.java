/*
 * *******************************************************************************
 * COPYRIGHT
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *
 *      Copyright (C) 2017 PAX Technology, Inc. All rights reserved.
 * *******************************************************************************
 */
package com.pax.market.api.sdk.java.api.param;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.pax.market.api.sdk.java.base.api.BaseApi;
import com.pax.market.api.sdk.java.base.constant.Constants;
import com.pax.market.api.sdk.java.base.constant.ResultCode;
import com.pax.market.api.sdk.java.base.dto.DownloadResultObject;
import com.pax.market.api.sdk.java.base.dto.InnerDownloadResultObject;
import com.pax.market.api.sdk.java.base.dto.LastFailObject;
import com.pax.market.api.sdk.java.base.dto.ParamListObject;
import com.pax.market.api.sdk.java.base.dto.ParamObject;
import com.pax.market.api.sdk.java.base.dto.SdkObject;
import com.pax.market.api.sdk.java.base.dto.UpdateActionObject;
import com.pax.market.api.sdk.java.base.exception.ParseXMLException;
import com.pax.market.api.sdk.java.base.request.SdkRequest;
import com.pax.market.api.sdk.java.base.util.FileUtils;
import com.pax.market.api.sdk.java.base.util.HMACSignatureGenerator;
import com.pax.market.api.sdk.java.base.util.JsonUtils;
import com.pax.market.api.sdk.java.base.util.MUtils;
import com.pax.market.api.sdk.java.base.util.ReplaceUtils;
import com.pax.market.api.sdk.java.base.util.SHA256Utils;
import com.pax.market.api.sdk.java.base.util.ZipUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource; // 引入org.xml.sax.InputSource

import static com.pax.market.api.sdk.java.base.util.HttpUtils.IOEXCTION_FLAG;


/**
 * Created by fanjun on 2016/12/5.
 */
public class ParamApi extends BaseApi {
    public static final String REMARKS_PARAM_DOWNLOADING = "15206";         //Param is downloading
    /**
     * The constant ACT_STATUS_PENDING.
     */
    public static final int ACT_STATUS_PENDING = 1;
    /**
     * The constant ACT_STATUS_SUCCESS.
     */
    public static final int ACT_STATUS_SUCCESS = 2;
    /**
     * The constant ACT_STATUS_FAILED.
     */
    public static final int ACT_STATUS_FAILED = 3;
    /**
     * The constant CODE_NONE_ERROR.
     */
    public static final int CODE_NONE_ERROR = 0;
    /**
     * The constant CODE_DOWNLOAD_ERROR.
     */
    public static final int CODE_DOWNLOAD_ERROR = 1;

    public static final int ERROR_CODE_PARAM_APPLY_FAILED = 48;
    public static final String REMARKS_CODE_PARAM_DOWNLOADED = "15223";
    public static final String REMARKS_CODE_PARAM_APPLIED = "15224";


    /**
     * The constant RETRY_COUNT.
     */
    public static final int RETRY_COUNT = 20;


    /**
     * The constant RETRY_TIME_LIMIT.
     */
    public static final long RETRY_TIME_LIMIT = 10 * 24 * 3600_000L;

    /**
     * Get last success param limit
     */
    public static final long GET_SUCCESS_PARAM_LIMIT = 60_000L;
    public static final String ERROR_CELLULAR_NOT_ALLOWED = "Cellular download not allowed";
    private static final String REQ_PARAM_PACKAGE_NAME = "packageName";
    private static final String REQ_PARAM_VERSION_CODE = "versionCode";
    private static final String REQ_PARAM_STATUS = "status";
    private static final String REQ_PARAM_ERROR_CODE = "errorCode";
    private static final String REQ_PARAM_REMARKS = "remarks";
    private static final String REQ_PARAM_TEMPLATE_NAME = "paramTemplateName";
    private static final String ERROR_REMARKS_REPLACE_VARIABLES = "Replace paramVariables failed";
    private static final String ERROR_REMARKS_NOT_GOOD_JSON = "Bad json : ";
    private static final String ERROR_REMARKS_VARIFY_MD_FAILED = "MD5 Validation Error";
    private static final String ERROR_REMARKS_VARIFY_SHA256_OR_SIG_NOT_FOUND = "SHA256 or signature not found";
    private static final String ERROR_REMARKS_VARIFY_SHA256_FAILED = "SHA256 Validation Error";
    private static final String ERROR_REMARKS_VARIFY_SHA256_SIGNATURE_FAILED = "SHA256 signature Validation Error";
    private static final String ERROR_UNZIP_FAILED = "Unzip file failed";
    private static final String DOWNLOAD_SUCCESS = "Success";
    private static final String FILE_DOWNLOAD_SUCCESS = "Params downloaded";
    private static final String SAVEPATH_CANNOT_BE_NULL = "Save path can not be empty";

    /**
     * The constant downloadParamUrl.
     */
    protected static String downloadParamUrl = "v1/3rdApps/param";
    /**
     * The constant updateStatusUrl.
     */
    protected static String updateStatusUrl = "v1/3rdApps/actions/{actionId}/status";
    /**
     * The constant updateStatusBatchUrl.
     */
    protected static String updateStatusBatchUrl = "v1/3rdApps/actions";
    /**
     * Get last success param url
     */
    protected static String lastSuccessParamUrl = "v1/3rdApps/param/last/success";

    private final Logger logger = LoggerFactory.getLogger(ParamApi.class);

    private long lastGetTime = -1;

    public ParamApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
        super(baseUrl, appKey, appSecret, terminalSN);
    }

    public String calculateSHA256(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(file);

                return  SHA256Utils.sha256Hex(inputStream);
            } catch (IOException e) {
                logger.error("calculateSHA256 error:" + e);
            }
        }
        return null;
    }

    public void setBaseUrl(String baseUrl) {
        super.getDefaultClient().setBaseUrl(baseUrl);
    }

    /**
     * Get terminal params to download
     *
     * @param packageName the packageName
     * @param versionCode the versionCode
     * @return the paramList
     */
    public ParamListObject getParamDownloadList(String packageName, int versionCode) {
        SdkRequest request = new SdkRequest(downloadParamUrl);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        request.addRequestParam(REQ_PARAM_PACKAGE_NAME, packageName);
        request.addRequestParam(REQ_PARAM_VERSION_CODE, Integer.toString(versionCode));
        return JsonUtils.fromJson(call(request), ParamListObject.class);
    }

    /**
     * Get terminal last success param
     * @param paramTemplateName the template need to get
     * @return the param objet
     */
    public ParamObject getLastSuccessParam(String paramTemplateName) {
        logger.debug(lastGetTime + "");

        if (lastGetTime != -1 && System.currentTimeMillis() - lastGetTime < GET_SUCCESS_PARAM_LIMIT) {
            return createRateLimit();
        } else {
            lastGetTime = System.currentTimeMillis();
        }

        SdkRequest request = new SdkRequest(lastSuccessParamUrl);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        if (paramTemplateName != null) {
            request.addRequestParam(REQ_PARAM_TEMPLATE_NAME, paramTemplateName);
        }
        return JsonUtils.fromJson(call(request), ParamObject.class);
    }

    public ParamObject createRateLimit() {
        ParamObject paramObject = new ParamObject();
        paramObject.setBusinessCode(-1);
        paramObject.setMessage("Try again after one minute too frequently");
        return paramObject;
    }

    public ParamObject getLastSuccessParam() {
        return getLastSuccessParam(null);
    }

    private String cookieHeader(String signature, String expires, String keyPairId) {
        StringBuilder cookieHeader = new StringBuilder();
        if (expires == null && keyPairId == null) {
            cookieHeader.append("CloudFront-Signature").append('=').append(signature);
        } else {
            cookieHeader.append("CloudFront-Signature").append('=').append(signature);
            cookieHeader.append("; ");
            cookieHeader.append("CloudFront-Expires").append('=').append(expires);
            cookieHeader.append("; ");
            cookieHeader.append("CloudFront-Key-Pair-Id").append('=').append(keyPairId);
        }
        return cookieHeader.toString();
    }

    /**
     * Download param files
     *
     * @param paramObject  You can get ParamObject from getParamDownloadList();
     * @param saveFilePath Path that param files will be saved.
     * @param needVerifySha need verify sha256 or not
     * @return the download result
     */
    public DownloadResultObject downloadParamFileOnly(ParamObject paramObject, String saveFilePath, boolean needVerifySha) {
        DownloadResultObject resultObject = new DownloadResultObject();
        resultObject.setParamSavePath(saveFilePath);

        // 0. prepare request
        SdkRequest request = new SdkRequest(paramObject.getDownloadUrl());
        request.setSaveFilePath(saveFilePath);
        if (paramObject.getCookieSignature() != null) { // there exist cookies
            String cookies = cookieHeader(paramObject.getCookieSignature(), paramObject.getCookieExpires(), paramObject.getCookieKeyPairId());
            request.addHeader("Cookie", cookies);
        }

        // 1. download
        String execute = download(request);
        SdkObject downloadResult = JsonUtils.fromJson(execute, SdkObject.class);
        String parPath = downloadResult.getMessage();
        if (downloadResult.getBusinessCode() != ResultCode.SUCCESS.getCode()) {
            return mapToResult(resultObject, downloadResult);
        }

        // 2. verify
        DownloadResultObject verifyResult = verifySHA256OrM(paramObject, needVerifySha, resultObject, parPath);
        if (verifyResult != null) {
            return verifyResult;
        }

        // 3. unzip and replace param variables
        SdkObject unzipFileAndDeletePar = unzipFileAndDeletePar(paramObject, saveFilePath, parPath);
        if (unzipFileAndDeletePar.getBusinessCode() != 0) {
            return mapToResult(resultObject, unzipFileAndDeletePar);
        }

        // all success
        return mapToResult(resultObject, downloadResult);
    }

    private DownloadResultObject verifySHA256OrM(ParamObject paramObject, boolean needVerifySha, DownloadResultObject resultObject, String parPath) {
        if (needVerifySha) {
            //compare sha256. if sha256 is null, fail
            SdkObject verifyShaResult = verifySha256(paramObject, parPath);
            if (verifyShaResult.getBusinessCode() != 0) {
                return mapToResult(resultObject, verifyShaResult);
            }
        } else {
            //compare m，if m is null, pass
            if (!verifyM(paramObject, parPath)) {
                resultObject.setBusinessCode(ResultCode.SDK_M_FAILED.getCode());
                resultObject.setMessage(ERROR_REMARKS_VARIFY_MD_FAILED);
                return resultObject;
            }
        }
        return null;
    }

    private DownloadResultObject mapToResult(DownloadResultObject resultObject, SdkObject downloadResult) {
        resultObject.setMessage(downloadResult.getMessage());
        resultObject.setBusinessCode(downloadResult.getBusinessCode());
        return resultObject;
    }

    /**
     * verify the sha256 of the file
     *
     * @param paramObject  You can get ParamObject from getParamDownloadList();
     * @param parPath Path that param files will be saved.
     * @return SdkObject    the final result
     */
    private SdkObject verifySha256(ParamObject paramObject, String parPath) {
        SdkObject sdkObject = new SdkObject();
        if (paramObject.getSha256() == null || paramObject.getSignature() == null) {
            logger.warn("sha256 or signature not found in task");
            sdkObject.setBusinessCode(ResultCode.SDK_SHA256_OR_SIGNATURE_NOT_FOUND.getCode());
            sdkObject.setMessage(ERROR_REMARKS_VARIFY_SHA256_OR_SIG_NOT_FOUND);
            return sdkObject;
        }

        String localCalSHA256 = calculateSHA256(parPath);
        if (!paramObject.getSha256().equals(localCalSHA256)) {
            logger.warn("sha256 verify failed");
            sdkObject.setBusinessCode(ResultCode.SDK_SHA256_FAILED.getCode());
            sdkObject.setMessage(ERROR_REMARKS_VARIFY_SHA256_FAILED);
            return sdkObject;
        }

        String localSignature = HMACSignatureGenerator.generateHmacSha256(localCalSHA256, getAppSecret());
        if (!paramObject.getSignature().equals(localSignature)) {
            logger.warn("signature verify failed");
            sdkObject.setBusinessCode(ResultCode.SDK_SHA256_SIGNATURE_FAILED.getCode());
            sdkObject.setMessage(ERROR_REMARKS_VARIFY_SHA256_SIGNATURE_FAILED);
            return sdkObject;
        }

        // pass all verify, then success
        sdkObject.setBusinessCode(ResultCode.SUCCESS.getCode());
        return sdkObject;
    }

    /**
     * verify the M of the file
     *
     * @param paramObject  You can get ParamObject from getParamDownloadList();
     * @param parPath Path the par file exists
     * @return boolean    the verify result
     */
    private boolean verifyM(ParamObject paramObject, String parPath) {
        if (paramObject.getM() == null || paramObject.getM().equals("")
                || paramObject.getM().equals(MUtils.getFileM(new File(parPath)))) {
            logger.debug("download file md5 is correct");
           return true;
        } else {
            logger.warn("download file md5 is wrong");
            return false;
        }
    }

    /**
     * unzip file to folder and delete par
     *
     * @param paramObject  You can get ParamObject from getParamDownloadList();
     * @param saveFilePath Path that param files will be saved.
     * @param parPath    the par path
     * @return SdkObject the result
     */
    private SdkObject unzipFileAndDeletePar(ParamObject paramObject, String saveFilePath, String parPath) {
        //Unzip zipfile and delete it
        SdkObject sdkObject = new SdkObject();
        boolean unzipResult = ZipUtil.unzip(parPath);
        boolean deleteResult = FileUtils.deleteFile(parPath);
        if (!unzipResult || !deleteResult) {
            sdkObject.setBusinessCode(ResultCode.SDK_UNZIP_FAILED.getCode());
            sdkObject.setMessage(ERROR_UNZIP_FAILED);
            return sdkObject;
        }

        //replace file
        if (!ReplaceUtils.isHashMapJson(paramObject.getParamVariables())) {
            sdkObject.setBusinessCode(ResultCode.SDK_REPLACE_VARIABLES_FAILED.getCode());
            sdkObject.setMessage(ERROR_REMARKS_NOT_GOOD_JSON + paramObject.getParamVariables());
            return sdkObject;
        }
        boolean ifReplaceSuccess = ReplaceUtils.replaceParams(saveFilePath, paramObject.getParamVariables());
        if (!ifReplaceSuccess) {
            logger.warn("replace paramVariables failed");
            sdkObject.setBusinessCode(ResultCode.SDK_REPLACE_VARIABLES_FAILED.getCode());
            sdkObject.setMessage(ERROR_REMARKS_REPLACE_VARIABLES);
            return sdkObject;
        }

        // unzip, delete and replace success
        sdkObject.setBusinessCode(ResultCode.SUCCESS.getCode());
        return sdkObject;
    }


    /**
     * update push task status
     *
     * @param actionId  Id of push task.
     * @param remarks   the remarks
     * @param status    result of push task：{ pending:1, success:2, fail:3 }
     * @param errorCode error code { None error code:0 }
     * @return the update result
     */
    public SdkObject updateDownloadStatus(String actionId, int status, int errorCode, String remarks) {
        String requestUrl = updateStatusUrl.replace("{actionId}", actionId);
        SdkRequest request = new SdkRequest(requestUrl);
        request.setRequestBody("");
        request.setRequestMethod(SdkRequest.RequestMethod.PUT);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        request.addRequestParam(REQ_PARAM_STATUS, Integer.toString(status));
        request.addRequestParam(REQ_PARAM_ERROR_CODE, Integer.toString(errorCode));
        request.addRequestParam(REQ_PARAM_REMARKS, remarks);
        return JsonUtils.fromJson(call(request), SdkObject.class);
    }

    /**
     * Update push task result in a batch.
     *
     * @param updateActionObjectList the update action list
     * @return the update result
     */
    public SdkObject updateDownloadStatusBatch(List<UpdateActionObject> updateActionObjectList) {
        String requestBody = JsonUtils.toJson(updateActionObjectList);
        SdkRequest request = new SdkRequest(updateStatusBatchUrl);
        request.setRequestMethod(SdkRequest.RequestMethod.POST);
        request.addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        request.setRequestBody(requestBody);
        return JsonUtils.fromJson(call(request), SdkObject.class);
    }

    public InnerDownloadResultObject downloadParamToPath(String packageName, int versionCode, String saveFilePath,
                                                            LastFailObject lastFailObject, boolean mobileNetAvailable, boolean needApplyStatus, List<Long> downloadedList) {
        return downloadParams(packageName, versionCode, saveFilePath, lastFailObject, mobileNetAvailable, false, needApplyStatus, downloadedList);
    }

    public InnerDownloadResultObject downloadParamsWithShaCheck(String packageName, int versionCode, String saveFilePath,
                                                                LastFailObject lastFailObject, boolean mobileNetAvailable, boolean needApplyStatus, List<Long> downloadedList) {
        return downloadParams(packageName, versionCode, saveFilePath, lastFailObject, mobileNetAvailable, true, needApplyStatus, downloadedList);
    }

    /**
     * Download param files to specific folder
     *
     * @param packageName        the packageName
     * @param versionCode        the versionCode
     * @param saveFilePath       the saveFilePath
     * @param lastFailObject     the lastFailObject
     * @param mobileNetAvailable the network available
     * @param verySha256 need verify SHA256 or not
     * @param needApplyStatus if need to sync apply result
     * @param downloadedList do not download again for tasks in this list
     * @return the result
     */
    public InnerDownloadResultObject downloadParams(String packageName, int versionCode, String saveFilePath,
                                                    LastFailObject lastFailObject, boolean mobileNetAvailable,
                                                    boolean verySha256, boolean needApplyStatus, List<Long> downloadedList) {
        logger.debug("downloadParamToPath: start");
        InnerDownloadResultObject result = new InnerDownloadResultObject();
        if (saveFilePath == null || "".equals(saveFilePath.trim())) {
            result.setBusinessCode(ResultCode.SDK_FILE_NOT_FOUND.getCode());
            result.setMessage(JsonUtils.getSdkJson(ResultCode.SDK_FILE_NOT_FOUND.getCode(), SAVEPATH_CANNOT_BE_NULL));
            return result;
        }

        result.setParamSavePath(saveFilePath);
        //get paramList
        ParamListObject paramListObject = getParamDownloadList(packageName, versionCode);
        if (paramListObject.getBusinessCode() != ResultCode.SUCCESS.getCode()) {
            result.setBusinessCode(paramListObject.getBusinessCode());
            result.setMessage(paramListObject.getMessage());
            return result;
        } else if (paramListObject.getTotalCount() == 0) {
            result.setBusinessCode(-10);
            result.setMessage("No params to download");
            return result;
        }

        if (needApplyStatus && downloadedList != null && !downloadedList.isEmpty()) {
            List<ParamObject> removeList = new ArrayList<>();
            for (ParamObject paramObject : paramListObject.getList()) {
                for (Long actionId : downloadedList) {
                    if (actionId.equals(paramObject.getActionId())) {
                        removeList.add(paramObject);
                    }
                }
            }
            if (!removeList.isEmpty()) {
                updateAsDownloaded(removeList);
            }
            paramListObject.getList().removeAll(removeList);
            result.setActionList(transferToIdList(removeList));
        }
        if (paramListObject.getList().isEmpty()) {
            result.setBusinessCode(ResultCode.SDK_ALREADY_DOWNLOADED.getCode());
            result.setMessage("Params already downloaded");
            return result;
        }

        //update remarks only
        List<UpdateActionObject> updateBatchBody = getUpdateBatchBody(paramListObject, REMARKS_PARAM_DOWNLOADING, ACT_STATUS_PENDING, CODE_NONE_ERROR);
        updateDownloadStatusBatch(updateBatchBody);
        //download each param

        saveFilePath = saveFilePath + File.separator + paramListObject.getList().get(0).getActionId(); // use first actionId as temp folder name
        String remarks = null;

        for (ParamObject paramObject : paramListObject.getList()) {
            if (paramObject.isWifiOnly() && mobileNetAvailable) { // If this task not allowed, stop downloading params.
                ParamListObject cellularForbidList = new ParamListObject();
                updateDownloadStatus(String.valueOf(paramObject.getActionId()),
                        CODE_NONE_ERROR, CODE_NONE_ERROR, ERROR_CELLULAR_NOT_ALLOWED);
                result.setBusinessCode(ResultCode.SDK_DOWNLOAD_WITH_CELLULAR_NOT_ALLOWED.getCode());
                result.setMessage(ERROR_CELLULAR_NOT_ALLOWED);
                return result;
            }
            SdkObject sdkObject = downloadParamFileOnly(paramObject, saveFilePath, verySha256);
            if (sdkObject.getBusinessCode() != ResultCode.SUCCESS.getCode()) {
                setIOExceptionResult(lastFailObject, result, paramObject, sdkObject);
                result.setBusinessCode(sdkObject.getBusinessCode());
                result.setMessage(sdkObject.getMessage());
                remarks = sdkObject.getMessage();
                logger.debug("download error remarks: " + remarks);
                break;
            }
        }
        if (remarks != null) {
            // Since download failed, result of updating action is not concerned, just return the result of download failed reason
            FileUtils.delFolder(saveFilePath);
            updateActionListByRemarks(paramListObject, result, remarks);
        } else {
            if (!needApplyStatus) {
                SdkObject updateResultObj = updateActionListByRemarks(paramListObject, result, remarks);
                if (updateResultObj.getBusinessCode() != ResultCode.SUCCESS.getCode()) {
                    FileUtils.delFolder(saveFilePath);
                    result.setBusinessCode(updateResultObj.getBusinessCode());
                    result.setMessage(updateResultObj.getMessage());
                } else {
                    FileUtils.moveToFatherFolder(saveFilePath);
                    result.setBusinessCode(ResultCode.SUCCESS.getCode());
                    result.setMessage(DOWNLOAD_SUCCESS);
                }
            } else { // 如果需要把参数的apply状态上送， 那么这里就不要把任务更新为结束
                // 当下载成功之后， 就把文件解压到上级目录
                FileUtils.moveToFatherFolder(saveFilePath);
                // 这里就是把所有的任务不要立即更新为成功， 而是等待更新
                // 这里更新为参数下载成功， 等待apply
                result.setBusinessCode(ResultCode.SUCCESS.getCode());
                result.setMessage(FILE_DOWNLOAD_SUCCESS);
                List<UpdateActionObject> updateBatchBody1 = getUpdateBatchBody(paramListObject, REMARKS_CODE_PARAM_DOWNLOADED, ACT_STATUS_PENDING, CODE_NONE_ERROR);
                updateDownloadStatusBatch(updateBatchBody1);
                ArrayList<Long> actionIdList = transferToIdList(paramListObject.getList()); // 这里不可能为空， 所以不需要判断
                if (result.getActionList() != null) { // 可能之前已经有被igonre的item了， 那么追加即可
                    result.getActionList().addAll(actionIdList);
                } else {
                    result.setActionList(actionIdList);
                }
            }
        }
        logger.debug("downloadParamToPath: end");
        return result;
    }

    /**
     * update as downloaded when see the task again
     * @param removeList
     */
    private void updateAsDownloaded(List<ParamObject> removeList) {
        ParamListObject paramListObject = new ParamListObject();
        paramListObject.setList(removeList);
        List<UpdateActionObject> updateBatchBody = getUpdateBatchBody(paramListObject, REMARKS_CODE_PARAM_DOWNLOADED, ACT_STATUS_PENDING, CODE_NONE_ERROR);
        updateDownloadStatusBatch(updateBatchBody);
    }

    /**
     * transform to actionId list
     * @param list
     * @return
     */
    private static ArrayList<Long> transferToIdList(List<ParamObject> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        ArrayList<Long> actionIdList = new ArrayList<>();
        for (ParamObject paramObject : list) {
            actionIdList.add(paramObject.getActionId());
        }
        return actionIdList;
    }


    public InnerDownloadResultObject downloadLastSuccessParmToPath(String savePath) {
        return downloadLastSuccess(savePath, null, false);
    }

    public InnerDownloadResultObject downloadLastSuccessParmToPath(String saveFilePath, String paramTemplateName) {
        return downloadLastSuccess(saveFilePath, paramTemplateName, false);
    }

    public DownloadResultObject downloadLastSuccessWithSha256Check(String saveFilePath) {
        return mapToDownloadResult(saveFilePath,
                downloadLastSuccess(saveFilePath, null, true));
    }

    public DownloadResultObject downloadLastSuccessWithSha256Check(String saveFilePath, String paramTemplateName) {
        return mapToDownloadResult(saveFilePath,
                downloadLastSuccess(saveFilePath, paramTemplateName, true));
    }

    public DownloadResultObject mapToDownloadResult(String saveFilePath, InnerDownloadResultObject downloadResultObject) {
        DownloadResultObject resultObject = new DownloadResultObject();
        resultObject.setMessage(downloadResultObject.getMessage());
        resultObject.setBusinessCode(downloadResultObject.getBusinessCode());
        resultObject.setParamSavePath(saveFilePath);
        resultObject.setActionList(downloadResultObject.getActionList());
        if (resultObject.getBusinessCode() != 0) {
            logger.error("Download Result:" + "errorCode: " + resultObject.getBusinessCode() + " errorMessage: " + resultObject.getMessage());
        }
        return resultObject;
    }

    public InnerDownloadResultObject downloadLastSuccess(String saveFilePath, String paramTemplateName, boolean needVerifySha) {
        logger.debug("downloadLastSuccessParmToPath: start");
        InnerDownloadResultObject result = new InnerDownloadResultObject();
        if (saveFilePath == null || "".equals(saveFilePath.trim())) {
            result.setBusinessCode(ResultCode.SDK_FILE_NOT_FOUND.getCode());
            result.setMessage(JsonUtils.getSdkJson(ResultCode.SDK_FILE_NOT_FOUND.getCode(), SAVEPATH_CANNOT_BE_NULL));
            return result;
        }
        result.setParamSavePath(saveFilePath);
        ParamObject paramObject;
        if (paramTemplateName != null) {
            paramObject = getLastSuccessParam(paramTemplateName);
        } else {
            paramObject = getLastSuccessParam();
        }

        if (paramObject.getBusinessCode() != ResultCode.SUCCESS.getCode()) {
            result.setBusinessCode(paramObject.getBusinessCode());
            result.setMessage(paramObject.getMessage());
            return result;
        }

        saveFilePath = saveFilePath + File.separator + paramObject.getActionId();
        String remarks = null;

        SdkObject sdkObject = downloadParamFileOnly(paramObject, saveFilePath, needVerifySha);
        if (sdkObject.getBusinessCode() != ResultCode.SUCCESS.getCode()) {
            result.setBusinessCode(sdkObject.getBusinessCode());
            result.setMessage(sdkObject.getMessage());
            remarks = sdkObject.getMessage();
            logger.warn("download error remarks: " + remarks);
        }


        if (remarks != null) {
            // Since download failed, result of updating action is not concerned, just return the result of download failed reason
            FileUtils.delFolder(saveFilePath);
        } else {
            FileUtils.moveToFatherFolder(saveFilePath);
            result.setBusinessCode(ResultCode.SUCCESS.getCode());
            result.setMessage(DOWNLOAD_SUCCESS);

        }
        logger.debug("downloadLastSuccessParmToPath: end");
        return result;
    }

    /**
     *
     */
    private void setIOExceptionResult(LastFailObject lastFailObject, InnerDownloadResultObject result, ParamObject paramObject, SdkObject sdkObject) {
        if (sdkObject.getMessage() != null && sdkObject.getMessage().contains(IOEXCTION_FLAG)) {
            if (lastFailObject == null) {
                lastFailObject = new LastFailObject();
            }
            // if it is a new task, update the fail time
            if (lastFailObject.getActionId() != paramObject.getActionId()) {
                lastFailObject.setFirstTryTime(System.currentTimeMillis());
                lastFailObject.setRetryCount(0);
            }
            lastFailObject.setActionId(paramObject.getActionId());
            lastFailObject.setRetryCount(lastFailObject.getRetryCount() + 1);
            result.setLastFailObject(lastFailObject);
        }
    }

    private SdkObject updateActionListByRemarks(ParamListObject paramListObject, InnerDownloadResultObject result, String remarks) {
        List<UpdateActionObject> updateBatchList;
        if (result.getMessage() != null && result.getMessage().contains(IOEXCTION_FLAG)
                && result.getLastFailObject() != null) {
            if (result.getLastFailObject().getRetryCount() > RETRY_COUNT) {
                updateBatchList = getUpdateBatchBody(paramListObject,
                        String.format(" Bad network connection, exceeded max retry times: %d . %s", RETRY_COUNT, remarks),
                        ACT_STATUS_FAILED, CODE_DOWNLOAD_ERROR);
            } else if (result.getLastFailObject().getFirstTryTime() + RETRY_TIME_LIMIT < System.currentTimeMillis()) {
                updateBatchList = getUpdateBatchBody(paramListObject,
                        String.format(" Bad network connection, exceeded max retry time 10 days. %s", remarks),
                        ACT_STATUS_FAILED, CODE_DOWNLOAD_ERROR);
            } else {
                remarks = String.format(" Bad network connection, %d time(s) tried. %s", result.getLastFailObject().getRetryCount(), remarks);
                result.setBusinessCode(ResultCode.SDK_DOWNLOAD_IOEXCEPTION.getCode());
                updateBatchList = getUpdateBatchBody(paramListObject, remarks, CODE_NONE_ERROR, CODE_NONE_ERROR);
            }
        } else if (remarks != null) {
            updateBatchList = getUpdateBatchBody(paramListObject, remarks, ACT_STATUS_FAILED, CODE_DOWNLOAD_ERROR);
        } else {
            updateBatchList = getUpdateBatchBody(paramListObject, remarks, ACT_STATUS_SUCCESS, CODE_NONE_ERROR);
        }
        return updateDownloadStatusBatch(updateBatchList);
    }

    private List<UpdateActionObject> getUpdateBatchBody(ParamListObject paramListObject, String remarks, int status, int errorCode) {
        List<UpdateActionObject> updateActionObjectList = new ArrayList<UpdateActionObject>();
        String updateBatch;
        for (ParamObject paramObject : paramListObject.getList()) {
            UpdateActionObject updateActionObject = new UpdateActionObject();
            updateActionObject.setActionId(paramObject.getActionId());
            updateActionObject.setStatus(status);
            updateActionObject.setErrorCode(errorCode);
            updateActionObject.setRemarks(remarks);
            updateActionObjectList.add(updateActionObject);
        }
        return updateActionObjectList;
    }

    /**
     * @param file the file
     * @return the list
     * @throws JsonParseException the exception
     */
    public LinkedHashMap<String, String> parseDownloadParamJsonWithOrder(File file) throws JsonParseException {
        if (file != null) {
            String fileString = null;
            try {
                fileString = FileUtils.readFileToString(file);

                if (fileString != null) {
                    Gson gson = new Gson();
                    java.lang.reflect.Type type = new TypeToken<LinkedHashMap<String, String>>() {
                    }.getType();
                    return gson.fromJson(fileString, type);
                }
            } catch (Exception e) {
                logger.error("Read file error" + e);
                throw new JsonParseException(e.getMessage());
            }
        }
        return null;
    }

    public HashMap<String, String> parseDownloadParamXml(File xmlFile) throws ParseXMLException{
        HashMap<String, String> xmlData = new HashMap<>();
        if (!xmlFile.exists() || !xmlFile.isFile()) {
            System.out.println("parseDownloadParamXml error, File not exists or not a valid xml");
            return xmlData;
        }
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            InputStreamReader isr = new InputStreamReader(new FileInputStream(xmlFile), StandardCharsets.UTF_8);
            Document doc = dBuilder.parse(new InputSource(isr));

            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    xmlData.put(element.getNodeName(), element.getTextContent());
                }
            }
        } catch (Exception e) {
            throw new ParseXMLException(e);
        }
        return xmlData;
    }

    public LinkedHashMap<String, String> parseDownloadParamXmlWithOrder(File xmlFile) throws ParseXMLException{
        LinkedHashMap<String, String> xmlData = new LinkedHashMap<>();
        if (!xmlFile.exists() || !xmlFile.isFile()) {
            System.out.println("parseDownloadParamXmlWithOrder error, File not exists or not a valid xml");
            return xmlData;
        }
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            InputStreamReader isr = new InputStreamReader(new FileInputStream(xmlFile), StandardCharsets.UTF_8);
            Document doc = dBuilder.parse(new InputSource(isr));

            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    xmlData.put(element.getNodeName(), element.getTextContent());
                }
            }
        } catch (Exception e) {
            throw new ParseXMLException(e);
        }
        return xmlData;
    }

    public SdkObject updateApplyResult(List<Long> actionIdList, Integer status, int errorCode, String remarks) {
        SdkObject sdkObject = new SdkObject();
        if (status == null) {
            sdkObject.setBusinessCode(-1);
            sdkObject.setMessage("status should not be null");
            return sdkObject;
        }
        if (actionIdList == null || actionIdList.isEmpty()) {
            sdkObject.setBusinessCode(-1);
            sdkObject.setMessage("actionIdList should not be null or empty");
            return sdkObject;
        }


        List<UpdateActionObject> updateActionObjectList = new ArrayList<UpdateActionObject>();
        for (Long actionId : actionIdList) {
            UpdateActionObject updateActionObject = new UpdateActionObject();
            updateActionObject.setActionId(actionId);
            updateActionObject.setStatus(status);
            updateActionObject.setErrorCode(errorCode);
            updateActionObject.setRemarks(remarks);
            updateActionObjectList.add(updateActionObject);
        }
        return updateDownloadStatusBatch(updateActionObjectList);
    }

}
