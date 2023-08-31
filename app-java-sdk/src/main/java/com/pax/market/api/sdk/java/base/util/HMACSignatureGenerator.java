package com.pax.market.api.sdk.java.base.util;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class HMACSignatureGenerator {
    private final static Logger logger = LoggerFactory.getLogger(HMACSignatureGenerator.class);

    public static String generateHmacSha256(String content, String secret) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        if (secret == null || secret.isEmpty()) {
            return null;
        }
        return generateHmacSha256(content, secret.getBytes(StandardCharsets.UTF_8));
    }

    public static String generateHmacSha256(String content, byte[] secretBytes) {
        if (secretBytes == null || secretBytes.length <= 0) {
            return null;
        }
        try {
            return byte2Hex(encryptHmac(content, secretBytes, "HmacSHA256"));
        } catch (GeneralSecurityException e) {
            logger.warn("failed to sign content" + e);
            return null;
        }
    }

    private static byte[] encryptHmac(String content, byte[] secret, String algorithm) throws GeneralSecurityException {
        SecretKey secretKey = new SecretKeySpec(secret, algorithm);
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        return mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
    }

    private static String byte2Hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() == 1) {
                sign.append('0');
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

}
