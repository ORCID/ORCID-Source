package org.orcid.api.memberV2.server;

import static org.orcid.core.api.OrcidApiConstants.ACTIVITIES;
import static org.orcid.core.api.OrcidApiConstants.EDUCATION;
import static org.orcid.core.api.OrcidApiConstants.EDUCATION_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.EMPLOYMENT;
import static org.orcid.core.api.OrcidApiConstants.EMPLOYMENT_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.ERROR;
import static org.orcid.core.api.OrcidApiConstants.FUNDING;
import static org.orcid.core.api.OrcidApiConstants.FUNDING_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.GROUP_ID_RECORD;
import static org.orcid.core.api.OrcidApiConstants.ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.PEER_REVIEW;
import static org.orcid.core.api.OrcidApiConstants.PEER_REVIEW_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.PERMISSIONS_PATH;
import static org.orcid.core.api.OrcidApiConstants.PERMISSIONS_VIEW_PATH;
import static org.orcid.core.api.OrcidApiConstants.PUTCODE;
import static org.orcid.core.api.OrcidApiConstants.STATUS_PATH;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.WORK;
import static org.orcid.core.api.OrcidApiConstants.WORK_SUMMARY;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.orcid.api.common.swagger.SwaggerUIBuilder;
import org.orcid.api.member.MemberApiServiceImplHelper;
import org.orcid.api.memberV2.server.delegator.MemberV2ApiServiceDelegator;
import org.orcid.api.notificationsV2.server.delegator.NotificationsApiServiceDelegator;
import org.orcid.core.exception.OrcidNotificationAlreadyReadException;
import org.orcid.jaxb.model.common_rc1.Contributor;
import org.orcid.jaxb.model.common_rc1.Visibility;
import org.orcid.jaxb.model.groupid_rc1.GroupIdRecord;
import org.orcid.jaxb.model.groupid_rc1.GroupIdRecords;
import org.orcid.jaxb.model.message.ScopeConstants;
import org.orcid.jaxb.model.notification.permission_rc1.NotificationPermission;
import org.orcid.jaxb.model.notification_rc1.Notification;
import org.orcid.jaxb.model.record.summary_rc1.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc1.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc1.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc1.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc1.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc1.WorkSummary;
import org.orcid.jaxb.model.record_rc1.Education;
import org.orcid.jaxb.model.record_rc1.Employment;
import org.orcid.jaxb.model.record_rc1.Funding;
import org.orcid.jaxb.model.record_rc1.FundingContributor;
import org.orcid.jaxb.model.record_rc1.PeerReview;
import org.orcid.jaxb.model.record_rc1.Work;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc3.ResearcherUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
import io.swagger.annotations.ExternalDocs;
import io.swagger.annotations.ResponseHeader;

/**
 * 
 * @author rcpeters
 * 
 */
