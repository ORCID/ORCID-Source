package org.orcid.api.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.orcid.api.common.filter.ApiVersionCheckFilter;
import org.orcid.api.filters.AnalyticsFilter;
import org.springframework.context.annotation.Configuration;

import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class PublicApiResourceConfig extends ResourceConfig {
    
    public PublicApiResourceConfig() {
        packages("org.orcid.api.publicV2.server;org.orcid.api.publicV3.server");
        registerClasses(ApiVersionCheckFilter.class);
        registerClasses(AnalyticsFilter.class);
        property(ServerProperties.WADL_FEATURE_DISABLE, true);
    }

}
