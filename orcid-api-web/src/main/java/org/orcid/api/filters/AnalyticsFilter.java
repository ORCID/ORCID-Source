package org.orcid.api.filters;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.orcid.api.common.analytics.AnalyticsProcess;
import org.orcid.api.common.analytics.client.AnalyticsClient;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.utils.OrcidRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;


import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ContainerResponseContext;

@Provider
@Component
public class AnalyticsFilter implements ContainerResponseFilter, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsFilter.class);
    
    @Inject
    private OrcidSecurityManager orcidSecurityManager;

    @Inject
    private AnalyticsClient analyticsClient;

    @Inject
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    
    @Inject
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Inject
    private ThreadPoolTaskExecutor apiAnalyticsTaskExecutor;
    
    @Context
    private HttpServletRequest httpServletRequest;
    
    @Value("${org.orcid.mapi.enableAnalytics:false}")
    private boolean enableMemberAPIAnalytics;
    
    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        if (enableMemberAPIAnalytics) {
            AnalyticsProcess analyticsProcess = getAnalyticsProcess(request, response);
            apiAnalyticsTaskExecutor.execute(analyticsProcess);
        }
        return;
    }
    
    private AnalyticsProcess getAnalyticsProcess(ContainerRequestContext request, ContainerResponseContext response) {
        AnalyticsProcess process = new AnalyticsProcess();
       
        //process.setRequest(request);
        //process.setResponse(response);
        process.setAnalyticsClient(analyticsClient);
        process.setClientDetailsEntityCacheManager(clientDetailsEntityCacheManager);
        process.setClientDetailsId(orcidSecurityManager.getClientIdFromAPIRequest());
        process.setPublicApi(false);
        process.setProfileEntityCacheManager(profileEntityCacheManager);
        process.setIp(OrcidRequestUtil.getIpAddress(httpServletRequest));
        process.setScheme(OrcidUrlManager.getscheme(httpServletRequest));
        return process;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("Is Members Api Analytics filter enabled? " + enableMemberAPIAnalytics);
    }
}
