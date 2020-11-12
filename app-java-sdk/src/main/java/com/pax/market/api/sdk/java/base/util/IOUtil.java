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

import java.io.Closeable;
import java.io.IOException;

/**
 * IO stream tools
 *
 * @author zcy
 */
public class IOUtil {
    private static final Logger logger = LoggerFactory.getLogger(IOUtil.class);

    /**
     * Close one or more stream objects
     *
     * @param closeables List of stream objects that can be closed
     * @throws IOException the io exception
     */
    public static void close(Closeable... closeables) throws IOException {
        if (closeables != null) {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        }
    }

    /**
     * Close one or more stream objects
     *
     * @param closeables List of stream objects that can be closed
     */
    public static void closeQuietly(Closeable... closeables) {
        try {
            close(closeables);
        } catch (IOException e) {
            logger.error("IOException Occurred. Details: {}", e.toString());
        }
    }
 
}