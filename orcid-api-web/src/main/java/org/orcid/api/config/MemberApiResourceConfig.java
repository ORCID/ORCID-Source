package org.orcid.api.config;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

@ApplicationPath("/*")
public class MemberApiResourceConfig extends ResourceConfig {
    
    public MemberApiResourceConfig() {
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("MemberApiResourceConfig");
        System.out.println("---------------------------------------------------------------------------------");
        packages("org.orcid.api.memberV3.server");
        property(ServerProperties.WADL_FEATURE_DISABLE, true);
    }

}
