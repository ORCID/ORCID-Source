package org.orcid.internal.config;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

@ApplicationPath("/*")
public class InternalApiResourceConfig extends ResourceConfig {
    
    public InternalApiResourceConfig() {
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("InternalApiResourceConfig");
        System.out.println("---------------------------------------------------------------------------------");
        packages("org.orcid.internal.server");
        //registerClasses(ApiVersionCheckFilter.class);
        //registerClasses(AnalyticsFilter.class);
        //register(PublicV3ApiServiceImplV3_0.class);
        property(ServerProperties.WADL_FEATURE_DISABLE, true);
    }

}
