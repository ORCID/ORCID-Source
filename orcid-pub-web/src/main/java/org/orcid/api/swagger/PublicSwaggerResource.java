package org.orcid.api.swagger;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.orcid.api.common.swagger.SwaggerJSONResource;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

import io.swagger.annotations.Api;
import io.swagger.models.Swagger;
import io.swagger.models.auth.OAuth2Definition;
import io.swagger.util.Json;

/**
 * Resource that serves swagger.json
 * 
 * @author tom
 *
 */
@Path(OrcidApiConstants.SWAGGER_PATH)
@Api(OrcidApiConstants.SWAGGER_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class PublicSwaggerResource extends SwaggerJSONResource {
    @Value("${org.orcid.swagger.tokenendpoint}")
    private String tokenEndPoint;

    /**
     * Scan the classes and add in the OAuth information
     * 
     */
    @Override
    protected synchronized Swagger scan(Application app) {
        // tell swagger to pick up our jaxb annotations
        Json.mapper().registerModule(new JaxbAnnotationModule());
        Swagger s = super.scan(app);

        OAuth2Definition oauthTwoLegs = new OAuth2Definition();
        oauthTwoLegs.application(this.tokenEndPoint);
        oauthTwoLegs.scope(ScopePathType.READ_PUBLIC.value(), "Read Public record");
        s.securityDefinition("orcid_two_legs", oauthTwoLegs);

        return s;
    }
}