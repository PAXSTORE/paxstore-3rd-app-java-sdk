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

import java.io.File;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by zcy on 2016/12/1 0001.
 */
public class FileUtils {
    /**
     * Delete file boolean.
     *
     * @param sPath the s path
     * @return the boolean
     */
    public static boolean deleteFile(String sPath) {
        if (sPath == null || "".equals(sPath.trim())) {
            return false;
        }
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }


    /**
     * Returns a fixed-length random string (only contains uppercase and lowercase letters, numbers)
     *
     * @param length Random string length
     * @return Random string string
     */
    public static String generateMixString(int length) {
        String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
        }

        return sb.toString();
    }


    public static boolean moveToFatherFolder(String pathName) {
        //Check if folder exists, if children exist, if father folder exists
        String endPath = pathName.substring(0, pathName.lastIndexOf(File.separator));
        if (!new File(pathName).exists() || new File(pathName).listFiles().length == 0 ||
                pathName.lastIndexOf(File.separator) == 0 || !new File(endPath).exists()) {
            System.out.println(" moveToFatherFolder >>> Dictionary is not exits, has no child files or has no parent dictionary");
            return false;
        }

        File[] files = new File(pathName).listFiles();
        for (File startFile : files) {
            try {
                if (startFile.renameTo(new File(endPath + startFile.getName()))) {
                } else {
                    System.out.println(startFile.getName() + " >> File is failed to move!");
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        try {
            new File(pathName).delete();
        } catch (Exception e) {
            System.out.println("Delete endPath failed!");
            return false;
        }

        return true;
    }


    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //delete all contents in it
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); //delete empty folder
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//delete the files in it first
                delFolder(path + "/" + tempList[i]);//then delete the folder
                flag = true;
            }
        }
        return flag;
    }
}
