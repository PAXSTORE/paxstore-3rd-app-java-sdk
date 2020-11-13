package com.pax.market.api.sdk.java.base.client;

import java.net.PasswordAuthentication;
import java.net.Proxy;

/**
 * Created by fojut on 2019/1/9.
 */
public interface ProxyDelegate {
    Proxy retrieveProxy();

    String retrieveBasicAuthorization();

    PasswordAuthentication retrievePasswordAuthentication();
}
