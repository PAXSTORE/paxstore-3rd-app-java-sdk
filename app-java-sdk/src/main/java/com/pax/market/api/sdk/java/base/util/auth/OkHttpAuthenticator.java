package com.pax.market.api.sdk.java.base.util.auth;

import com.pax.market.api.sdk.java.base.constant.Constants;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class OkHttpAuthenticator implements Authenticator {

    private String credential;
    public OkHttpAuthenticator(String credential) {
        this.credential = credential;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        if (response.request().header(Constants.REQ_HEADER_PROXY_AUTHORIZATION) != null) {
            return null;    // Give up, we've already attempted to authenticate.
        }
        return response.request().newBuilder()
                .header(Constants.REQ_HEADER_PROXY_AUTHORIZATION, credential)
                .build();
    }
}
