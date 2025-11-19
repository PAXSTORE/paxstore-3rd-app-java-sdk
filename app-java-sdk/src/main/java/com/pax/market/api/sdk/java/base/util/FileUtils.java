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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

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
        SecureRandom random = new SecureRandom();
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
                if (startFile.renameTo(new File(endPath, startFile.getName()))) {
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


    public static String readFileToString(final File file) throws IOException {
        try (InputStream in = openInputStream(file)) {
            return IOUtil.toString(in, StandardCharsets.UTF_8);
        }
    }

    public static FileInputStream openInputStream(final File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canRead() == false) {
                throw new IOException("File '" + file + "' cannot be read");
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        return new FileInputStream(file);
    }

    /**
     * Writes a String to a file creating the file if it does not exist using the default encoding for the VM.
     *
     * @param file the file to write
     * @param data the content to write to the file
     * @throws IOException in case of an I/O error
     */
    public static void writeStringToFile(final File file, final String data) throws IOException {
        writeStringToFile(file, data, StandardCharsets.UTF_8, false);
    }

    /**
     * Opens a {@link FileOutputStream} for the specified file, checking and
     * creating the parent directory if it does not exist.
     * <p>
     * At the end of the method either the stream will be successfully opened,
     * or an exception will have been thrown.
     * </p>
     * <p>
     * The parent directory will be created if it does not exist.
     * The file will be created if it does not exist.
     * An exception is thrown if the file object exists but is a directory.
     * An exception is thrown if the file exists but cannot be written to.
     * An exception is thrown if the parent directory cannot be created.
     * </p>
     *
     * @param file   the file to open for output, must not be {@code null}
     * @param append if {@code true}, then bytes will be added to the
     *               end of the file rather than overwriting
     * @return a new {@link FileOutputStream} for the specified file
     * @throws IOException if the file object is a directory
     * @throws IOException if the file cannot be written to
     * @throws IOException if a parent directory needs creating but that fails
     * @since 2.1
     */
    public static FileOutputStream openOutputStream(final File file, final boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canWrite() == false) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            final File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }

    /**
     * Writes a String to a file creating the file if it does not exist.
     *
     * @param file     the file to write
     * @param data     the content to write to the file
     * @param charset the charset to use, {@code null} means platform default
     * @param append   if {@code true}, then the String will be added to the
     *                 end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     * @since 2.3
     */
    public static void writeStringToFile(final File file, final String data, final Charset charset, final boolean append)
            throws IOException {
        try (OutputStream out = openOutputStream(file, append)) {
            IOUtil.write(data, out, charset);
        }
    }

}
