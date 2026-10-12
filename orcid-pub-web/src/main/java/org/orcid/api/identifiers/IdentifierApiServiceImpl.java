package org.orcid.api.identifiers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.glassfish.jersey.message.XmlHeader;
import org.orcid.pojo.IdentifierType;
import org.orcid.api.identifiers.delegator.IdentifierApiServiceDelegator;
import org.orcid.core.api.OrcidApiConstants;
import org.springframework.stereotype.Component;

@Component
@Path("/v{version: 2.0|2.1|3.0}" + OrcidApiConstants.IDENTIFIER_PATH)
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
  @XmlHeader(xmllocation)
  @Operation(summary = "Fetch the external identifier types", tags = "Identifiers",
          description = "The identifier types the registry accepts in `external-id-type`, such as `doi` or `isbn`, with how each one resolves.")
  @ApiResponse(responseCode = "200", description = "Every identifier type", content = @Content(array = @ArraySchema(schema = @Schema(implementation = IdentifierType.class))))
  public Response viewIdentifierTypes(@Parameter(description = "Locale for the descriptions, for example `en` or `fr`. Defaults to `en`.") @QueryParam("locale") String locale) {
      if (locale == null || locale.isEmpty())
          locale = "en";
      return serviceDelegator.getIdentifierTypes(locale);
  }

}