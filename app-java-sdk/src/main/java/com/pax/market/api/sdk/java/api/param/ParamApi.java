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
import com.pax.market.api.sdk.java.base.util.JsonUtils;
import com.pax.market.api.sdk.java.base.util.Md5Utils;
import com.pax.market.api.sdk.java.base.util.ReplaceUtils;
import com.pax.market.api.sdk.java.base.util.ZipUtil;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

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

    /**
     * The constant RETRY_COUNT.
     */
    public static final int RETRY_COUNT = 20;


    /**
     * The constant RETRY_TIME_LIMIT.
     */
    public static final long RETRY_TIME_LIMIT = 10 * 24 * 3600_000L;

    /**
     *Get last success param limit
     */
    public static final long GET_SUCCESS_PARAM_LIMIT = 60_000L;

    private static final String REQ_PARAM_PACKAGE_NAME = "packageName";
    private static final String REQ_PARAM_VERSION_CODE = "versionCode";
    private static final String REQ_PARAM_STATUS = "status";
    private static final String REQ_PARAM_ERROR_CODE = "errorCode";
    private static final String REQ_PARAM_REMARKS = "remarks";
    private static final String REQ_PARAM_TEMPLATE_NAME = "paramTemplateName";
    private static final String ERROR_REMARKS_REPLACE_VARIABLES = "Replace paramVariables failed";
    private static final String ERROR_REMARKS_NOT_GOOD_JSON = "Bad json : ";
    private static final String ERROR_REMARKS_VARIFY_MD_FAILED = "MD5 Validation Error";
    private static final String ERROR_UNZIP_FAILED = "Unzip file failed";
    private static final String DOWNLOAD_SUCCESS = "Success";
    public static final String ERROR_CELLULAR_NOT_ALLOWED = "Cellular download not allowed";
    private static final String SAVEPATH_CANNOT_BE_NULL = "Save path can not be empty";

    /**
     * The constant downloadParamUrl.
     */
    protected static String downloadParamUrl = "/3rdApps/param";
    /**
     * The constant updateStatusUrl.
     */
    protected static String updateStatusUrl = "/3rdApps/actions/{actionId}/status";
    /**
     * The constant updateStatusBatchUrl.
     */
    protected static String updateStatusBatchUrl = "/3rdApps/actions";
    /**
     * Get last success param url
     */
    protected static String lastSuccessParamUrl = "/3rdApps/param/last/success";

    private final Logger logger = LoggerFactory.getLogger(ParamApi.class.getSimpleName());

    private long lastGetTime = -1;

    public ParamApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
        super(baseUrl, appKey, appSecret, terminalSN);
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
     * Get terminal last success parm
     * @param paramTemplateName  the template need to get
     * @return the param objet
     */
    public ParamObject getLastSuccessParm(String paramTemplateName) {
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

    public ParamObject getLastSuccessParm() {
       return getLastSuccessParm(null);
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
     * @return the download result
     */
    public DownloadResultObject downloadParamFileOnly(ParamObject paramObject, String saveFilePath) {
        SdkRequest request = new SdkRequest(paramObject.getDownloadUrl());
        request.setSaveFilePath(saveFilePath);
        if (paramObject.getCookieSignature() != null) { // there exist cookies
            String cookies = cookieHeader(paramObject.getCookieSignature(), paramObject.getCookieExpires(), paramObject.getCookieKeyPairId());
            request.addHeader("Cookie", cookies);
        }

        String execute = download(request);
        SdkObject sdkObject = JsonUtils.fromJson(execute, SdkObject.class);

        if (sdkObject.getBusinessCode() == ResultCode.SUCCESS.getCode()) {
            //compare md，if md is null, pass
            if (paramObject.getMd() == null || paramObject.getMd().equals("")
                    || paramObject.getMd().equals(Md5Utils.getFileMD5(new File(sdkObject.getMessage())))) {
                logger.debug("download file md5 is correct");
                //Unzip zipfile and delete it
                boolean unzipResult = ZipUtil.unzip(sdkObject.getMessage());
                boolean deleteResult = FileUtils.deleteFile(sdkObject.getMessage());
                if (!unzipResult || !deleteResult) {
                    sdkObject.setBusinessCode(ResultCode.SDK_UNZIP_FAILED.getCode());
                    sdkObject.setMessage(ERROR_UNZIP_FAILED);
                } else {
                    //replace file
                    if (!ReplaceUtils.isHashMapJson(paramObject.getParamVariables())) {
                        sdkObject.setBusinessCode(ResultCode.SDK_REPLACE_VARIABLES_FAILED.getCode());
                        sdkObject.setMessage(ERROR_REMARKS_NOT_GOOD_JSON + paramObject.getParamVariables());
                    } else {
                        boolean ifReplaceSuccess = ReplaceUtils.replaceParams(saveFilePath, paramObject.getParamVariables());
                        if (!ifReplaceSuccess) {
                            logger.info("replace paramVariables failed");
                            sdkObject.setBusinessCode(ResultCode.SDK_REPLACE_VARIABLES_FAILED.getCode());
                            sdkObject.setMessage(ERROR_REMARKS_REPLACE_VARIABLES);
                        }
                    }
                }
            } else {
                logger.debug("download file md5 is wrong");
                sdkObject.setBusinessCode(ResultCode.SDK_MD_FAILED.getCode());
                sdkObject.setMessage(ERROR_REMARKS_VARIFY_MD_FAILED);
            }
        }

        DownloadResultObject resultObject = new DownloadResultObject();
        resultObject.setMessage(sdkObject.getMessage());
        resultObject.setBusinessCode(sdkObject.getBusinessCode());
        resultObject.setParamSavePath(saveFilePath);
        return resultObject;
    }


    /**
     * update push task status
     *
     * @param actionId  Id of push task.
     * @param remarks the remarks
     * @param status    result of push task：{ pending:1, success:2, fail:3 }
     * @param errorCode error code { None error code:0 }
     * @return the update result
     */
    public SdkObject updateDownloadStatus(String actionId, int status, int errorCode, String remarks) {
        String requestUrl = updateStatusUrl.replace("{actionId}", actionId);
        SdkRequest request = new SdkRequest(requestUrl);
        request.setRequestMethod(SdkRequest.RequestMethod.PUT);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        request.addRequestParam(REQ_PARAM_STATUS, Integer.toString(status));
        request.addRequestParam(REQ_PARAM_ERROR_CODE, Integer.toString(errorCode));
        request.addRequestParam(REQ_PARAM_REMARKS, remarks);
        return JsonUtils.fromJson(call(request), SdkObject.class);
    }

    /**
     * Update push task result in a batch.
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


    /**
     * Download param files to specific folder
     *
     * @param packageName the packageName
     * @param versionCode the versionCode
     * @param saveFilePath the saveFilePath
     * @param lastFailObject the lastFailObject
     * @param mobileNetAvailable  the network available
     * @return the result
     */
    public InnerDownloadResultObject downloadParamToPath(String packageName, int versionCode, String saveFilePath,
                                                         LastFailObject lastFailObject, boolean mobileNetAvailable) {
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
            SdkObject sdkObject = downloadParamFileOnly(paramObject, saveFilePath);
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
        }
        logger.debug("downloadParamToPath: end");
        return result;
    }

    public InnerDownloadResultObject downloadLastSuccessParmToPath(String savePath) {
        return downloadLastSuccessParmToPath(savePath, null);
    }

    public InnerDownloadResultObject downloadLastSuccessParmToPath(String saveFilePath, String paramTemplateName) {
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
             paramObject = getLastSuccessParm(paramTemplateName);
        } else {
            paramObject = getLastSuccessParm();
        }

        if (paramObject.getBusinessCode() != ResultCode.SUCCESS.getCode()) {
            result.setBusinessCode(paramObject.getBusinessCode());
            result.setMessage(paramObject.getMessage());
            return result;
        }

        saveFilePath = saveFilePath + File.separator + paramObject.getActionId();
        String remarks = null;

        SdkObject sdkObject = downloadParamFileOnly(paramObject, saveFilePath);
        if (sdkObject.getBusinessCode() != ResultCode.SUCCESS.getCode()) {
            result.setBusinessCode(sdkObject.getBusinessCode());
            result.setMessage(sdkObject.getMessage());
            remarks = sdkObject.getMessage();
            logger.debug("download error remarks: " + remarks);
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
     * @param lastFailObject
     * @param result
     * @param paramObject
     * @param sdkObject
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
     * parse the downloaded parameter xml file, convert the xml elements to HashMap String,String
     * this method will not keep the xml fields order. HashMap will have a better performance.
     *
     * @param file the downloaded xml
     * @return HashMap with key/value of xml elements
     * @throws ParseXMLException the exception
     */
    public HashMap<String, String> parseDownloadParamXml(File file) throws ParseXMLException {
        HashMap<String, String> resultMap = new HashMap<>();
        if (file != null) {
            try {
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(file);
                Element root = document.getRootElement();
                for (Iterator it = root.elementIterator(); it.hasNext(); ) {
                    Element element = (Element) it.next();
                    resultMap.put(element.getName(), element.getText());
                }
            } catch (Exception e) {
                throw new ParseXMLException(e);
            }
        } else {
            logger.info("parseDownloadParamXml: file is null, please make sure the file is correct.");
        }
        return resultMap;
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
                fileString = org.apache.commons.io.FileUtils.readFileToString(file);

                if (fileString != null) {
                    Gson gson = new Gson();
                    java.lang.reflect.Type type = new TypeToken<LinkedHashMap<String, String>>() {
                    }.getType();
                    return gson.fromJson(fileString, type);
                }
            } catch (Exception e) {
                logger.error("Read file error", e);
                throw new JsonParseException(e.getMessage());
            }
        }
        return null;
    }

    /**
     * parse the downloaded parameter xml file, convert the xml elements to LinkedHashMap String,String
     * this method will keep the xml fields order. LinkedHashMap performance is slower than HashMap
     *
     * @param file the downloaded xml
     * @return LinkedHashMap with key/value of xml elements
     * @throws ParseXMLException the exception
     */
    public LinkedHashMap<String, String> parseDownloadParamXmlWithOrder(File file) throws ParseXMLException {
        LinkedHashMap<String, String> resultMap = new LinkedHashMap<>();
        if (file != null) {
            try {
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(file);
                Element root = document.getRootElement();
                for (Iterator it = root.elementIterator(); it.hasNext(); ) {
                    Element element = (Element) it.next();
                    resultMap.put(element.getName(), element.getText());
                }
            } catch (Exception e) {
                throw new ParseXMLException(e);
            }
        } else {
            logger.info("parseDownloadParamXmlWithOrder: file is null, please make sure the file is correct.");
        }
        return resultMap;
    }


}
