package org.orcid.api.swagger;

//TODOSWAGGER
/*
import io.swagger.annotations.Api;
import io.swagger.models.Swagger;
import io.swagger.models.auth.OAuth2Definition;
import io.swagger.util.Json;
*/

import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;

import org.orcid.api.common.swagger.SwaggerJSONResource;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
/**
 * Resource that serves swagger.json
 * 
 * @author tom
 *
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "org.orcid")
@Import({org.springdoc.core.SpringDocConfiguration.class,
         org.springdoc.webmvc.core.SpringDocWebMvcConfiguration.class,
         org.springdoc.webmvc.ui.SwaggerConfig.class,
         org.springdoc.core.SwaggerUiConfigProperties.class,
         org.springdoc.core.SwaggerUiOAuthProperties.class,
         org.springdoc.core.SpringDocConfigProperties.class,
         org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.class})
//@Path(OrcidApiConstants.SWAGGER_PATH)
//@Api(OrcidApiConstants.SWAGGER_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class MemberSwaggerResource extends SwaggerJSONResource {

    @Value("${org.orcid.swagger.authendpoint}")
    private String authEndPoint;

    @Value("${org.orcid.swagger.tokenendpoint}")
    private String tokenEndPoint;

    /**
     * Scan the classes and add in the OAuth information
     * 
     
    @Override
    protected synchronized Swagger scan(Application app) {
        // tell swagger to pick up our jaxb annotations
      /*  Json.mapper().registerModule(new JaxbAnnotationModule());
        Swagger s = super.scan(app);

        OAuth2Definition oauth = new OAuth2Definition();
        oauth.accessCode(this.authEndPoint, this.tokenEndPoint);
        oauth.scope(ScopePathType.READ_LIMITED.value(), "Read Limited record");
        oauth.scope(ScopePathType.PERSON_UPDATE.value(), "Update person");
        oauth.scope(ScopePathType.ACTIVITIES_UPDATE.value(), "Update activities");
        s.securityDefinition("orcid_auth", oauth);

        OAuth2Definition oauthTwoLegs = new OAuth2Definition();
        oauthTwoLegs.application(this.tokenEndPoint);
        oauthTwoLegs.scope(ScopePathType.PREMIUM_NOTIFICATION.value(), "Notifications");
        oauthTwoLegs.scope(ScopePathType.READ_PUBLIC.value(), "Read Public record");
        oauthTwoLegs.scope(ScopePathType.GROUP_ID_RECORD_READ.value(), "Read groups");
        oauthTwoLegs.scope(ScopePathType.GROUP_ID_RECORD_UPDATE.value(), "Update groups");
        s.securityDefinition("orcid_two_legs", oauthTwoLegs);

        return s;
    }
*/
}