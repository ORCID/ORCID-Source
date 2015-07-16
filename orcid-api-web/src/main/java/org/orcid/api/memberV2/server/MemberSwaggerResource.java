/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.api.memberV2.server;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.config.FilterFactory;
import io.swagger.config.Scanner;
import io.swagger.config.ScannerFactory;
import io.swagger.config.SwaggerConfig;
import io.swagger.core.filter.SpecFilter;
import io.swagger.core.filter.SwaggerSpecFilter;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.JaxrsScanner;
import io.swagger.jaxrs.config.ReaderConfigUtils;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import io.swagger.models.Swagger;
import io.swagger.models.auth.ApiKeyAuthDefinition;
import io.swagger.models.auth.In;
import io.swagger.models.auth.OAuth2Definition;
import io.swagger.util.Json;
import io.swagger.util.Yaml;

import org.apache.commons.lang.NotImplementedException;
import org.orcid.api.common.swagger.ORCIDAPIListingResource;
import org.orcid.jaxb.model.message.ScopePathType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Path("/resources")
@Api("/resources")
@Produces(MediaType.APPLICATION_JSON)
public class MemberSwaggerResource extends ORCIDAPIListingResource {
    
    @Value("${org.orcid.swagger.authendpoint}")
    private String authEndPoint;

    @Value("${org.orcid.swagger.tokenendpoint}")
    private String tokenEndPoint;
    

    /** Scan the classes and add in the OAuth information
     * 
     */
    @Override
    protected synchronized Swagger scan(Application app) {
        //tell swagger to pick up our jaxb annotations
        Json.mapper().registerModule(new JaxbAnnotationModule());
        Swagger s = super.scan(app);
        
        OAuth2Definition oauth = new OAuth2Definition();
        oauth.accessCode(this.authEndPoint, this.tokenEndPoint);
        oauth.scope(ScopePathType.ACTIVITIES_READ_LIMITED.value(), "Read activities");
        oauth.scope(ScopePathType.ACTIVITIES_UPDATE.value(), "Update activities");
        s.securityDefinition("orcid_auth",oauth);
        
        /*
        OAuth2Definition oauthTwoLegs = new OAuth2Definition();
        oauthTwoLegs.application(this.tokenEndPoint);
        oauthTwoLegs.scope(ScopePathType.PREMIUM_NOTIFICATION.value(), "Notifications");
        s.securityDefinition("orcid_two_legs",oauthTwoLegs);
        */
        
        //TODO: fix swagger UI to recognize two legged auth flow.
        //or we can put bearer tokens in as implicit params...
        //s.securityDefinition("bearer_token", new ApiKeyAuthDefinition("bearer_token", In.HEADER));        
        return s;
    }
    
}