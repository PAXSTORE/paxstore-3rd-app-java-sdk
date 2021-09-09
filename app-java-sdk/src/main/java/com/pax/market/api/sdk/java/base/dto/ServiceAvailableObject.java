/*
 * *
 *     * ********************************************************************************
 *     * COPYRIGHT
 *     *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *     *   This software is supplied under the terms of a license agreement or
 *     *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *     *   or disclosed except in accordance with the terms in that agreement.
 *     *
 *     *      Copyright (C) 2017 PAX Technology, Inc. All rights reserved.
 *     * ********************************************************************************
 *
 */

package com.pax.market.api.sdk.java.base.dto;

import com.google.gson.annotations.SerializedName;


public class ServiceAvailableObject extends SdkObject {
    /**
     * if update available
     */
    @SerializedName("serviceAvailable")
    private Boolean serviceAvailable;

    public Boolean isServiceAvailable() {
        return serviceAvailable;
    }

    public void setServiceAvailable(Boolean serviceAvailable) {
        this.serviceAvailable = serviceAvailable;
    }

    @Override
    public String toString() {
        return "UpdateObject{" +
                "updateAvailable=" + serviceAvailable +
                '}';
    }
}