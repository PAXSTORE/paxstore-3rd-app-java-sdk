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


import com.google.gson.JsonParseException;
import com.pax.market.api.sdk.java.base.client.ThreadLocalProxyAuthenticator;
import com.pax.market.api.sdk.java.base.util.auth.OkHttpAuthenticator;
import com.pax.market.api.sdk.java.base.constant.Constants;
import com.pax.market.api.sdk.java.base.constant.ResultCode;
import com.pax.market.api.sdk.java.base.dto.SdkObject;
import com.pax.market.api.sdk.java.base.request.SdkRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;


/**
 * Network tools.
 */
public abstract class HttpUtils {
	private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

	private static final int BUFFER_SIZE = 4096;
	private static final String DEFAULT_CHARSET = Constants.CHARSET_UTF8;
	private static Locale locale = Locale.ENGLISH;
	public static final String IOEXCTION_FLAG = "IOException-";
	private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();
	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	/**
     * Sets local.
     *
     * @param locale the locale
     */
    public static void setLocal(Locale locale) {
		HttpUtils.locale = locale;
	}

	private HttpUtils() {
	}

	public static String pingHosts(String dynamicHost, String staticHost, Proxy proxy, String basicAuthorization, PasswordAuthentication passwordAuthentication) {
    	return pingHostItem(dynamicHost, proxy, basicAuthorization, passwordAuthentication) != null
				? dynamicHost
				: pingHostItem(staticHost, proxy, basicAuthorization, passwordAuthentication);
	}

