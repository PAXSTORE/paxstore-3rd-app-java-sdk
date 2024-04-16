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


/**
 * Created by fanjun on 2016/11/10.
 */
public class CryptoUtils {

    public static String getM() {
        String[] array = {"z", "H", "m", "a", "y", "aa", "c", "a", "M", "cc", "D", "asd", "5", "x", "w"};
        StringBuilder result = new StringBuilder();
        result.append(array[1]);
        result.append(array[2]);
        result.append(array[3]);
        result.append(array[6]);
        result.append(array[8]);
        result.append(array[10]);
        result.append(array[12]);
        return result.toString();
    }

}
