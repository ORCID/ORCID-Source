package org.orcid.api.identifiers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.message.XmlHeader;
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
  public Response viewIdentifierTypes(@QueryParam("locale") String locale) {
      if (locale == null || locale.isEmpty())
          locale = "en";
      return serviceDelegator.getIdentifierTypes(locale);
  }

}