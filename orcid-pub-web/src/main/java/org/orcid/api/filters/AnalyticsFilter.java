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
package org.orcid.api.filters;

import javax.ws.rs.ext.Provider;

import org.orcid.core.analytics.AnalyticsProcess;
import org.orcid.core.analytics.client.AnalyticsClient;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.OrcidSecurityManager;

import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

@Provider
public class AnalyticsFilter implements ContainerResponseFilter {

    @InjectParam("orcidSecurityManager")
    private OrcidSecurityManager orcidSecurityManager;

    @InjectParam("analyticsClient")
    private AnalyticsClient analyticsClient;

    @InjectParam("clientDetailsEntityCacheManager")
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    
    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        new Thread(getAnalyticsProcess(request, response)).start();
        return response;
    }
    
    private AnalyticsProcess getAnalyticsProcess(ContainerRequest request, ContainerResponse response) {
        AnalyticsProcess process = new AnalyticsProcess();
        process.setRequest(request);
        process.setResponse(response);
        process.setAnalyticsClient(analyticsClient);
        process.setClientDetailsEntityCacheManager(clientDetailsEntityCacheManager);
        process.setClientDetailsId(orcidSecurityManager.getClientIdFromAPIRequest());
        process.setPublicApi(true);
        return process;
    }

}
