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
    SDK_MD_FAILED(16108),
    SDK_REPLACE_VARIABLES_FAILED(16109),
    SDK_INIT_FAILED(16110),
    SDK_FILE_NOT_FOUND(16111),
    SDK_DOWNLOAD_IOEXCEPTION(16112),

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