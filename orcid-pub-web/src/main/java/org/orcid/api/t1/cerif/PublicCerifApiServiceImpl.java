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
package org.orcid.api.t1.cerif;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.orcid.api.t1.cerif.delegator.PublicCerifApiServiceDelgator;
import org.orcid.core.api.OrcidApiConstants;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Implements the CERIF V1.0 API defined at
 * http://dspacecris.eurocris.org/bitstream/11366/398/3/
 * CERIF_REST_API_Specification_v1.0.pdf
 * 
 * Implements operations 2,3 & 4 (individual entities). Omits 1,5 & 6 (paging
 * and search)
 * 
 * Supports Person, Publication and Product entities.
 * 
 * @author tom
 *
 */
@Api("CERIF API")
@Path(OrcidApiConstants.CERIF_PATH)
public class PublicCerifApiServiceImpl {

    private PublicCerifApiServiceDelgator serviceDelegator;

    public void setServiceDelegator(PublicCerifApiServiceDelgator serviceDelegator) {
        this.serviceDelegator = serviceDelegator;
    }

    /*
     * TODO: implement query params
     * fedIds={true/false}&classifications={true/fal
     * se}&links={true/false/{cerifEntity1;cerifEntit
     * y2;...;cerifEntityN}}&linkedObjects={true | false}&linkedSemantics={true
     * | false}
     */

    @GET
    @Produces(value = { MediaType.APPLICATION_XML })
    @Path("/persons/{id}")
    @ApiOperation(value = "Fetch a person record")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Person found"), @ApiResponse(code = 404, message = "Person not found") })
    public Response viewPerson(@PathParam("id") String id) {
        return serviceDelegator.getPerson(id);
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_XML })
    @Path("/publications/{id}")
    @ApiOperation(value = "Fetch a research publication record")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Publication found"), @ApiResponse(code = 404, message = "Publication not found") })
    public Response viewPublication(@PathParam("id") String id) {
        return serviceDelegator.getPublication(id);
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_XML })
    @Path("/products/{id}")
    @ApiOperation(value = "Fetch a research product record")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Product found"), @ApiResponse(code = 404, message = "Product not found") })
    public Response viewProduct(@PathParam("id") String id) {
        return serviceDelegator.getProduct(id);
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_XML })
    @Path("/entities")
    @ApiOperation(value = "Fetch the list of supported entities")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
    public Response viewEntities() {
        return serviceDelegator.getEntities();
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_XML })
    @Path("/semantics")
    @ApiOperation(value = "Fetch the CERIF semantics")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
    public Response viewSemantics() {
        return serviceDelegator.getSemantics();
    }
}
