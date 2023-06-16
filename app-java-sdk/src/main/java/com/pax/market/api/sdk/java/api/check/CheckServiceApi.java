package com.pax.market.api.sdk.java.api.check;

import com.pax.market.api.sdk.java.api.update.UpdateApi;
import com.pax.market.api.sdk.java.base.api.BaseApi;
import com.pax.market.api.sdk.java.base.constant.Constants;
import com.pax.market.api.sdk.java.base.dto.ServiceAvailableObject;
import com.pax.market.api.sdk.java.base.request.SdkRequest;
import com.pax.market.api.sdk.java.base.util.JsonUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckServiceApi  extends BaseApi {


    private final Logger logger = LoggerFactory.getLogger(UpdateApi.class.getSimpleName());

    /**
     * The constant checkServiceUrl.
     */
    protected static final String checkServiceUrl = "v1/3rdApps/service/{serviceType}/usable";

    public CheckServiceApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
        super(baseUrl, appKey, appSecret, terminalSN);
    }

    public void setBaseUrl(String baseUrl) {
        super.getDefaultClient().setBaseUrl(baseUrl);
    }

    public enum ServiceType {

        LAUNCHER_UP("launcherup");

        ServiceType(String value) {
            this.value = value;
        }

        private final String value;

        public String getValue() {
            return this.value;
        }
    }

    /**
     * check if service is subscribed according to the serviceType
     *
     * @param serviceType the type of the service
     * @return service available result
     */
    public ServiceAvailableObject checkServiceAvailable(ServiceType serviceType) {
        String replacedUrl = checkServiceUrl.replace("{serviceType}", serviceType.getValue());
        logger.info("checkServiceUrl >>> " + replacedUrl);
        SdkRequest request = new SdkRequest(replacedUrl);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        return JsonUtils.fromJson(call(request), ServiceAvailableObject.class);
    }

    /**
     * check if Solution service is subscribed according to the serviceType
     *
     * @return service available result
     */
    public ServiceAvailableObject checkSolutionAppAvailable() {
        String replacedUrl = checkServiceUrl.replace("{serviceType}", "industry_solution");
        logger.info("checkServiceUrl >>> " + replacedUrl);
        SdkRequest request = new SdkRequest(replacedUrl);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        return JsonUtils.fromJson(call(request), ServiceAvailableObject.class);
    }
}
