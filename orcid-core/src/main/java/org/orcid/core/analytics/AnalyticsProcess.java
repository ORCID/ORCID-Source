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

import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.PathSegment;

import org.orcid.core.analytics.client.AnalyticsClient;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.springframework.util.StringUtils;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;

public class AnalyticsProcess implements Runnable {

    private static final String REMOTE_IP_HEADER_NAME = "X-FORWARDED-FOR";
    
    private static final String ORCID_PROFILE_CREATE_CATEGORY = "orcid-profile";
    
    private static final String PUBLIC_API_USER = "Public API user";
    
    private static final String PUBLIC_API = "Public";
    
    private static final String MEMBER_API = "Member";

    private static final String RECORD_CATEGORY = "record";

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
                
        AnalyticsData analyticsData = new AnalyticsData();
        analyticsData.setUrl(request.getAbsolutePath().toString());
        analyticsData.setClientDetailsString(getClientDetailsString());
        analyticsData.setClientId(clientDetailsId != null ? clientDetailsId : ip); 
        analyticsData.setContentType(request.getHeaderValue(HttpHeaders.CONTENT_TYPE));
        analyticsData.setUserAgent(request.getHeaderValue(HttpHeaders.USER_AGENT));
        analyticsData.setResponseCode(response.getStatus());
        analyticsData.setIpAddress(ip);
        analyticsData.setCategory(getCategory(request));
        analyticsData.setApiVersion(getApiVersion(request));
        analyticsData.setMethod(request.getMethod());
        return analyticsData;
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

    private String getApiVersion(ContainerRequest request) {
        List<PathSegment> path = request.getPathSegments(true);
        return (publicApi ? PUBLIC_API : MEMBER_API) + " " + path.get(0).toString();
    }

    private String getCategory(ContainerRequest request) {
        if (request.getAbsolutePath().toString().contains(ORCID_PROFILE_CREATE_CATEGORY)) {
            // url inconsistent with others containing ORCID iDs
            return ORCID_PROFILE_CREATE_CATEGORY;
        }

        List<PathSegment> path = request.getPathSegments(true);
        
        if (path.size() < 3 || StringUtils.isEmpty(path.get(2).toString())) {
            return RECORD_CATEGORY;
        }
        return path.get(2).toString();
    }

}
