package org.orcid.api.config;

import java.util.Set;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.orcid.api.common.filter.ApiVersionCheckFilter;
import org.orcid.api.filters.AnalyticsFilter;

import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/*")
public class PublicApiResourceConfig extends ResourceConfig {
    public PublicApiResourceConfig() {
        packages("org.orcid.api.t1;org.orcid.api.client;org.orcid.api.common;org.orcid.api.filter");
        registerClasses(ApiVersionCheckFilter.class);
        registerClasses(AnalyticsFilter.class);
        property(ServerProperties.WADL_FEATURE_DISABLE, true);
    }

}
