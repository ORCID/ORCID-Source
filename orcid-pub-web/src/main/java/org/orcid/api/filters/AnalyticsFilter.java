package org.orcid.api.filters;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;


@Provider
@Component
public class AnalyticsFilter implements ContainerResponseFilter, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsFilter.class);
    
    @Autowired //@Inject @Named("orcidSecurityManager")
    private OrcidSecurityManager orcidSecurityManager;

    @Autowired //@Inject @Named("analyticsClient")
    private AnalyticsClient analyticsClient;

    @Autowired //@Inject @Named("clientDetailsEntityCacheManager")
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    
    @Autowired //@Inject @Named("profileEntityCacheManager")
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Autowired //@Inject @Named("apiAnalyticsTaskExecutor")
    private ThreadPoolTaskExecutor apiAnalyticsTaskExecutor;
    
    @Context
    private HttpServletRequest httpServletRequest;
    
    @Value("${org.orcid.papi.enableAnalytics:false}")
    private boolean enablePublicAPIAnalytics;
    
    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        if (enablePublicAPIAnalytics) {
            AnalyticsProcess analyticsProcess = getAnalyticsProcess(request, response);
            apiAnalyticsTaskExecutor.execute(analyticsProcess);
        }
        return ;
    }
    
    private AnalyticsProcess getAnalyticsProcess(ContainerRequestContext request, ContainerResponseContext response) {
        AnalyticsProcess process = new AnalyticsProcess();
        //process.setRequest(request);
        //process.setResponse(response);
        process.setAnalyticsClient(analyticsClient);
        process.setClientDetailsEntityCacheManager(clientDetailsEntityCacheManager);
        process.setClientDetailsId(orcidSecurityManager.getClientIdFromAPIRequest());
        process.setPublicApi(true);
        process.setProfileEntityCacheManager(profileEntityCacheManager);
        process.setIp(OrcidRequestUtil.getIpAddress(httpServletRequest));
        process.setScheme(OrcidUrlManager.getscheme(httpServletRequest));
        return process;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("Is Public Api Analytics filter enabled? " + enablePublicAPIAnalytics);
    }

}
