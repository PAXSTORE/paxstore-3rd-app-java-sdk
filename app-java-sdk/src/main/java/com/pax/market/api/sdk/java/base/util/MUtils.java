/*
 *  *******************************************************************************
 *  COPYRIGHT
 *                PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *    This software is supplied under the terms of a license agreement or
 *    nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *    or disclosed except in accordance with the terms in that agreement.
 *
 *       Copyright (C) 2017 PAX Technology, Inc. All rights reserved.
 *  *******************************************************************************
 */
package com.pax.market.api.sdk.java.base.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * Created by zcy on 2017/4/17 0017.
 */
public class MUtils {

    /**
     * Get the m value of a single file
     * @param file the file
     * @return the file m
     */
    public static String getFileM(File file) {
        if (!file.isFile()) {
            return null;
        }
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            String m = getFileM(in);

            return m;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            if(in != null){
                try{
                    in.close();
                }catch (Exception ignore){

                }
            }
        }
    }

    /**
     *
     * @param fis the inputStream
     * @return the file M
     * @throws Exception the exception
     */
    public static String getFileM(InputStream fis) throws Exception {
        MessageDigest md = null;
        md = MessageDigest.getInstance(getM());


        byte[] dataBytes = new byte[1024];

        int nread = 0;
        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        }

        byte[] mdbytes = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //convert the byte to hex format method 2
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < mdbytes.length; i++) {
            String hex = Integer.toHexString(0xff & mdbytes[i]);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        String result = hexString.toString();
        return result;
    }


    private static String getM() {
        String[] array = {"z", "H", "m", "a", "y", "aa", "c", "a", "M", "cc", "D", "asd", "5", "x", "w"};
        StringBuilder result = new StringBuilder();
        result.append(array[8]);
        result.append(array[10]);
        result.append(array[12]);
        return result.toString();
    }

}
