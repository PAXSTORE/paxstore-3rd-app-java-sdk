package com.pax.market.api.sdk.java.api.sync.dto;

/*
 * ===========================================================================================
 * = COPYRIGHT
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or nondisclosure
 *   agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or
 *   disclosed except in accordance with the terms in that agreement.
 *
 *     Copyright (C) 2018 PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.
 *
 * Revision History:
 * Date	                 Author	                Action
 * 2018/10/8  	         miaoyf           	    Create
 * ===========================================================================================
 */

import java.util.List;

public class TerminalSyncBizData {

    List bizDataList;

    public List getBizDataList() {
        return bizDataList;
    }

    public void setBizDataList(List bizDataList) {
        this.bizDataList = bizDataList;
    }
}
