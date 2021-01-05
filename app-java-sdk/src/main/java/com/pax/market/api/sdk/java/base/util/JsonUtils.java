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
package com.pax.market.api.sdk.java.base.util;

import com.google.gson.Gson;
import com.pax.market.api.sdk.java.base.constant.ResultCode;
import com.pax.market.api.sdk.java.base.dto.SdkObject;


/**
 * The type Json utils.
 */
public class JsonUtils {
    private static Gson gson = getGson();

    /**
     * Gets gson.
     *
     * @return the gson
     */
    static /*package*/ Gson getGson() {
        if (gson == null) {
            synchronized (Gson.class) {
                if (gson == null) {
                    gson = new Gson();
                }
            }
        }
        return gson;
    }

    /**
     * Generate Json object format string according to javaBean
     *
     * @param object Any javaBean type object
     * @return The spliced String object string
     */
    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    /**
     * Generate Javabean based on the Json string returned by the SDK, and the json string is encapsulated in data
     *
     * @param <T>        the type parameter
     * @param sdkJsonStr Json string
     * @param clazz      the clazz
     * @return Javabean Object
     */
    public static <T> T fromJson(String sdkJsonStr, Class<T> clazz) {
        return gson.fromJson(sdkJsonStr, clazz);
    }

    /**
     * Gets sdk json.
     *
     * @param resultCode the result code
     * @return the sdk json
     */
    public static String getSdkJson(int resultCode, String errorMsg) {
        String message = "";
        switch (ResultCode.toResultCode(resultCode)) {
            case SDK_PARAM_ERROR:
                message = "Sdk param error";
                break;
            case SDK_UNINIT:
                message = "Sdk uninit";
                break;
            case SDK_DEC_ERROR:
                message = "Sdk decrypt error";
                break;
            case SDK_JSON_ERROR:
                message = "Sdk json error";
                break;
            case SDK_CONNECT_TIMEOUT:
                message = "Sdk connect timeout";
                break;
            case SDK_UN_CONNECT:
                message = "Sdk unconnect";
                break;
            case SDK_RQUEST_EXCEPTION:
                message = "Sdk rquest exception";
                break;
            case SDK_UNZIP_FAILED:
                message = "Sdk unzip failed";
                break;
            case SDK_MD_FAILED:
                message = "Sdk md failed";
                break;
            case SDK_REPLACE_VARIABLES_FAILED:
                message = "Sdk replace variables failed";
                break;
            case SDK_INIT_FAILED:
                message = "Sdk init failed";
                break;
            case SDK_FILE_NOT_FOUND:
                message = "Sdk file not found";
                break;

        }
        message = message + ":" +errorMsg;
        return getSdkJsonStr(resultCode, message);
    }

    /**
     * Gets sdk json.
     *
     * @param resultCode the result code
     * @param message    the message
     * @return the sdk json
     */
    public static String getSdkJsonStr(int resultCode, String message) {
        SdkObject sdkObject = new SdkObject();
        sdkObject.setBusinessCode(resultCode);
        sdkObject.setMessage(message);
        return toJson(sdkObject);
    }
}