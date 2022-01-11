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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


/**
 * Compress and decompress files through Java's Zip input and output streams
 *
 * @author zcy
 */
public final class ZipUtil {
    private static final Logger logger = LoggerFactory.getLogger(ZipUtil.class);

    private ZipUtil() {
        // empty
    }

    /**
     * Compressed file
     *
     * @param filePath The path of the file to be compressed
     * @return Compressed file
     */
    public static File zip(String filePath) {
        File target = null;
        File source = new File(filePath);
        if (source.exists()) {
            // compressed fileName=original fileName.zip
            String zipName = source.getName() + ".zip";
            target = new File(source.getParent(), zipName);
            if (target.exists()) {
                target.delete(); // delete old file
            }
            FileOutputStream fos = null;
            ZipOutputStream zos = null;
            try {
                fos = new FileOutputStream(target);
                zos = new ZipOutputStream(new BufferedOutputStream(fos));
                // add correspond file Entry
                addEntry("/", source, zos);
            } catch (IOException e) {
            	logger.error(String.format("error zip file '%s'", filePath), e);
            	return null;
            } finally {
                IOUtil.closeQuietly(zos, fos);
            }
        }
        return target;
    }

    /**
     * Scan to add file Entry
     *
     * @param base   Base path
     * @param source Source File
     * @param zos    Zip file output stream
     * @throws IOException
     */
    private static void addEntry(String base, File source, ZipOutputStream zos)
            throws IOException {
        // Rank by directory，eg:/aaa/bbb.txt
        String entry = base + source.getName();
        if (source.isDirectory()) {
            for (File file : source.listFiles()) {
                // list all files in the folder and add file entry
                addEntry(entry + "/", file, zos);
            }
        } else {
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                byte[] buffer = new byte[1024 * 10];
                fis = new FileInputStream(source);
                bis = new BufferedInputStream(fis, buffer.length);
                int read = 0;
                zos.putNextEntry(new ZipEntry(entry));
                while ((read = bis.read(buffer, 0, buffer.length)) != -1) {
                    zos.write(buffer, 0, read);
                }
                zos.closeEntry();
            } finally {
                IOUtil.closeQuietly(bis, fis);
            }
        }
    }

    /**
     * unzip files
     *
     * @param filePath Compressed file path
     * @return the result
     */
    public static boolean unzip(String filePath) {
        File source = new File(filePath);
        if (source.exists()) {
            ZipInputStream zis = null;
            FileInputStream fis = null;


            try {
                fis = new FileInputStream(source);
                zis = new ZipInputStream(fis);
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null && !entry.isDirectory()) {
                    writeEachFile(source, zis, entry);
                }
                zis.closeEntry();
            } catch (IOException e) {
                logger.error(String.format("error write single file %s", filePath), e);
                return false;
            } finally {
                IOUtil.closeQuietly(zis, fis);
            }
        }

        return true;
    }

    private static void writeEachFile(File source, ZipInputStream zis, ZipEntry entry) {
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            File target = new File(source.getParent(), entry.getName());
            if (!target.getParentFile().exists()) {
                // create father folder
                target.getParentFile().mkdirs();
            }
            // write file
            fos = new FileOutputStream(target);
            bos = new BufferedOutputStream(fos);
            int read = 0;
            byte[] buffer = new byte[1024 * 10];
            while ((read = zis.read(buffer, 0, buffer.length)) != -1) {
                bos.write(buffer, 0, read);
            }
            bos.flush();
        } catch (IOException e) {
            logger.error("error writeEachFile", e);
        } finally {
            IOUtil.closeQuietly(fos, bos);
        }
    }

}
