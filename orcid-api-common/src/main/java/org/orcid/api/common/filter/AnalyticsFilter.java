package org.orcid.api.common.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.orcid.api.common.analytics.AnalyticsProcess;
import org.orcid.core.analytics.client.AnalyticsClient;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.utils.OrcidRequestUtil;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.sun.jersey.api.core.InjectParam;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

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
    
    private final Boolean isPublicApi;
    
    
    public AnalyticsFilter(Boolean isPublicApi) {
        this.isPublicApi = isPublicApi;
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        AnalyticsProcess analyticsProcess = getAnalyticsProcess(requestContext, responseContext);
        apiAnalyticsTaskExecutor.execute(analyticsProcess);
    }
    
    private AnalyticsProcess getAnalyticsProcess(ContainerRequestContext request, ContainerResponseContext response) {
        AnalyticsProcess process = new AnalyticsProcess();
        process.setRequest(request);
        process.setResponse(response);
        process.setAnalyticsClient(analyticsClient);
        process.setClientDetailsEntityCacheManager(clientDetailsEntityCacheManager);
        process.setClientDetailsId(orcidSecurityManager.getClientIdFromAPIRequest());
        process.setPublicApi(this.isPublicApi);
        process.setProfileEntityCacheManager(profileEntityCacheManager);
        process.setIp(OrcidRequestUtil.getIpAddress(httpServletRequest));
        process.setScheme(OrcidUrlManager.getscheme(httpServletRequest));
        return process;
    }

}
