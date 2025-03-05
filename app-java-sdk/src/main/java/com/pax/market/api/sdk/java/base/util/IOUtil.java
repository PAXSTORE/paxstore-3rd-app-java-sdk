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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * IO stream tools
 *
 * @author zcy
 */
public class IOUtil {
    private static final Logger logger = LoggerFactory.getLogger(IOUtil.class);
    /**
     * The default buffer size ({@value}) to use in copy methods.
     */
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    /**
     * Represents the end-of-file (or stream).
     * @since 2.5 (made public)
     */
    public static final int EOF = -1;

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


    public static String toString(final InputStream input, final Charset charset) throws IOException {
        try (final StringBuilderWriter sw = new StringBuilderWriter()) {
            copy(input, sw, charset);
            return sw.toString();
        }
    }

    public static void copy(final InputStream input, final Writer output, final Charset inputCharset)
            throws IOException {
        final InputStreamReader in = new InputStreamReader(input, inputCharset);
        copy(in, output);
    }

    public static int copy(final Reader input, final Writer output) throws IOException {
        final long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    public static long copyLarge(final Reader input, final Writer output) throws IOException {
        return copyLarge(input, output, new char[DEFAULT_BUFFER_SIZE]);
    }

    public static long copyLarge(final Reader input, final Writer output, final char[] buffer) throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }


    public static void write(final String data, final OutputStream output, final Charset charset) throws IOException {
        if (data != null) {
            output.write(data.getBytes(charset));
        }
    }

}