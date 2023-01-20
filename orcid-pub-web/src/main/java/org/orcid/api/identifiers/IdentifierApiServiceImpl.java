package org.orcid.api.identifiers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.orcid.api.identifiers.delegator.IdentifierApiServiceDelegator;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.jaxb.model.message.ScopeConstants;



import io.swagger.v3.oas.annotations.Operation;
//TODOSWAGGER
//import com.sun.jersey.api.provider.jaxb.XmlHeader;
/*
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
*/

//@Api("Identifier API")
@Path("/v{version: 2.0|2.1|3.0|3.0_rc1|3.0_rc2}" + OrcidApiConstants.IDENTIFIER_PATH)
public class IdentifierApiServiceImpl {

    public final String xmllocation = "<?xml-stylesheet type=\"text/xsl\" href=\"../static/identifierTypes.xsl\"?>";
    
    private IdentifierApiServiceDelegator serviceDelegator;
    
    public void setServiceDelegator(IdentifierApiServiceDelegator serviceDelegator) {
        this.serviceDelegator = serviceDelegator;
    }
    
    /**
     * @return Available external-id types in the ORCID registry
     */
    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("")
    /*@Operation(description = "Fetch identifier type map. Defaults to English descriptions", authorizations = { @Authorization(value = "orcid_two_legs", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_PUBLIC, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "")})
    @XmlHeader(xmllocation)*/
    //public Response viewIdentifierTypes(@ApiParam() @QueryParam("locale") String locale) {
    public Response viewIdentifierTypes(@QueryParam("locale") String locale) {
        if (locale == null || locale.isEmpty())
            locale = "en";
        return serviceDelegator.getIdentifierTypes(locale);
    }

}