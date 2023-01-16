package org.orcid.api.filters;

import javax.servlet.http.HttpServletRequest;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

//import com.sun.jersey.api.core.InjectParam;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.ContainerResponseContext;

@Provider
@Component
public class AnalyticsFilter implements ContainerResponseFilter, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsFilter.class);
    
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
    
    @Value("${org.orcid.papi.enableAnalytics:false}")
    private boolean enablePublicAPIAnalytics;
    
    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        if (enablePublicAPIAnalytics) {
            AnalyticsProcess analyticsProcess = getAnalyticsProcess(request, response);
            apiAnalyticsTaskExecutor.execute(analyticsProcess);
        }
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
