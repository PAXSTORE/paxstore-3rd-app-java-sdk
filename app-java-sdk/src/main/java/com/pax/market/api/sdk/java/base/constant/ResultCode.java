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
package com.pax.market.api.sdk.java.base.constant;

/**
 * The type Result code.
 * Attention: Code below should not smaller than 16100
 */
public enum ResultCode {

    SUCCESS(0),
    SDK_PARAM_ERROR(16100),
    SDK_UNINIT(16101),
    SDK_DEC_ERROR(16102),
    SDK_JSON_ERROR(16103),
    SDK_CONNECT_TIMEOUT(16104),
    SDK_UN_CONNECT(16105),
    SDK_RQUEST_EXCEPTION(16106),
    SDK_UNZIP_FAILED(16107),
    SDK_M_FAILED(16108),
    SDK_REPLACE_VARIABLES_FAILED(16109),
    SDK_INIT_FAILED(16110),
    SDK_FILE_NOT_FOUND(16111),
    SDK_DOWNLOAD_IOEXCEPTION(16112),
    SDK_DOWNLOAD_WITH_CELLULAR_NOT_ALLOWED(16113),
    NO_HOST_AVAILABLE(16114),
    API_CALLED_TOO_FAST(16115),
    SDK_SHA256_FAILED(16116),
    SDK_SHA256_OR_SIGNATURE_NOT_FOUND(16117),
    SDK_SHA256_SIGNATURE_FAILED(16118),

    SDK_ALREADY_DOWNLOADED(16119),
    PARAM_NO_LAST_SUCCESS_FOUND(16120),
    UPLOAD_FILE_TOO_LARGE(16121),
    PARAM_NO_PARAMS_TASK(-10),

    UN_CODE(-1);

    private int code;

    ResultCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ResultCode toResultCode(int code) {
        for (ResultCode resultCode : values()) {
            if (resultCode.getCode() == code) {
                return resultCode;
            }
        }
        return UN_CODE;
    }



}