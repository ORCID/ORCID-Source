package org.orcid.api.filters;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.orcid.core.analytics.AnalyticsProcess;
import org.orcid.core.analytics.client.AnalyticsClient;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.utils.OrcidRequestUtil;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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
    
    @InjectParam("profileEntityCacheManager")
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @InjectParam("apiAnalyticsTaskExecutor")
    private ThreadPoolTaskExecutor apiAnalyticsTaskExecutor;
    
    @Context
    private HttpServletRequest httpServletRequest;
    
    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        AnalyticsProcess analyticsProcess = getAnalyticsProcess(request, response);
        apiAnalyticsTaskExecutor.execute(analyticsProcess);
        return response;
    }
    
    private AnalyticsProcess getAnalyticsProcess(ContainerRequest request, ContainerResponse response) {
        AnalyticsProcess process = new AnalyticsProcess();
        process.setRequest(request);
        process.setResponse(response);
        process.setAnalyticsClient(analyticsClient);
        process.setClientDetailsEntityCacheManager(clientDetailsEntityCacheManager);
        process.setClientDetailsId(orcidSecurityManager.getClientIdFromAPIRequest());
        process.setPublicApi(false);
        process.setProfileEntityCacheManager(profileEntityCacheManager);
        process.setIp(OrcidRequestUtil.getIpAddress(httpServletRequest));
        process.setScheme(OrcidUrlManager.getscheme(httpServletRequest));
        return process;
    }

}
