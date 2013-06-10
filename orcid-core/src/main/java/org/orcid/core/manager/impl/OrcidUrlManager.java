/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import org.springframework.beans.factory.annotation.Value;

public class OrcidUrlManager {

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
