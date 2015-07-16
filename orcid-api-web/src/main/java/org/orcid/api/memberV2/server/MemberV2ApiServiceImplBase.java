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

import static org.orcid.core.api.OrcidApiConstants.ACTIVITIES;
import static org.orcid.core.api.OrcidApiConstants.EDUCATION;
import static org.orcid.core.api.OrcidApiConstants.EDUCATION_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.EMPLOYMENT;
import static org.orcid.core.api.OrcidApiConstants.EMPLOYMENT_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.ERROR;
import static org.orcid.core.api.OrcidApiConstants.FUNDING;
import static org.orcid.core.api.OrcidApiConstants.FUNDING_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.PUTCODE;
import static org.orcid.core.api.OrcidApiConstants.STATUS_PATH;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.WORK;
import static org.orcid.core.api.OrcidApiConstants.WORK_SUMMARY;

import java.net.URI;

import static org.orcid.core.api.OrcidApiConstants.PEER_REVIEW;
import static org.orcid.core.api.OrcidApiConstants.PEER_REVIEW_SUMMARY;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.orcid.api.memberV2.server.delegator.MemberV2ApiServiceDelegator;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Employment;
import org.orcid.jaxb.model.record.Funding;
import org.orcid.jaxb.model.record.PeerReview;
import org.orcid.jaxb.model.record.Work;
import org.orcid.jaxb.model.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary.EducationSummary;
import org.orcid.jaxb.model.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.record.summary.FundingSummary;
import org.orcid.jaxb.model.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary.WorkSummary;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
import io.swagger.annotations.ExternalDocs;
import io.swagger.annotations.ResponseHeader;

/**
 * @author rcpeters
 */

abstract public class MemberV2ApiServiceImplBase {

    public static final String ACTIVITIES_READ_LIMITED_STRING = "/activities/read-limited";
    public static final String ACTIVITIES_UPDATE_STRING = "/activities/update";
    
    @Context
    private UriInfo uriInfo;

    private MemberV2ApiServiceDelegator serviceDelegator;

    public void setServiceDelegator(MemberV2ApiServiceDelegator serviceDelegator) {
        this.serviceDelegator = serviceDelegator;
    }

