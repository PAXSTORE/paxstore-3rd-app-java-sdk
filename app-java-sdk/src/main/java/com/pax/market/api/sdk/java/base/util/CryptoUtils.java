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


import com.pax.market.api.sdk.java.base.constant.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by fanjun on 2016/11/10.
 */
public class CryptoUtils {
    private static final Logger logger = LoggerFactory.getLogger(CryptoUtils.class);

    private static final String AES_CBC = "AES/CBC/PKCS5Padding";

    public static final int DEFAULT_AES_KEY_SIZE = 128;
    public static final int DEFAULT_IV_SIZE = 16;
    private static SecureRandom random = new SecureRandom();

    private CryptoUtils() {}

    /**
     * Sign the request
     *
     * @param params     Request parameter
     * @param secret     Signing key
     * @param signMethod signMethod
     * @return signature string
     * @throws IOException              the io exception
     * @throws GeneralSecurityException the general security exception
     */
    public static String signRequest(Map<String, String> params, String secret, String signMethod) throws IOException, GeneralSecurityException {
        return signRequest(params, null, secret, signMethod);
    }

    /**
     * Sign the request
     *
     * @param params     Request parameter
     * @param body       Request body content
     * @param secret     Signing key
     * @param signMethod signMethod
     * @return signature string
     * @throws IOException              the io exception
     * @throws GeneralSecurityException the general security exception
     */
    public static String signRequest(Map<String, String> params, String body, String secret, String signMethod) throws IOException, GeneralSecurityException {
        // 1. check the params if ordered
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        // 2. put all params keys and values together
        StringBuilder query = new StringBuilder();
        for (String key : keys) {
            String value = params.get(key);
            if (StringUtils.areNotEmpty(key, value)) {
                query.append(key).append(value);
            }
        }

        // 3. append post body after the params
        if (body != null) {
            query.append(body);
        }

        // 4. use MD5/HMAC to encrypt
        byte[] bytes;
        if (Constants.SIGN_METHOD_HMAC.equals(signMethod)) {
            bytes = encryptHMAC(query.toString(), secret);
        } else {
            query.append(secret);
            bytes = encryptMD5(query.toString());
        }

        // 5. 把二进制转化为大写的十六进制
        return byte2hex(bytes);
    }

    /**
     * Sign the request
     *
     * @param queryString Request parameter
     * @param body        Request body content
     * @param secret      Signing key
     * @param signMethod  Signature method
     * @return signature string
     * @throws IOException              the io exception
     * @throws GeneralSecurityException the general security exception
     */
    public static String signRequest(String queryString, String body, String secret, String signMethod) throws IOException, GeneralSecurityException {

        // 1. 判断参数是否存在
        StringBuilder query = new StringBuilder();
        if(queryString != null){
            query.append(queryString);
        }

        // 2. 把请求主体拼接在参数后面
        if (body != null) {
            query.append(body);
        }

        // 3. 使用MD5/HMAC加密
        byte[] bytes;
        if (Constants.SIGN_METHOD_HMAC.equals(signMethod)) {
            bytes = encryptHMAC(query.toString(), secret);
        } else {
            query.append(secret);
            bytes = encryptMD5(query.toString());
        }

        // 4. 把二进制转化为大写的十六进制
        return byte2hex(bytes);
    }

    private static byte[] encryptHMAC(String data, String secret) throws GeneralSecurityException, IOException {
        byte[] bytes;
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(Constants.CHARSET_UTF8), "HmacMD5");
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        bytes = mac.doFinal(data.getBytes(Constants.CHARSET_UTF8));
        return bytes;
    }

    /**
     * After the string is encoded in UTF-8, MD5 is used for digest.
     *
     * @param data the data
     * @return the byte [ ]
     * @throws IOException              the io exception
     * @throws GeneralSecurityException the general security exception
     */
    public static byte[] encryptMD5(String data) throws IOException, GeneralSecurityException {
        return encryptMD5(data.getBytes(Constants.CHARSET_UTF8));
    }

    /**
     * MD5 digest the byte stream.
     *
     * @param data the data
     * @return the byte [ ]
     * @throws IOException              the io exception
     * @throws GeneralSecurityException the general security exception
     */
    public static byte[] encryptMD5(byte[] data) throws IOException, GeneralSecurityException {
        byte[] bytes;
        MessageDigest md = MessageDigest.getInstance("MD5");
        bytes = md.digest(data);
        return bytes;
    }

    /**
     * Convert byte stream to hexadecimal representation.
     *
     * @param bytes the bytes
     * @return the string
     */
    public static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

    public static byte charToByte(char paramChar) {
        return (byte) "0123456789ABCDEF".indexOf(paramChar);
    }
}
