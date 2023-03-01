package org.orcid.api.config;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

@ApplicationPath("/*")
public class PublicApiResourceConfig extends ResourceConfig {
    
    public PublicApiResourceConfig() {
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("PublicApiResourceConfig");
        System.out.println("---------------------------------------------------------------------------------");
        packages("org.orcid.api.publicV3.server");
        //registerClasses(ApiVersionCheckFilter.class);
        //registerClasses(AnalyticsFilter.class);
        //register(PublicV3ApiServiceImplV3_0.class);
        property(ServerProperties.WADL_FEATURE_DISABLE, true);
    }

}
