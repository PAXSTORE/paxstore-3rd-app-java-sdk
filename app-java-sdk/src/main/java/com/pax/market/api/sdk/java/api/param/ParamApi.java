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

import com.pax.market.api.sdk.java.base.api.BaseApi;
import com.pax.market.api.sdk.java.base.constant.Constants;
import com.pax.market.api.sdk.java.base.constant.ResultCode;
import com.pax.market.api.sdk.java.base.dto.DownloadResultObject;
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


/**
 * Created by fanjun on 2016/12/5.
 */
public class ParamApi extends BaseApi {
    private final Logger logger = LoggerFactory.getLogger(ParamApi.class.getSimpleName());


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

    private static final String REQ_PARAM_PACKAGE_NAME = "packageName";
    private static final String REQ_PARAM_VERSION_CODE = "versionCode";
    private static final String REQ_PARAM_STATUS = "status";
    private static final String REQ_PARAM_ERROR_CODE = "errorCode";
    private static final String REQ_PARAM_REMARKS = "remarks";
    private static final String ERROR_REMARKS_REPLACE_VARIABLES = "Replace paramVariables failed";
    private static final String ERROR_REMARKS_VARIFY_MD_FAILED = "MD5 Validation Error";
    private static final String ERROR_UNZIP_FAILED = "Unzip file failed";
    private static final String DOWNLOAD_SUCCESS = "Success";
    public static final String REMARKS_PARAM_DOWNLOADING = "15206";         //Param is downloading


    public ParamApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
        super(baseUrl, appKey, appSecret, terminalSN);
    }

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
     * Get terminal params to download
     *
     * @param packageName
     * @param versionCode
     * @return
     */
    public ParamListObject getParamDownloadList(String packageName, int versionCode){
        SdkRequest request = new SdkRequest(downloadParamUrl);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        request.addRequestParam(REQ_PARAM_PACKAGE_NAME, packageName);
        request.addRequestParam(REQ_PARAM_VERSION_CODE, Integer.toString(versionCode));
        return JsonUtils.fromJson(call(request), ParamListObject.class);
    }

    /**
     * Download param files
     *
     * @param paramObject  You can get ParamObject from getParamDownloadList();
     * @param saveFilePath Path that param files will be saved.
     * @return
     */
    public DownloadResultObject downloadParamFileOnly(ParamObject paramObject, String saveFilePath) {
        SdkRequest request = new SdkRequest(paramObject.getDownloadUrl());
        request.setSaveFilePath(saveFilePath);
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
                    boolean ifReplaceSuccess = ReplaceUtils.replaceParams(saveFilePath, paramObject.getParamVariables());
                    if (!ifReplaceSuccess) {
                        logger.info("replace paramVariables failed");
                        sdkObject.setBusinessCode(ResultCode.SDK_REPLACE_VARIABLES_FAILED.getCode());
                        sdkObject.setMessage(ERROR_REMARKS_REPLACE_VARIABLES);
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
     * @param status    result of push task：{ pending:1, success:2, fail:3 }
     * @param errorCode error code { None error code:0 }
     * @return
     */
    public SdkObject updateDownloadStatus(String actionId, int status, int errorCode, String remarks){
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
     *
     * @param updateActionObjectList
     * @return
     */
    public SdkObject updateDownloadStatusBatch(List<UpdateActionObject> updateActionObjectList){
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
     * @param packageName
     * @param versionCode
     * @param saveFilePath
     * @return
     */
    public DownloadResultObject downloadParamToPath(String packageName, int versionCode, String saveFilePath) {
        logger.debug("downloadParamToPath: start");
        DownloadResultObject result = new DownloadResultObject();
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
            SdkObject sdkObject = downloadParamFileOnly(paramObject, saveFilePath);
            if (sdkObject.getBusinessCode() != ResultCode.SUCCESS.getCode()) {
                result.setBusinessCode(sdkObject.getBusinessCode());
                result.setMessage(sdkObject.getMessage());
                if (ResultCode.toResultCode(sdkObject.getBusinessCode()) != ResultCode.UN_CODE) { // check code isdefined in ResultCode, resultcode can be translated by server, no message needed.
                    remarks = sdkObject.getBusinessCode() + "";
                } else {
                    remarks = sdkObject.getBusinessCode() + ":" + sdkObject.getMessage();
                }
                break;
            }
        }

        if (remarks != null) {
            // Since download failed, result of updating action is not concerned, just return the result of download failed reason
            FileUtils.delFolder(saveFilePath);
            updateActionListByRemarks(paramListObject, remarks);
        } else {
            SdkObject updateResultObj = updateActionListByRemarks(paramListObject, remarks);
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


    private SdkObject updateActionListByRemarks(ParamListObject paramListObject, String remarks) {

        List<UpdateActionObject> updateBatchList;
        if (remarks != null) {
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
     * parse the downloaded parameter xml file, convert the xml elements to HashMap<String,String>
     * this method will not keep the xml fields order. HashMap will have a better performance.
     *
     * @param file the downloaded xml
     * @return HashMap with key/value of xml elements
     */
    public HashMap<String,String> parseDownloadParamXml(File file) throws ParseXMLException {
        HashMap<String,String> resultMap = new HashMap<>();
        if(file!=null){
            try {
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(file);
                Element root = document.getRootElement();
                for (Iterator it = root.elementIterator(); it.hasNext(); ) {
                    Element element = (Element) it.next();
                    resultMap.put(element.getName(),element.getText());
                }
            }catch (Exception e){
                throw new ParseXMLException(e);
            }
        }else{
            logger.info("parseDownloadParamXml: file is null, please make sure the file is correct.");
        }
        return resultMap;
    }


    /**
     * parse the downloaded parameter xml file, convert the xml elements to LinkedHashMap<String,String>
     * this method will keep the xml fields order. LinkedHashMap performance is slower than HashMap
     *
     * @param file the downloaded xml
     * @return LinkedHashMap with key/value of xml elements
     */
    public LinkedHashMap<String,String> parseDownloadParamXmlWithOrder(File file) throws ParseXMLException {
        LinkedHashMap<String,String> resultMap = new LinkedHashMap<>();
        if(file!=null){
            try {
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(file);
                Element root = document.getRootElement();
                for (Iterator it = root.elementIterator(); it.hasNext(); ) {
                    Element element = (Element) it.next();
                    resultMap.put(element.getName(),element.getText());
                }
            }catch (Exception e){
                throw new ParseXMLException(e);
            }
        }else{
            logger.info("parseDownloadParamXmlWithOrder: file is null, please make sure the file is correct.");
        }
        return resultMap;
    }


}