	private static String pingHostItem(String host, Proxy proxy, String basicAuthorization, PasswordAuthentication passwordAuthentication) {
		logger.error("Ping host start:");
		HttpLoggingInterceptor mLoggingInterceptor = new HttpLoggingInterceptor();
		mLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
		OkHttpClient.Builder httpClientBuilder = OK_HTTP_CLIENT.newBuilder()
				.addInterceptor(mLoggingInterceptor)
				.retryOnConnectionFailure(false)
				.connectTimeout(5, TimeUnit.SECONDS)
				.readTimeout(5, TimeUnit.SECONDS)
				.writeTimeout(5, TimeUnit.SECONDS);
		boolean clearCredentials = false;
		if (proxy != null) {
			httpClientBuilder.proxy(proxy);
			if (proxy.type() == Proxy.Type.HTTP && basicAuthorization != null) {
				httpClientBuilder.proxyAuthenticator(new OkHttpAuthenticator(basicAuthorization));
			} else if (proxy.type() == Proxy.Type.SOCKS && passwordAuthentication != null) {
				clearCredentials = true;
				java.net.Authenticator.setDefault(ThreadLocalProxyAuthenticator.getInstance());
				ThreadLocalProxyAuthenticator.getInstance().setCredentials(passwordAuthentication);
			} else {
				httpClientBuilder.proxyAuthenticator(okhttp3.Authenticator.NONE);
			}
		}
		try {
			String pingHost = host;
			if (host.endsWith("p-market-api/v1")) {
				pingHost = host.substring(0, host.indexOf("p-market-api/v1"));
			}
			Response execute = httpClientBuilder.build()
					.newCall(new Request.Builder().url(pingHost + "healthcheck/ping").build())
					.execute();
			logger.error("Ping host end:");
			return host;
		} catch (IOException e) {
			logger.error("Ping failed: {}", e.toString());
		} finally {
			if (clearCredentials) {
				ThreadLocalProxyAuthenticator.clearCredentials();
			}
		}
		return null;
	}
	/**
	 * Request string.
	 *
	 * @param requestUrl		    the request url
	 * @param requestMethod    the request method
	 * @param connectTimeout    the connect timeout
	 * @param readTimeout    the read timeout
	 * @param writeTimeout    the write timeout
	 * @param userData    the user data
	 * @param headerMap    the header map
	 * @param saveFilePath    the save file path
	 * @param proxy  the proxy
	 * @param basicAuthorization the basicAuthorization
	 * @param passwordAuthentication the passwordAuthentication
	 * @return  the string
	 */
	public static String request(String requestUrl, SdkRequest.RequestMethod requestMethod, int connectTimeout, int readTimeout, int writeTimeout, String userData, RequestBody multipartBody,
								 Map<String, String> headerMap, String saveFilePath, Proxy proxy, String basicAuthorization, PasswordAuthentication passwordAuthentication) {
		FileOutputStream fileOutputStream = null;
		String filePath = null;
		boolean clearCredentials = false;
		try {
			HttpLoggingInterceptor mLoggingInterceptor = new HttpLoggingInterceptor();
			mLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

			OkHttpClient.Builder httpClientBuilder = OK_HTTP_CLIENT.newBuilder()
					.addInterceptor(mLoggingInterceptor)
					.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
					.readTimeout(readTimeout, TimeUnit.MILLISECONDS)
					.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);

			if (proxy != null) {
				httpClientBuilder.proxy(proxy);
				if (proxy.type() == Proxy.Type.HTTP && basicAuthorization != null) {
					httpClientBuilder.proxyAuthenticator(new OkHttpAuthenticator(basicAuthorization));
				} else if (proxy.type() == Proxy.Type.SOCKS && passwordAuthentication != null) {
					clearCredentials = true;
					java.net.Authenticator.setDefault(ThreadLocalProxyAuthenticator.getInstance());
					ThreadLocalProxyAuthenticator.getInstance().setCredentials(passwordAuthentication);
				} else {
					httpClientBuilder.proxyAuthenticator(okhttp3.Authenticator.NONE);
				}
			}

			Request.Builder requestBuilder = new Request.Builder().url(requestUrl);
			switch (requestMethod) {
				case POST:
					requestBuilder.post(RequestBody.create(JSON, userData));
					break;
				case MULTIPART_POST:
					requestBuilder.post(multipartBody);
					break;
				case PUT:
					requestBuilder.put(RequestBody.create(JSON, userData));
					break;
				case DELETE:
					requestBuilder.delete();
					break;
				default:
					requestBuilder.get();
					break;
			}
			if(locale != null) {
				requestBuilder.addHeader(Constants.ACCESS_LANGUAGE, getLanguageTag(locale));
			}
			if (headerMap != null) {
				for (Entry<String, String> entry : headerMap.entrySet()) {
					requestBuilder.addHeader(entry.getKey(), entry.getValue());
				}
			}
			Response response = httpClientBuilder.build()
					.newCall(requestBuilder.build())
					.execute();

			if (response.isSuccessful()) {
				if(saveFilePath != null) {
                    filePath = saveFilePath + File.separator + FileUtils.generateMixString(16) ;

                    File fileDir = new File(saveFilePath);
                    if(!fileDir.exists()) {
                        fileDir.mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(filePath);

                    int bytesRead;
                    byte[] buffer = new byte[BUFFER_SIZE];
                    while ((bytesRead = response.body().byteStream().read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                    return JsonUtils.getSdkJsonStr(ResultCode.SUCCESS.getCode(), filePath);
                }
			}

			String responseBody = response.body().string();
			try {
                SdkObject sdkObject = JsonUtils.fromJson(responseBody, SdkObject.class);
                if (sdkObject == null) {
                    return JsonUtils.getSdkJsonStr(response.code(), responseBody);
                }
            } catch (IllegalStateException e ) {
                logger.error("IllegalStateException Occurred. Details: {}", e.toString());
                return JsonUtils.getSdkJsonStr(response.code(), responseBody);
            } catch (JsonParseException e1) {
                logger.error("JsonParseException Occurred. Details: {}", e1.toString());
                return JsonUtils.getSdkJsonStr(response.code(), responseBody);
            }
			return responseBody;
		} catch (SocketTimeoutException localSocketTimeoutException) {
			FileUtils.deleteFile(filePath);
			logger.error("SocketTimeoutException Occurred. Details: {}", localSocketTimeoutException.toString());
			return JsonUtils.getSdkJson(ResultCode.SDK_CONNECT_TIMEOUT.getCode(), IOEXCTION_FLAG + localSocketTimeoutException.toString());
		} catch (ConnectException localConnectException) {
			FileUtils.deleteFile(filePath);
			logger.error("ConnectException Occurred. Details: {}", localConnectException.toString());
			return JsonUtils.getSdkJson(ResultCode.SDK_UN_CONNECT.getCode(), IOEXCTION_FLAG + localConnectException.toString());
		} catch (FileNotFoundException fileNotFoundException) {
			FileUtils.deleteFile(filePath);
			logger.error("FileNotFoundException Occurred. Details: {}", fileNotFoundException.toString());
			return JsonUtils.getSdkJson(ResultCode.SDK_FILE_NOT_FOUND.getCode(), fileNotFoundException.toString());
		} catch (Exception ignored) {
			FileUtils.deleteFile(filePath);
			logger.error("Exception Occurred. Details: {}", ignored.toString());
			String errMsg = ignored.toString();
			if (ignored instanceof IOException) {
				errMsg = IOEXCTION_FLAG +ignored.toString();
			}
			return JsonUtils.getSdkJsonStr(ResultCode.SDK_RQUEST_EXCEPTION.getCode(), errMsg);
		} finally {
			if(fileOutputStream != null){
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					logger.error("IOException Occurred. Details: {}", e.toString());
				}
			}
			if (clearCredentials) {
				ThreadLocalProxyAuthenticator.clearCredentials();
			}
		}
	}

    /**
     * Build request url string.
     *
     * @param url     the url
     * @param queries the queries
     * @return the string
     */
    public static String buildRequestUrl(String url, String... queries) {
		if (queries == null || queries.length == 0) {
			return url;
		}

		StringBuilder newUrl = new StringBuilder(url);
		boolean hasQuery = url.contains("?");
		boolean hasPrepend = url.endsWith("?") || url.endsWith("&");

		for (String query : queries) {
			if (!StringUtils.isEmpty(query)) {
				if (!hasPrepend) {
					if (hasQuery) {
						newUrl.append("&");
					} else {
						newUrl.append("?");
						hasQuery = true;
					}
				}
				newUrl.append(query);
				hasPrepend = false;
			}
		}
		return newUrl.toString();
	}

    /**
     * Build query string.
     *
     * @param params  the params
     * @param charset the charset
     * @return the string
     * @throws IOException the io exception
     */
    public static String buildQuery(Map<String, String> params, String charset) throws IOException {
		if (params == null || params.isEmpty()) {
			return null;
		}

		StringBuilder query = new StringBuilder();
		Set<Entry<String, String>> entries = params.entrySet();
		boolean hasParam = false;

		for (Entry<String, String> entry : entries) {
			String name = entry.getKey();
			String value = entry.getValue();
			// 忽略参数名或参数值为空的参数
			if (StringUtils.areNotEmpty(name, value)) {
				if (hasParam) {
					query.append("&");
				} else {
					hasParam = true;
				}

				query.append(name).append("=").append(URLEncoder.encode(value, charset));
			}
		}

		return query.toString();
	}

    /**
     * Gets response as string.
     *
     * @param conn the conn
     * @return the response as string
     * @throws IOException the io exception
     */
    protected static String getResponseAsString(HttpURLConnection conn) throws IOException {
		String charset = getResponseCharset(conn.getContentType());
		if (conn.getResponseCode() < 400) {
			String contentEncoding = conn.getContentEncoding();
			if (Constants.CONTENT_ENCODING_GZIP.equalsIgnoreCase(contentEncoding)) {
				return getStreamAsString(new GZIPInputStream(conn.getInputStream()), charset);
			} else {
				return getStreamAsString(conn.getInputStream(), charset);
			}
		} else {// Client Error 4xx and Server Error 5xx
			throw new IOException(conn.getResponseCode() + " " + conn.getResponseMessage());
		}
	}

    /**
     * Gets stream as string.
     *
     * @param stream  the stream
     * @param charset the charset
     * @return the stream as string
     * @throws IOException the io exception
     */
    public static String getStreamAsString(InputStream stream, String charset) throws IOException {
		try {
			Reader reader = new InputStreamReader(stream, charset);
			StringBuilder response = new StringBuilder();

			final char[] buff = new char[1024];
			int read = 0;
			while ((read = reader.read(buff)) > 0) {
				response.append(buff, 0, read);
			}

			return response.toString();
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

    /**
     * Gets response charset.
     *
     * @param ctype the ctype
     * @return the response charset
     */
    public static String getResponseCharset(String ctype) {
		String charset = DEFAULT_CHARSET;

		if (!StringUtils.isEmpty(ctype)) {
			String[] params = ctype.split(";");
			for (String param : params) {
				param = param.trim();
				if (param.startsWith("charset")) {
					String[] pair = param.split("=", 2);
					if (pair.length == 2) {
						if (!StringUtils.isEmpty(pair[1])) {
							charset = pair[1].trim();
						}
					}
					break;
				}
			}
		}

		return charset;
	}

    /**
     * Use the default UTF-8 character set to reverse encode request parameter values.
     *
     * @param value Parameter value
     * @return Parameter value after de-encoding string
     */
    public static String decode(String value) {
		return decode(value, DEFAULT_CHARSET);
	}

    /**
     * Use the default UTF-8 character set encoding to request parameter values.
     *
     * @param value Parameter value
     * @return Parameter value after encoding string
     */
    public static String encode(String value) {
		return encode(value, DEFAULT_CHARSET);
	}

    /**
     * Use the specified character set to reverse encode the request parameter value.
     *
     * @param value   Parameter value
     * @param charset character set
     * @return Parameter value after de-encoding string
     */
    public static String decode(String value, String charset) {
		String result = null;
		if (!StringUtils.isEmpty(value)) {
			try {
				result = URLDecoder.decode(value, charset);
			} catch (IOException e) {
				logger.error("IOException Occurred. Details: {}", e.toString());
			}
		}
		return result;
	}

    /**
     * Use the specified character set encoding to request parameter values.
     *
     * @param value   Parameter value
     * @param charset character set
     * @return Parameter value after encoding string
     */
    public static String encode(String value, String charset) {
		String result = null;
		if (!StringUtils.isEmpty(value)) {
			try {
				result = URLEncoder.encode(value, charset);
			} catch (IOException e) {
				logger.error("IOException Occurred. Details: {}", e.toString());
			}
		}
		return result;
	}

    /**
     * Extract all parameters from the URL.
     *
     * @param query URL address
     * @return Parameter mapping map
     */
    public static Map<String, String> splitUrlQuery(String query) {
		Map<String, String> result = new HashMap<String, String>();

		String[] pairs = query.split("&");
		if (pairs != null && pairs.length > 0) {
			for (String pair : pairs) {
				String[] param = pair.split("=", 2);
				if (param != null && param.length == 2) {
					result.put(param[0], param[1]);
				}
			}
		}

		return result;
	}

    /**
     * Compress data byte [ ].
     *
     * @param bytes the bytes
     * @return the byte [ ]
     * @throws IOException the io exception
     */
    public static byte[] compressData(byte[] bytes)
			throws IOException {
		if (null == bytes) {
			return null;
		}
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
		gzipOutputStream.write(bytes);
		gzipOutputStream.close();
		return byteArrayOutputStream.toByteArray();
	}

	private static String getLanguageTag(Locale locale) {
		if (locale != null) {
			String localeStr = locale.toString();
			return localeStr.replace("_", "-");
		}
		return null;
	}



}
