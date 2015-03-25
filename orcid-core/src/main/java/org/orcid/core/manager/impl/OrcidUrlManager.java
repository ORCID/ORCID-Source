/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager.impl;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;

public class OrcidUrlManager {

    static String PROTOCALL_PATTREN = "http[s]{0,1}:\\/\\/";

    @Value("${org.orcid.core.baseUri}")
    private String baseUrl;

    @Value("${org.orcid.core.pubBaseUri}")
    private String pubBaseUrl;

    @Value("${org.orcid.core.apiBaseUri}")
    private String apiBaseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseDomainRmProtocall() {
        return getBaseUrl().replaceAll(PROTOCALL_PATTREN, "");
    }

    public String getBaseUriHttp() {
        return this.baseUrl.replace("https", "http");
    }

    public String getBaseHost() {
        try {
            return new URI(this.baseUrl).getHost();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Problem parsing base URI: " + this.baseUrl, e);
        }
    }

    public String getPubBaseUrl() {
        return pubBaseUrl;
    }

    public void setPubBaseUrl(String pubBaseUrl) {
        this.pubBaseUrl = pubBaseUrl;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

}