@Path("/v2.0_rc1")
public class MemberV2ApiServiceImplV2_0_rc1 extends MemberApiServiceImplHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemberV2ApiServiceImplV2_0_rc1.class);

    @Context
    private UriInfo uriInfo;

    @Value("${org.orcid.core.baseUri}")
    protected String baseUri;

    @Value("${org.orcid.core.apiBaseUri}")
    protected String apiBaseUri;
    
    protected MemberV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work, ?, ?, ?> serviceDelegator;

    private NotificationsApiServiceDelegator<NotificationPermission> notificationsServiceDelegator;

    public void setServiceDelegator(
            MemberV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work, ?, ?, ?> serviceDelegator) {
        this.serviceDelegator = serviceDelegator;
    }

    public void setNotificationsServiceDelegator(NotificationsApiServiceDelegator<NotificationPermission> notificationsServiceDelegator) {
        this.notificationsServiceDelegator = notificationsServiceDelegator;
    }

    /**
     * Serves the Swagger UI HTML page
     * 
     * @return a 200 response containing the HTML
     */
    @GET
    @Produces(value = { MediaType.TEXT_HTML })
    @Path("/")
    @ApiOperation(value = "Fetch the HTML swagger UI interface", hidden = true)
    public Response viewSwagger() {
        return new SwaggerUIBuilder().buildSwaggerHTML(baseUri, apiBaseUri, true);
    }

    /**
     * Serves the Swagger UI o2c OAuth page
     * 
     * @return a 200 response containing the HTML
     */
    @GET
    @Produces(value = { MediaType.TEXT_HTML })
    @Path("/o2c.html")
    @ApiOperation(value = "Fetch the swagger OAuth component", hidden = true)
    public Response viewSwaggerO2c() {
        return new SwaggerUIBuilder().buildSwaggerO2CHTML();
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
    @ApiOperation(value = "Fetch all activities", response = ActivitiesSummary.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    @ExternalDocs(value = "Activities XML Schema", url = "https://raw.githubusercontent.com/ORCID/ORCID-Source/master/orcid-model/src/main/resources/record_2.0_rc1/activities-2.0_rc1.xsd")
    public Response viewActivities(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewActivities(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORK + PUTCODE)
    @ApiOperation(value = "Fetch a Work", response = Work.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewWork(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewWork(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORK_SUMMARY + PUTCODE)
    @ApiOperation(value = "Fetch a Work Summary", response = WorkSummary.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewWorkSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewWorkSummary(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORK)
    @ApiOperation(value = "Create a Work", response = URI.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Work created, see HTTP Location header for URI", responseHeaders = @ResponseHeader(name = "Location", description = "The created Education resource", response = URI.class)),
            @ApiResponse(code = 400, message = "Invalid Work representation", response = String.class),
            @ApiResponse(code = 500, message = "Invalid Work representation that wasn't trapped (bad fuzzy date or you tried to add a put code)", response = String.class) })
    public Response createWork(@PathParam("orcid") String orcid, Work work) {
        compareWorkAndCreditNameVisibility(work);
        return serviceDelegator.createWork(orcid, work);
    }
    
    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORK + PUTCODE)
    @ApiOperation(value = "Update a Work", response = Work.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Work updated") })
    public Response updateWork(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Work work) {
        compareWorkAndCreditNameVisibility(work);
        return serviceDelegator.updateWork(orcid, getPutCode(putCode), work);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORK + PUTCODE)
    @ApiOperation(value = "Delete a Work", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 204, message = "Work deleted") })
    public Response deleteWork(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteWork(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING + PUTCODE)
    @ApiOperation(value = "Fetch a Funding", response = Funding.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewFunding(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewFunding(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING_SUMMARY + PUTCODE)
    @ApiOperation(value = "Fetch a Funding Summary", response = FundingSummary.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewFundingSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewFundingSummary(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING)
    @ApiOperation(value = "Create a Funding", response = URI.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Funding created, see HTTP Location header for URI", responseHeaders = @ResponseHeader(name = "Location", description = "The created Education resource", response = URI.class)),
            @ApiResponse(code = 400, message = "Invalid Funding representation", response = String.class),
            @ApiResponse(code = 500, message = "Invalid Funding representation that wasn't trapped (bad fuzzy date or you tried to add a put code)", response = String.class) })
    public Response createFunding(@PathParam("orcid") String orcid, Funding funding) {
        compareFundingAndCreditNameVisibility(funding);
        return serviceDelegator.createFunding(orcid, funding);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING + PUTCODE)
    @ApiOperation(value = "Update a Funding", response = Funding.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Funding updated") })
    public Response updateFunding(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Funding funding) {
        compareFundingAndCreditNameVisibility(funding);
        return serviceDelegator.updateFunding(orcid, getPutCode(putCode), funding);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING + PUTCODE)
    @ApiOperation(value = "Delete a Funding", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 204, message = "Funding deleted") })
    public Response deleteFunding(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteFunding(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATION + PUTCODE)
    @ApiOperation(value = "Fetch an Education", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = Education.class),
            @ApiResponse(code = 404, message = "putCode not found", response = String.class),
            @ApiResponse(code = 400, message = "Invalid putCode or ORCID ID", response = String.class) })
    public Response viewEducation(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewEducation(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATION_SUMMARY + PUTCODE)
    @ApiOperation(value = "Fetch an Education summary", response = EducationSummary.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewEducationSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewEducationSummary(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATION)
    @ApiOperation(value = "Create an Education", response = URI.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Education created, see HTTP Location header for URI", responseHeaders = @ResponseHeader(name = "Location", description = "The created Education resource", response = URI.class)),
            @ApiResponse(code = 400, message = "Invalid Education representation", response = String.class),
            @ApiResponse(code = 500, message = "Invalid Education representation that wasn't trapped (bad fuzzy date or you tried to add a put code)", response = String.class) })
    public Response createEducation(@PathParam("orcid") String orcid, Education education) {
        return serviceDelegator.createEducation(orcid, education);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATION + PUTCODE)
    @ApiOperation(value = "Update an Education", response = Education.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Education updated") })
    public Response updateEducation(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Education education) {
        return serviceDelegator.updateEducation(orcid, getPutCode(putCode), education);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATION + PUTCODE)
    @ApiOperation(value = "Delete an Education", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 204, message = "Education deleted") })
    public Response deleteEducation(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteAffiliation(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENT + PUTCODE)
    @ApiOperation(value = "Fetch an Employment", response = Employment.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewEmployment(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewEmployment(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENT_SUMMARY + PUTCODE)
    @ApiOperation(value = "Fetch an Employment Summary", response = EmploymentSummary.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewEmploymentSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewEmploymentSummary(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENT)
    @ApiOperation(value = "Create an Employment", response = URI.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Employment created, see HTTP Location header for URI", responseHeaders = @ResponseHeader(name = "Location", description = "The created Education resource", response = URI.class)),
            @ApiResponse(code = 400, message = "Invalid Employment representation", response = String.class),
            @ApiResponse(code = 500, message = "Invalid Employment representation that wasn't trapped (bad fuzzy date or you tried to add a put code)", response = String.class) })
    public Response createEmployment(@PathParam("orcid") String orcid, Employment employment) {
        return serviceDelegator.createEmployment(orcid, employment);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENT + PUTCODE)
    @ApiOperation(value = "Update an Employment", response = Employment.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Employment updated") })
    public Response updateEmployment(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Employment employment) {
        return serviceDelegator.updateEmployment(orcid, getPutCode(putCode), employment);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENT + PUTCODE)
    @ApiOperation(value = "Delete an Employment", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 204, message = "Employment deleted") })
    public Response deleteEmployment(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteAffiliation(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEW + PUTCODE)
    @ApiOperation(value = "Fetch a Peer Review", response = PeerReview.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewPeerReview(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewPeerReview(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEW_SUMMARY + PUTCODE)
    @ApiOperation(value = "Fetch a Peer Review Summary", response = PeerReviewSummary.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewPeerReviewSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewPeerReviewSummary(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEW)
    @ApiOperation(value = "Create a Peer Review", response = URI.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Peer Review created, see HTTP Location header for URI", responseHeaders = @ResponseHeader(name = "Location", description = "The created Education resource", response = URI.class)),
            @ApiResponse(code = 400, message = "Invalid Peer Review representation", response = String.class),
            @ApiResponse(code = 500, message = "Invalid Peer Review representation that wasn't trapped (bad fuzzy date or you tried to add a put code)", response = String.class) })
    public Response createPeerReview(@PathParam("orcid") String orcid, PeerReview peerReview) {
        return serviceDelegator.createPeerReview(orcid, peerReview);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEW + PUTCODE)
    @ApiOperation(value = "Update a Peer Review", response = PeerReview.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Peer Review updated") })
    public Response updatePeerReview(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, PeerReview peerReview) {
        return serviceDelegator.updatePeerReview(orcid, getPutCode(putCode), peerReview);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEW + PUTCODE)
    @ApiOperation(value = "Delete a Peer Review", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 204, message = "Peer Review deleted") })
    public Response deletePeerReview(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deletePeerReview(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(GROUP_ID_RECORD + PUTCODE)
    @ApiOperation(value = "Fetch a Group", response = GroupIdRecord.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.GROUP_ID_RECORD_READ, description = "you need this") }) })
    public Response viewGroupIdRecord(@PathParam("putCode") String putCode) {
        return serviceDelegator.viewGroupIdRecord(getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(GROUP_ID_RECORD)
    @ApiOperation(value = "Create a Group", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.GROUP_ID_RECORD_UPDATE, description = "you need this") }) })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Group created, see HTTP Location header for URI", responseHeaders = @ResponseHeader(name = "Location", description = "The created Group resource", response = URI.class)),
            @ApiResponse(code = 400, message = "Invalid Group representation", response = String.class) })
    public Response createGroupIdRecord(GroupIdRecord groupIdRecord) {
        return serviceDelegator.createGroupIdRecord(groupIdRecord);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(GROUP_ID_RECORD + PUTCODE)
    @ApiOperation(value = "Update a Group", response = GroupIdRecord.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.GROUP_ID_RECORD_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Peer Review updated") })
    public Response updateGroupIdRecord(@PathParam("putCode") String putCode, GroupIdRecord groupIdRecord) {
        return serviceDelegator.updateGroupIdRecord(groupIdRecord, getPutCode(putCode));
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(GROUP_ID_RECORD + PUTCODE)
    @ApiOperation(value = "Delete a Group", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.GROUP_ID_RECORD_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 204, message = "Group deleted") })
    public Response deleteGroupIdRecord(@PathParam("putCode") String putCode) {
        return serviceDelegator.deleteGroupIdRecord(getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(GROUP_ID_RECORD)
    @ApiOperation(value = "Fetch Groups", response = GroupIdRecords.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.GROUP_ID_RECORD_READ, description = "you need this") }) })
    public Response viewGroupIdRecords(@QueryParam("page-size") String pageSize, @QueryParam("page") String page) {
        return serviceDelegator.viewGroupIdRecords(pageSize, page);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ERROR)
    @ApiOperation(value = "Fetch the most recent error", response = String.class, hidden = true)
    public Response viewError() {
        throw new RuntimeException("Sample Error", new Exception("Sample Exception"));
    }

    // START NOTIFICATIONS
    // ===================
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PERMISSIONS_PATH)
    @ApiOperation(value = "Fetch all notifications for an ORCID ID", hidden = true, authorizations = {
            @Authorization(value = "orcid_two_legs", scopes = { @AuthorizationScope(scope = ScopeConstants.PREMIUM_NOTIFICATION, description = "you need this") }) })
    public Response viewPermissionNotifications(@PathParam("orcid") String orcid) {
        return notificationsServiceDelegator.findPermissionNotifications(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PERMISSIONS_VIEW_PATH)
    @ApiOperation(value = "Fetch a notification by id", response = Notification.class, authorizations = {
            @Authorization(value = "orcid_two_legs", scopes = { @AuthorizationScope(scope = ScopeConstants.PREMIUM_NOTIFICATION, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Notification found", response = Notification.class),
            @ApiResponse(code = 404, message = "Notification not found", response = String.class),
            @ApiResponse(code = 401, message = "Access denied, this is not your notification", response = String.class) })
    public Response viewPermissionNotification(@PathParam("orcid") String orcid, @PathParam("id") Long id) {
        return notificationsServiceDelegator.findPermissionNotification(orcid, id);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PERMISSIONS_VIEW_PATH)
    @Consumes()
    @ApiOperation(value = "Archive a notification", response = Notification.class, authorizations = {
            @Authorization(value = "orcid_two_legs", scopes = { @AuthorizationScope(scope = ScopeConstants.PREMIUM_NOTIFICATION, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Notification archived", response = Notification.class),
            @ApiResponse(code = 404, message = "Notification not found", response = String.class),
            @ApiResponse(code = 401, message = "Access denied, this is not your notification", response = String.class) })
    public Response flagAsArchivedPermissionNotification(@PathParam("orcid") String orcid, @PathParam("id") Long id) throws OrcidNotificationAlreadyReadException {
        return notificationsServiceDelegator.flagNotificationAsArchived(orcid, id);
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PERMISSIONS_PATH)
    @ApiOperation(value = "Add a notification", response = URI.class, authorizations = {
            @Authorization(value = "orcid_two_legs", scopes = { @AuthorizationScope(scope = ScopeConstants.PREMIUM_NOTIFICATION, description = "you need this") }) })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Notification added, see HTTP Location header for URI", responseHeaders = @ResponseHeader(name = "Location", description = "The created Notification resource", response = URI.class)) })
    public Response addPermissionNotification(@PathParam("orcid") String orcid, NotificationPermission notification) {
        return notificationsServiceDelegator.addPermissionNotification(uriInfo, orcid, notification);
    }

    private void compareFundingAndCreditNameVisibility(Funding funding) {
        Visibility fundingVisibility = funding.getVisibility();
        if (funding.getContributors() != null && funding.getContributors().getContributor() != null) {
            for (FundingContributor contributor : funding.getContributors().getContributor()) {
                if (contributor.getCreditName() != null && contributor.getCreditName().getVisibility() != null
                        && contributor.getCreditName().getVisibility().isMoreRestrictiveThan(fundingVisibility)) {
                    String title = (funding.getTitle() == null || funding.getTitle().getTitle() == null) ? null : funding.getTitle().getTitle().getContent();
                    LOGGER.error("Client posting funding '{}' with visibility ({}) less restrictive than its contributor credit name '{}' ({})",
                            new Object[] { title, fundingVisibility, contributor.getCreditName().getContent(), contributor.getCreditName().getVisibility() });
                }
            }
        }
    }

    private void compareWorkAndCreditNameVisibility(Work work) {
        Visibility workVisibility = work.getVisibility();
        if (work.getWorkContributors() != null && work.getWorkContributors().getContributor() != null) {
            for (Contributor contributor : work.getWorkContributors().getContributor()) {
                if (contributor.getCreditName() != null && contributor.getCreditName().getVisibility() != null
                        && contributor.getCreditName().getVisibility().isMoreRestrictiveThan(workVisibility)) {
                    String title = (work.getWorkTitle() == null || work.getWorkTitle().getTitle() == null) ? null : work.getWorkTitle().getTitle().getContent();
                    LOGGER.error("Client posting work '{}' with visibility ({}) less restrictive than its contributor credit name '{}' ({})",
                            new Object[] { title, workVisibility, contributor.getCreditName().getContent(), contributor.getCreditName().getVisibility() });
                }
            }
        }
    }
}