    /**
     * @return Plain text message indicating health of service
     */
    @GET
    @Produces(value = { MediaType.TEXT_PLAIN })
    @Path(STATUS_PATH)
    @ApiOperation(value = "Check the server status", hidden = true)
    public Response viewStatusText() {
        return serviceDelegator.viewStatusText();
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ACTIVITIES)
    @ApiOperation(value = "Fetch all activities", response = ActivitiesSummary.class, authorizations = { @Authorization(value = "orcid_auth", scopes = {
            @AuthorizationScope(scope = ACTIVITIES_READ_LIMITED_STRING, description = "you need this") }) })
    @ExternalDocs(value = "Activities XML Schema", url = "https://raw.githubusercontent.com/ORCID/ORCID-Source/master/orcid-model/src/main/resources/record_2.0_rc1/activities-2.0_rc1.xsd")
    public Response viewActivities(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewActivities(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORK + PUTCODE)
    @ApiOperation(value = "Fetch a Work", response = Work.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_READ_LIMITED_STRING, description = "you need this") }) })
    public Response viewWork(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewWork(orcid, putCode);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORK_SUMMARY + PUTCODE)
    @ApiOperation(value = "Fetch a Work Summary", response = WorkSummary.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_READ_LIMITED_STRING, description = "you need this") }) })
    public Response viewWorkSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewWorkSummary(orcid, putCode);
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORK)
    @ApiOperation(value = "Create a Work", response = URI.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_UPDATE_STRING, description = "you need this") }) })
    @ApiResponses(value = { 
            @ApiResponse(code = 201, message = "Work created, see HTTP Location header for URI", responseHeaders = @ResponseHeader(name = "Location", description = "The created Education resource", response = URI.class)),
            @ApiResponse(code = 400, message = "Invalid Work representation",response =String.class),
            @ApiResponse(code = 500, message = "Invalid Work representation that wasn't trapped (bad fuzzy date or you tried to add a put code)",response =String.class)})
    public Response createWork(@PathParam("orcid") String orcid, Work work) {
        return serviceDelegator.createWork(orcid, work);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORK + PUTCODE)
    @ApiOperation(value = "Update a Work", response = Work.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_UPDATE_STRING, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Work updated") })
    public Response updateWork(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Work work) {
        return serviceDelegator.updateWork(orcid, putCode, work);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORK + PUTCODE)
    @ApiOperation(value = "Delete a Work", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_UPDATE_STRING, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 204, message = "Work deleted") })
    public Response deleteWork(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteWork(orcid, putCode);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING + PUTCODE)
    @ApiOperation(value = "Fetch a Funding", response = Funding.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_READ_LIMITED_STRING, description = "you need this") }) })
    public Response viewFunding(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewFunding(orcid, putCode);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING_SUMMARY + PUTCODE)
    @ApiOperation(value = "Fetch a Funding Summary", response = FundingSummary.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_READ_LIMITED_STRING, description = "you need this") }) })
    public Response viewFundingSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewFundingSummary(orcid, putCode);
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING)
    @ApiOperation(value = "Create a Funding", response = URI.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_UPDATE_STRING, description = "you need this") }) })
    @ApiResponses(value = { 
            @ApiResponse(code = 201, message = "Funding created, see HTTP Location header for URI", responseHeaders = @ResponseHeader(name = "Location", description = "The created Education resource", response = URI.class)),
            @ApiResponse(code = 400, message = "Invalid Funding representation",response =String.class),
            @ApiResponse(code = 500, message = "Invalid Funding representation that wasn't trapped (bad fuzzy date or you tried to add a put code)",response =String.class)})
    public Response createFunding(@PathParam("orcid") String orcid, Funding funding) {
        return serviceDelegator.createFunding(orcid, funding);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING + PUTCODE)
    @ApiOperation(value = "Update a Funding", response = Funding.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_UPDATE_STRING, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Funding updated") })
    public Response updateFunding(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Funding funding) {
        return serviceDelegator.updateFunding(orcid, putCode, funding);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING + PUTCODE)
    @ApiOperation(value = "Delete a Funding", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_UPDATE_STRING, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 204, message = "Funding deleted") })
    public Response deleteFunding(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteFunding(orcid, putCode);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATION + PUTCODE)
    @ApiOperation(value = "Fetch an Education", authorizations = { @Authorization(value = "orcid_auth", scopes = {
            @AuthorizationScope(scope = ACTIVITIES_READ_LIMITED_STRING, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = Education.class),
            @ApiResponse(code = 404, message = "putCode not found", response = String.class),
            @ApiResponse(code = 400, message = "Invalid putCode or ORCID ID", response = String.class) })
    public Response viewEducation(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewEducation(orcid, putCode);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATION_SUMMARY + PUTCODE)
    @ApiOperation(value = "Fetch an Education summary", response = EducationSummary.class, authorizations = { @Authorization(value = "orcid_auth", scopes = {
            @AuthorizationScope(scope = ACTIVITIES_READ_LIMITED_STRING, description = "you need this") }) })
    public Response viewEducationSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewEducationSummary(orcid, putCode);
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATION)
    @ApiOperation(value = "Create an Education", response = URI.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_UPDATE_STRING, description = "you need this") }) })
    @ApiResponses(value = { 
            @ApiResponse(code = 201, message = "Education created, see HTTP Location header for URI", responseHeaders = @ResponseHeader(name = "Location", description = "The created Education resource", response = URI.class)),
            @ApiResponse(code = 400, message = "Invalid Education representation",response =String.class),
            @ApiResponse(code = 500, message = "Invalid Education representation that wasn't trapped (bad fuzzy date or you tried to add a put code)",response =String.class)})
    public Response createEducation(@PathParam("orcid") String orcid, Education education) {
        return serviceDelegator.createEducation(orcid, education);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATION + PUTCODE)
    @ApiOperation(value = "Update an Education", response = Education.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_UPDATE_STRING, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Education updated") })
    public Response updateEducation(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Education education) {
        return serviceDelegator.updateEducation(orcid, putCode, education);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATION + PUTCODE)
    @ApiOperation(value = "Delete an Education", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_UPDATE_STRING, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 204, message = "Education deleted") })
    public Response deleteEducation(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteAffiliation(orcid, putCode);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENT + PUTCODE)
    @ApiOperation(value = "Fetch an Employment", response = Employment.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_READ_LIMITED_STRING, description = "you need this") }) })
    public Response viewEmployment(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewEmployment(orcid, putCode);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENT_SUMMARY + PUTCODE)
    @ApiOperation(value = "Fetch an Employment Summary", response = EmploymentSummary.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_READ_LIMITED_STRING, description = "you need this") }) })
    public Response viewEmploymentSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewEmploymentSummary(orcid, putCode);
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENT)
    @ApiOperation(value = "Create an Employment", response = URI.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_UPDATE_STRING, description = "you need this") }) })
    @ApiResponses(value = { 
            @ApiResponse(code = 201, message = "Employment created, see HTTP Location header for URI", responseHeaders = @ResponseHeader(name = "Location", description = "The created Education resource", response = URI.class)),
            @ApiResponse(code = 400, message = "Invalid Employment representation",response =String.class),
            @ApiResponse(code = 500, message = "Invalid Employment representation that wasn't trapped (bad fuzzy date or you tried to add a put code)",response =String.class)})
    public Response createEmployment(@PathParam("orcid") String orcid, Employment employment) {
        return serviceDelegator.createEmployment(orcid, employment);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENT + PUTCODE)
    @ApiOperation(value = "Update an Employment", response = Employment.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_UPDATE_STRING, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Employment updated") })
    public Response updateEmployment(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Employment employment) {
        return serviceDelegator.updateEmployment(orcid, putCode, employment);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENT + PUTCODE)
    @ApiOperation(value = "Delete an Employment", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_UPDATE_STRING, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 204, message = "Employment deleted") })
    public Response deleteEmployment(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteAffiliation(orcid, putCode);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEW + PUTCODE)
    @ApiOperation(value = "Fetch a Peer Review", response = PeerReview.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_READ_LIMITED_STRING, description = "you need this") }) })
    public Response viewPeerReview(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewPeerReview(orcid, putCode);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEW_SUMMARY + PUTCODE)
    @ApiOperation(value = "Fetch a Peer Review Summary", response = PeerReviewSummary.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_READ_LIMITED_STRING, description = "you need this") }) })
    public Response viewPeerReviewSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewPeerReviewSummary(orcid, putCode);
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEW)
    @ApiOperation(value = "Create a Peer Review", response = URI.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_UPDATE_STRING, description = "you need this") }) })
    @ApiResponses(value = { 
            @ApiResponse(code = 201, message = "Peer Review created, see HTTP Location header for URI", responseHeaders = @ResponseHeader(name = "Location", description = "The created Education resource", response = URI.class)),
            @ApiResponse(code = 400, message = "Invalid Peer Review representation",response =String.class),
            @ApiResponse(code = 500, message = "Invalid Peer Review representation that wasn't trapped (bad fuzzy date or you tried to add a put code)",response =String.class)})
    public Response createPeerReview(@PathParam("orcid") String orcid, PeerReview peerReview) {
        return serviceDelegator.createPeerReview(orcid, peerReview);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEW + PUTCODE)
    @ApiOperation(value = "Update a Peer Review", response = PeerReview.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_UPDATE_STRING, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Peer Review updated") })
    public Response updatePeerReview(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, PeerReview peerReview) {
        return serviceDelegator.updatePeerReview(orcid, putCode, peerReview);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEW + PUTCODE)
    @ApiOperation(value = "Delete a Peer Review", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ACTIVITIES_UPDATE_STRING, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 204, message = "Peer Review deleted") })
    public Response deletePeerReview(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deletePeerReview(orcid, putCode);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ERROR)
    @ApiOperation(value = "Fetch the most recent error", response = String.class, hidden=true)
    public Response viewError() {
        throw new RuntimeException("Sample Error", new Exception("Sample Exception"));
    }
}
