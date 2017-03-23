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
package org.orcid.core.analytics;

import javax.ws.rs.core.HttpHeaders;

import org.orcid.core.analytics.client.AnalyticsClient;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;

public class AnalyticsProcess implements Runnable {

    private static final String REMOTE_IP_HEADER_NAME = "X-FORWARDED-FOR";
    
    private static final String PUBLIC_API_USER = "Public API user";
    
    private static final String PUBLIC_API = "Public API";
    
    private static final String MEMBER_API = "Member API";

    private ContainerRequest request;

    private ContainerResponse response;

    private AnalyticsClient analyticsClient;

    private String clientDetailsId;
    
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    
    public boolean publicApi;

    @Override
    public void run() {
        AnalyticsData data = getAnalyticsData();
        analyticsClient.sendAnalyticsData(data);
    }
    
    public void setRequest(ContainerRequest request) {
        this.request = request;
    }

    public void setResponse(ContainerResponse response) {
        this.response = response;
    }

    public void setAnalyticsClient(AnalyticsClient analyticsClient) {
        this.analyticsClient = analyticsClient;
    }

    public void setClientDetailsEntityCacheManager(ClientDetailsEntityCacheManager clientDetailsEntityCacheManager) {
        this.clientDetailsEntityCacheManager = clientDetailsEntityCacheManager;
    }

    public void setClientDetailsId(String clientDetailsId) {
        this.clientDetailsId = clientDetailsId;
    }
    
    public void setPublicApi(boolean publicApi) {
        this.publicApi = publicApi;
    }

    private AnalyticsData getAnalyticsData() {
        String ip = request.getHeaderValue(REMOTE_IP_HEADER_NAME);
        APIEndpointParser parser = new APIEndpointParser(request);
                
        AnalyticsData analyticsData = new AnalyticsData();
        analyticsData.setUrl(request.getAbsolutePath().toString());
        analyticsData.setClientDetailsString(getClientDetailsString());
        analyticsData.setClientId(clientDetailsId != null ? clientDetailsId : ip); 
        analyticsData.setContentType(request.getHeaderValue(HttpHeaders.CONTENT_TYPE));
        analyticsData.setUserAgent(request.getHeaderValue(HttpHeaders.USER_AGENT));
        analyticsData.setResponseCode(response.getStatus());
        analyticsData.setIpAddress(ip);
        analyticsData.setCategory(parser.getCategory());
        analyticsData.setApiVersion(getApiString(parser.getApiVersion()));
        analyticsData.setMethod(request.getMethod());
        return analyticsData;
    }

    private String getApiString(String apiVersion) {
        if (publicApi) {
            return PUBLIC_API + " " + apiVersion;
        }
        return MEMBER_API + " " + apiVersion;
    }

    private String getClientDetailsString() {
        if (clientDetailsId != null) {
            ClientDetailsEntity client = clientDetailsEntityCacheManager.retrieve(clientDetailsId);
            StringBuilder clientDetails = new StringBuilder(client.getClientType().value());
            clientDetails.append(" | ");
            clientDetails.append(client.getClientName());
            clientDetails.append(" - ");
            clientDetails.append(clientDetailsId);
            return clientDetails.toString();
        } else {
            return PUBLIC_API_USER;
        }
    }
}
