package org.orcid.api.t1.config;

import java.util.Set;

import org.glassfish.jersey.server.ResourceConfig;

import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/*")
public class ApiResourceConfig extends ResourceConfig {
    public ApiResourceConfig() {
        packages("org.orcid.api.t1;org.orcid.api.client;org.orcid.api.common");
        //property(null, cachedClasses)
    }
    

}
