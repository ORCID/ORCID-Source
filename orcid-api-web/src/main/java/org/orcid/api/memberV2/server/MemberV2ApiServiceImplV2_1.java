package org.orcid.api.memberV2.server;

import static org.orcid.core.api.OrcidApiConstants.ACTIVITIES;
import static org.orcid.core.api.OrcidApiConstants.ADDRESS;
import static org.orcid.core.api.OrcidApiConstants.BIOGRAPHY;
import static org.orcid.core.api.OrcidApiConstants.BULK_WORKS;
import static org.orcid.core.api.OrcidApiConstants.CLIENT_PATH;
import static org.orcid.core.api.OrcidApiConstants.EDUCATION;
import static org.orcid.core.api.OrcidApiConstants.EDUCATIONS;
import static org.orcid.core.api.OrcidApiConstants.EDUCATION_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.EMAIL;
import static org.orcid.core.api.OrcidApiConstants.EMPLOYMENT;
import static org.orcid.core.api.OrcidApiConstants.EMPLOYMENTS;
import static org.orcid.core.api.OrcidApiConstants.EMPLOYMENT_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.ERROR;
import static org.orcid.core.api.OrcidApiConstants.EXTERNAL_IDENTIFIERS;
import static org.orcid.core.api.OrcidApiConstants.FUNDING;
import static org.orcid.core.api.OrcidApiConstants.FUNDINGS;
import static org.orcid.core.api.OrcidApiConstants.FUNDING_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.GROUP_ID_RECORD;
import static org.orcid.core.api.OrcidApiConstants.KEYWORDS;
import static org.orcid.core.api.OrcidApiConstants.ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.OTHER_NAMES;
import static org.orcid.core.api.OrcidApiConstants.PEER_REVIEW;
import static org.orcid.core.api.OrcidApiConstants.PEER_REVIEWS;
import static org.orcid.core.api.OrcidApiConstants.PEER_REVIEW_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.PERMISSIONS_PATH;
import static org.orcid.core.api.OrcidApiConstants.PERMISSIONS_VIEW_PATH;
import static org.orcid.core.api.OrcidApiConstants.PERSON;
import static org.orcid.core.api.OrcidApiConstants.PERSONAL_DETAILS;
import static org.orcid.core.api.OrcidApiConstants.PUTCODE;
import static org.orcid.core.api.OrcidApiConstants.RESEARCHER_URLS;
import static org.orcid.core.api.OrcidApiConstants.SEARCH_PATH;
import static org.orcid.core.api.OrcidApiConstants.STATUS_PATH;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.WORK;
import static org.orcid.core.api.OrcidApiConstants.WORKS;
import static org.orcid.core.api.OrcidApiConstants.WORK_SUMMARY;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
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
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.core.exception.OrcidNotificationAlreadyReadException;
import org.orcid.jaxb.model.client_v2.ClientSummary;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecords;
import org.orcid.jaxb.model.message.ScopeConstants;
import org.orcid.jaxb.model.notification.permission_v2.NotificationPermission;
import org.orcid.jaxb.model.notification_v2.Notification;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.Educations;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_v2.Employments;
import org.orcid.jaxb.model.record.summary_v2.FundingSummary;
import org.orcid.jaxb.model.record.summary_v2.Fundings;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_v2.PeerReviews;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Addresses;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Keywords;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.Person;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_v2.PersonalDetails;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkBulk;
import org.orcid.jaxb.model.search_v2.Search;
import org.springframework.beans.factory.annotation.Value;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
import io.swagger.annotations.ExternalDocs;
import io.swagger.annotations.ResponseHeader;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@Api("Member API v2.1")
@Path("/v2.1")
public class MemberV2ApiServiceImplV2_1 extends MemberApiServiceImplHelper {

    @Context
    private UriInfo uriInfo;

    @Value("${org.orcid.core.baseUri}")
    protected String baseUri;

    @Value("${org.orcid.core.apiBaseUri}")
    protected String apiBaseUri;
    
    protected MemberV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work, WorkBulk, Address, Keyword> serviceDelegator;

    private NotificationsApiServiceDelegator<NotificationPermission> notificationsServiceDelegator;

    public void setServiceDelegator(
            MemberV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work, WorkBulk, Address, Keyword> serviceDelegator) {
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
    @ApiOperation( nickname="viewSwaggerV21", value = "Fetch the HTML swagger UI interface", hidden = true)
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
    @ApiOperation( nickname="viewSwaggerO2cV21", value = "Fetch the swagger OAuth component", hidden = true)
    public Response viewSwaggerO2c() {
        return new SwaggerUIBuilder().buildSwaggerO2CHTML();
    }

    /**
     * @return Plain text message indicating health of service
     */
    @GET
    @Produces(value = { MediaType.TEXT_PLAIN })
    @Path(STATUS_PATH)
    @ApiOperation( nickname="viewStatusTextV21", value = "Check the server status", hidden = true)
    public Response viewStatusText() {
        return serviceDelegator.viewStatusText();
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ACTIVITIES)
    @ApiOperation( nickname="viewActivitiesV21", value = "Fetch all activities", response = ActivitiesSummary.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    @ExternalDocs(value = "Activities XML Schema", url = "https://raw.githubusercontent.com/ORCID/ORCID-Source/master/orcid-model/src/main/resources/record_2.0/activities-2.0.xsd")
    public Response viewActivities(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewActivities(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORK + PUTCODE)
    @ApiOperation( nickname="viewWorkV21", value = "Fetch a Work", response = Work.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewWork(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewWork(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORKS)
    @ApiOperation( nickname="viewWorksV21", value = "Fetch all works", response = Works.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewWorks(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewWorks(orcid);
    }
    
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(BULK_WORKS)
    @ApiOperation( nickname="viewSpecifiedWorksV21", value = "Fetch specified works", response = WorkBulk.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewSpecifiedWorks(@PathParam("orcid") String orcid, @PathParam("putCodes") String putCodes) {
        return serviceDelegator.viewBulkWorks(orcid, putCodes);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORK_SUMMARY + PUTCODE)
    @ApiOperation( nickname="viewWorkSummaryV21", value = "Fetch a Work Summary", response = WorkSummary.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewWorkSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewWorkSummary(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORK)
    @ApiOperation( nickname="createWorkV21", value = "Create a Work", response = URI.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Work created, see HTTP Location header for URI", responseHeaders = @ResponseHeader(name = "Location", description = "The created Education resource", response = URI.class)),
            @ApiResponse(code = 400, message = "Invalid Work representation", response = String.class),
            @ApiResponse(code = 500, message = "Invalid Work representation that wasn't trapped (bad fuzzy date or you tried to add a put code)", response = String.class) })
    public Response createWork(@PathParam("orcid") String orcid, Work work) {
        return serviceDelegator.createWork(orcid, work);
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORKS)
    @ApiOperation( nickname="createWorksV21", value = "Create a list of Work", response = URI.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "At least one of the works was created", responseHeaders = @ResponseHeader(name = "Location", description = "The created Education resource", response = URI.class)),
            @ApiResponse(code = 400, message = "Invalid Work representation", response = String.class),
            @ApiResponse(code = 500, message = "Invalid Work representation that wasn't trapped (bad fuzzy date or you tried to add a put code)", response = String.class) })
    public Response createWorks(@PathParam("orcid") String orcid, WorkBulk works) {
        return serviceDelegator.createWorks(orcid, works);
    }
    
    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORK + PUTCODE)
    @ApiOperation( nickname="updateWorkV21", value = "Update a Work", response = Work.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Work updated") })
    public Response updateWork(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Work work) {
        return serviceDelegator.updateWork(orcid, getPutCode(putCode), work);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORK + PUTCODE)
    @ApiOperation( nickname="deleteWorkV21", value = "Delete a Work", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 204, message = "Work deleted") })
    public Response deleteWork(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteWork(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING + PUTCODE)
    @ApiOperation( nickname="viewFundingV21", value = "Fetch a Funding", response = Funding.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewFunding(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewFunding(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDINGS)
    @ApiOperation( nickname="viewFundingsV21", value = "Fetch all fundings", response = Fundings.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewFundings(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewFundings(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING_SUMMARY + PUTCODE)
    @ApiOperation( nickname="viewFundingSummaryV21", value = "Fetch a Funding Summary", response = FundingSummary.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewFundingSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewFundingSummary(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING)
    @ApiOperation( nickname="createFundingV21", value = "Create a Funding", response = URI.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Funding created, see HTTP Location header for URI", responseHeaders = @ResponseHeader(name = "Location", description = "The created Education resource", response = URI.class)),
            @ApiResponse(code = 400, message = "Invalid Funding representation", response = String.class),
            @ApiResponse(code = 500, message = "Invalid Funding representation that wasn't trapped (bad fuzzy date or you tried to add a put code)", response = String.class) })
    public Response createFunding(@PathParam("orcid") String orcid, Funding funding) {
        return serviceDelegator.createFunding(orcid, funding);
    }
    
    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING + PUTCODE)
    @ApiOperation( nickname="updateFundingV21", value = "Update a Funding", response = Funding.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Funding updated") })
    public Response updateFunding(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Funding funding) {
        return serviceDelegator.updateFunding(orcid, getPutCode(putCode), funding);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING + PUTCODE)
    @ApiOperation( nickname="deleteFundingV21", value = "Delete a Funding", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 204, message = "Funding deleted") })
    public Response deleteFunding(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteFunding(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATION + PUTCODE)
    @ApiOperation( nickname="viewEducationV21", value = "Fetch an Education", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = Education.class),
            @ApiResponse(code = 404, message = "putCode not found", response = String.class),
            @ApiResponse(code = 400, message = "Invalid putCode or ORCID ID", response = String.class) })
    public Response viewEducation(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewEducation(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATIONS)
    @ApiOperation( nickname="viewEducationsV21", value = "Fetch all educations", response = Educations.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewEducations(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewEducations(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATION_SUMMARY + PUTCODE)
    @ApiOperation( nickname="viewEducationSummaryV21", value = "Fetch an Education summary", response = EducationSummary.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewEducationSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewEducationSummary(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATION)
    @ApiOperation( nickname="createEducationV21", value = "Create an Education", response = URI.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
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
    @ApiOperation( nickname="updateEducationV21", value = "Update an Education", response = Education.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Education updated") })
    public Response updateEducation(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Education education) {
        return serviceDelegator.updateEducation(orcid, getPutCode(putCode), education);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATION + PUTCODE)
    @ApiOperation( nickname="deleteEducationV21", value = "Delete an Education", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 204, message = "Education deleted") })
    public Response deleteEducation(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteAffiliation(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENT + PUTCODE)
    @ApiOperation( nickname="viewEmploymentV21", value = "Fetch an Employment", response = Employment.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewEmployment(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewEmployment(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENTS)
    @ApiOperation( nickname="viewEmploymentsV21", value = "Fetch all employments", response = Employments.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewEmployments(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewEmployments(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENT_SUMMARY + PUTCODE)
    @ApiOperation( nickname="viewEmploymentSummaryV21", value = "Fetch an Employment Summary", response = EmploymentSummary.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewEmploymentSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewEmploymentSummary(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENT)
    @ApiOperation( nickname="createEmploymentV21", value = "Create an Employment", response = URI.class, authorizations = {
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
    @ApiOperation( nickname="updateEmploymentV21", value = "Update an Employment", response = Employment.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Employment updated") })
    public Response updateEmployment(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Employment employment) {
        return serviceDelegator.updateEmployment(orcid, getPutCode(putCode), employment);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENT + PUTCODE)
    @ApiOperation( nickname="deleteEmploymentV21", value = "Delete an Employment", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 204, message = "Employment deleted") })
    public Response deleteEmployment(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteAffiliation(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEW + PUTCODE)
    @ApiOperation( nickname="viewPeerReviewV21", value = "Fetch a Peer Review", response = PeerReview.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewPeerReview(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewPeerReview(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEWS)
    @ApiOperation( nickname="viewPeerReviewsV21", value = "Fetch all peer reviews", response = PeerReviews.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewPeerReviews(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewPeerReviews(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEW_SUMMARY + PUTCODE)
    @ApiOperation( nickname="viewPeerReviewSummaryV21", value = "Fetch a Peer Review Summary", response = PeerReviewSummary.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewPeerReviewSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewPeerReviewSummary(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEW)
    @ApiOperation( nickname="createPeerReviewV21", value = "Create a Peer Review", response = URI.class, authorizations = {
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
    @ApiOperation( nickname="updatePeerReviewV21", value = "Update a Peer Review", response = PeerReview.class, authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Peer Review updated") })
    public Response updatePeerReview(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, PeerReview peerReview) {
        return serviceDelegator.updatePeerReview(orcid, getPutCode(putCode), peerReview);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEW + PUTCODE)
    @ApiOperation( nickname="deletePeerReviewV21", value = "Delete a Peer Review", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.ACTIVITIES_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 204, message = "Peer Review deleted") })
    public Response deletePeerReview(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deletePeerReview(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(GROUP_ID_RECORD + PUTCODE)
    @ApiOperation( nickname="viewGroupIdRecordV21", value = "Fetch a Group", response = GroupIdRecord.class, authorizations = {
            @Authorization(value = "orcid_two_legs", scopes = { @AuthorizationScope(scope = ScopeConstants.GROUP_ID_RECORD_READ, description = "you need this") }) })
    public Response viewGroupIdRecord(@PathParam("putCode") String putCode) {
        return serviceDelegator.viewGroupIdRecord(getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(GROUP_ID_RECORD)
    @ApiOperation( nickname="createGroupIdRecordV21", value = "Create a Group", authorizations = {
            @Authorization(value = "orcid_two_legs", scopes = { @AuthorizationScope(scope = ScopeConstants.GROUP_ID_RECORD_UPDATE, description = "you need this") }) })
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
    @ApiOperation( nickname="updateGroupIdRecordV21", value = "Update a Group", response = GroupIdRecord.class, authorizations = {
            @Authorization(value = "orcid_two_legs", scopes = { @AuthorizationScope(scope = ScopeConstants.GROUP_ID_RECORD_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Peer Review updated") })
    public Response updateGroupIdRecord(@PathParam("putCode") String putCode, GroupIdRecord groupIdRecord) {
        return serviceDelegator.updateGroupIdRecord(groupIdRecord, getPutCode(putCode));
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(GROUP_ID_RECORD + PUTCODE)
    @ApiOperation( nickname="deleteGroupIdRecordV21", value = "Delete a Group", authorizations = {
            @Authorization(value = "orcid_two_legs", scopes = { @AuthorizationScope(scope = ScopeConstants.GROUP_ID_RECORD_UPDATE, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 204, message = "Group deleted") })
    public Response deleteGroupIdRecord(@PathParam("putCode") String putCode) {
        return serviceDelegator.deleteGroupIdRecord(getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(GROUP_ID_RECORD)
    @ApiOperation( nickname="viewGroupIdRecordsV21", value = "Fetch Groups", response = GroupIdRecords.class, authorizations = {
            @Authorization(value = "orcid_two_legs", scopes = { @AuthorizationScope(scope = ScopeConstants.GROUP_ID_RECORD_READ, description = "you need this") }) })
    public Response viewGroupIdRecords(@QueryParam("page-size") @DefaultValue("100") String pageSize, @QueryParam("page") @DefaultValue("1") String page,
            @QueryParam("name") String name, @QueryParam("group-id") String groupId) {
        if (groupId != null) {
            return serviceDelegator.findGroupIdRecordByGroupId(groupId);
        }
        if (name != null) {
            return serviceDelegator.findGroupIdRecordByName(name);
        }
        return serviceDelegator.viewGroupIdRecords(pageSize, page);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ERROR)
    @ApiOperation( nickname="viewErrorV21", value = "Fetch the most recent error", response = String.class, hidden = true)
    public Response viewError() {
        throw new RuntimeException("Sample Error", new Exception("Sample Exception"));
    }

    // START NOTIFICATIONS
    // ===================
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PERMISSIONS_PATH)
    @ApiOperation( nickname="viewPermissionNotificationsV21", value = "Fetch all notifications for an ORCID ID", hidden = true, authorizations = {
            @Authorization(value = "orcid_two_legs", scopes = { @AuthorizationScope(scope = ScopeConstants.PREMIUM_NOTIFICATION, description = "you need this") }) })
    public Response viewPermissionNotifications(@PathParam("orcid") String orcid) {
        return notificationsServiceDelegator.findPermissionNotifications(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PERMISSIONS_VIEW_PATH)
    @ApiOperation( nickname="viewPermissionNotificationV21", value = "Fetch a notification by id", response = Notification.class, authorizations = {
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
    @ApiOperation( nickname="flagAsArchivedPermissionNotificationV21", value = "Archive a notification", response = Notification.class, authorizations = {
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
    @ApiOperation( nickname="addPermissionNotificationV21", value = "Add a notification", response = URI.class, authorizations = {
            @Authorization(value = "orcid_two_legs", scopes = { @AuthorizationScope(scope = ScopeConstants.PREMIUM_NOTIFICATION, description = "you need this") }) })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Notification added, see HTTP Location header for URI", responseHeaders = @ResponseHeader(name = "Location", description = "The created Notification resource", response = URI.class)) })
    public Response addPermissionNotification(@PathParam("orcid") String orcid, NotificationPermission notification) {
        return notificationsServiceDelegator.addPermissionNotification(uriInfo, orcid, notification);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(RESEARCHER_URLS)
    @ApiOperation( nickname="viewResearcherUrlsV21", response = ResearcherUrls.class, value = "Fetch all researcher urls for an ORCID ID", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewResearcherUrls(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewResearcherUrls(orcid);
    }

    @GET
    @Path(RESEARCHER_URLS + PUTCODE)
    @ApiOperation( nickname="viewResearcherUrlV21", response = ResearcherUrl.class, value = "Fetch one researcher url for an ORCID ID", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewResearcherUrl(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewResearcherUrl(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(RESEARCHER_URLS)
    @ApiOperation( nickname="createResearcherUrlV21", value = "Add a new researcher url for an ORCID ID", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.PERSON_UPDATE, description = "you need this") }) })
    public Response createResearcherUrl(@PathParam("orcid") String orcid, ResearcherUrl researcherUrl) {
        return serviceDelegator.createResearcherUrl(orcid, researcherUrl);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(RESEARCHER_URLS + PUTCODE)
    @ApiOperation( nickname="editResearcherUrlV21", value = "Edits researcher url for an ORCID ID", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.PERSON_UPDATE, description = "you need this") }) })
    public Response editResearcherUrl(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, ResearcherUrl researcherUrl) {
        return serviceDelegator.updateResearcherUrl(orcid, getPutCode(putCode), researcherUrl);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(RESEARCHER_URLS + PUTCODE)
    @ApiOperation( nickname="deleteResearcherUrlV21", value = "Delete one researcher url from an ORCID ID", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.PERSON_UPDATE, description = "you need this") }) })
    public Response deleteResearcherUrl(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteResearcherUrl(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMAIL)
    @ApiOperation( nickname="viewEmailsV21", response = Emails.class, value = "Fetch all emails for an ORCID ID", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewEmails(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewEmails(orcid);
    }

    // Other names
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(OTHER_NAMES + PUTCODE)
    @ApiOperation( nickname="viewOtherNameV21", response = OtherName.class, value = "Fetch Other name", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewOtherName(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewOtherName(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(OTHER_NAMES)
    @ApiOperation( nickname="viewOtherNamesV21", response = OtherName.class, value = "Fetch Other names", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewOtherNames(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewOtherNames(orcid);
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(OTHER_NAMES)
    @ApiOperation( nickname="createOtherNameV21", value = "Add other name", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.PERSON_UPDATE, description = "you need this") }) })
    public Response createOtherName(@PathParam("orcid") String orcid, OtherName otherName) {
        return serviceDelegator.createOtherName(orcid, otherName);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(OTHER_NAMES + PUTCODE)
    @ApiOperation( nickname="editOtherNameV21", value = "Edit other name", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.PERSON_UPDATE, description = "you need this") }) })
    public Response editOtherName(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, OtherName otherName) {
        return serviceDelegator.updateOtherName(orcid, getPutCode(putCode), otherName);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(OTHER_NAMES + PUTCODE)
    @ApiOperation( nickname="deleteOtherNameV21", value = "Delete other name", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.PERSON_UPDATE, description = "you need this") }) })
    public Response deleteOtherName(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteOtherName(orcid, getPutCode(putCode));
    }

    // Personal details
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PERSONAL_DETAILS)
    @ApiOperation( nickname="viewPersonalDetailsV21", response = PersonalDetails.class, value = "Fetch personal details for an ORCID ID", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewPersonalDetails(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewPersonalDetails(orcid);
    }

    // External Identifiers
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EXTERNAL_IDENTIFIERS + PUTCODE)
    @ApiOperation( nickname="viewExternalIdentifierV21", response = PersonExternalIdentifier.class, value = "Fetch external identifier", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewExternalIdentifier(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewExternalIdentifier(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EXTERNAL_IDENTIFIERS)
    @ApiOperation( nickname="viewExternalIdentifiersV21", response = PersonExternalIdentifiers.class, value = "Fetch external identifiers", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewExternalIdentifiers(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewExternalIdentifiers(orcid);
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EXTERNAL_IDENTIFIERS)
    @ApiOperation( nickname="createExternalIdentifierV21", value = "Add external identifier", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.PERSON_UPDATE, description = "you need this") }) })
    public Response createExternalIdentifier(@PathParam("orcid") String orcid, PersonExternalIdentifier externalIdentifier) {
        return serviceDelegator.createExternalIdentifier(orcid, externalIdentifier);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EXTERNAL_IDENTIFIERS + PUTCODE)
    @ApiOperation( nickname="editExternalIdentifierV21", value = "Edit external identifier", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.PERSON_UPDATE, description = "you need this") }) })
    public Response editExternalIdentifier(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, PersonExternalIdentifier externalIdentifier) {
        return serviceDelegator.updateExternalIdentifier(orcid, getPutCode(putCode), externalIdentifier);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EXTERNAL_IDENTIFIERS + PUTCODE)
    @ApiOperation( nickname="deleteExternalIdentifierV21", value = "Delete external identifier", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.PERSON_UPDATE, description = "you need this") }) })
    public Response deleteExternalIdentifier(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteExternalIdentifier(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(BIOGRAPHY)
    @ApiOperation( nickname="viewBiographyV21", value = "Get biography details", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewBiography(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewBiography(orcid);
    }

    // Keywords
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(KEYWORDS + PUTCODE)
    @ApiOperation( nickname="viewKeywordV21", response = Keyword.class, value = "Fetch keyword", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewKeyword(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewKeyword(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(KEYWORDS)
    @ApiOperation( nickname="viewKeywordsV21", response = Keywords.class, value = "Fetch keywords", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewKeywords(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewKeywords(orcid);
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(KEYWORDS)
    @ApiOperation( nickname="createKeywordV21", value = "Add keyword", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.PERSON_UPDATE, description = "you need this") }) })
    public Response createKeyword(@PathParam("orcid") String orcid, Keyword keyword) {
        return serviceDelegator.createKeyword(orcid, keyword);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(KEYWORDS + PUTCODE)
    @ApiOperation( nickname="editKeywordV21", value = "Edit keyword", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.PERSON_UPDATE, description = "you need this") }) })
    public Response editKeyword(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Keyword keyword) {
        return serviceDelegator.updateKeyword(orcid, getPutCode(putCode), keyword);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(KEYWORDS + PUTCODE)
    @ApiOperation( nickname="deleteKeywordV21", value = "Delete keyword", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.PERSON_UPDATE, description = "you need this") }) })
    public Response deleteKeyword(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteKeyword(orcid, getPutCode(putCode));
    }

    // Address
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ADDRESS + PUTCODE)
    @ApiOperation( nickname="viewAddressV21", response = Address.class, value = "Fetch an address", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewAddress(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewAddress(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ADDRESS)
    @ApiOperation( nickname="viewAddressesV21", response = Addresses.class, value = "Fetch all addresses of a profile", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewAddresses(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewAddresses(orcid);
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ADDRESS)
    @ApiOperation( nickname="createAddressV21", value = "Add an address", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.PERSON_UPDATE, description = "you need this") }) })
    public Response createAddress(@PathParam("orcid") String orcid, Address address) {
        return serviceDelegator.createAddress(orcid, address);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ADDRESS + PUTCODE)
    @ApiOperation( nickname="editAddressV21", value = "Edit an address", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.PERSON_UPDATE, description = "you need this") }) })
    public Response editAddress(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Address address) {
        return serviceDelegator.updateAddress(orcid, getPutCode(putCode), address);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ADDRESS + PUTCODE)
    @ApiOperation( nickname="deleteAddressV21", value = "Delete an address", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.PERSON_UPDATE, description = "you need this") }) })
    public Response deleteAddress(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteAddress(orcid, getPutCode(putCode));
    }

    // Person
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PERSON)
    @ApiOperation( nickname="viewPersonV21", response = Person.class, value = "Fetch person details", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })
    public Response viewPerson(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewPerson(orcid);
    }

    // Record
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(OrcidApiConstants.RECORD_SIMPLE)
    @ApiOperation( nickname="viewRecordV21", response = Record.class, value = "Fetch record details", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })   
    @ExternalDocs(value = "Record XML Schema", url = "https://raw.githubusercontent.com/ORCID/ORCID-Source/master/orcid-model/src/main/resources/record_2.0/record-2.0.xsd")
    public Response viewRecord(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewRecord(orcid);
    }
    
    // Record
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(OrcidApiConstants.RECORD_RECORD)
    @ApiOperation( hidden=true,nickname="viewRecordRecordV21", value = "Fetch record details", authorizations = {
            @Authorization(value = "orcid_auth", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_LIMITED, description = "you need this") }) })   
    @ExternalDocs(value = "Record XML Schema", url = "https://raw.githubusercontent.com/ORCID/ORCID-Source/master/orcid-model/src/main/resources/record_2.0/record-2.0.xsd")
    public Response viewRecordRecord(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewRecord(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(SEARCH_PATH)
    @ApiOperation( nickname="searchByQueryV21", response = Search.class, value = "Search records", authorizations = {
            @Authorization(value = "orcid_two_legs", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_PUBLIC, description = "you need this") }) })
    @ExternalDocs(value = "Record XML Schema", url = "https://raw.githubusercontent.com/ORCID/ORCID-Source/master/orcid-model/src/main/resources/record_2.0/search-2.0.xsd")
    public Response searchByQuery(@QueryParam("q") @DefaultValue("") String query, @Context UriInfo uriInfo) {
        Map<String, List<String>> solrParams = uriInfo.getQueryParameters();
        Response xmlQueryResults = serviceDelegator.searchByQuery(solrParams);
        return xmlQueryResults;
    }

    @GET
    @Path(CLIENT_PATH)
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @ApiOperation( nickname="viewClientV21", response = ClientSummary.class, value = "Fetch client details", authorizations = {
            @Authorization(value = "orcid_two_legs", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_PUBLIC, description = "you need this") }) })
    @ExternalDocs(value = "Record XML Schema", url = "https://raw.githubusercontent.com/ORCID/ORCID-Source/master/orcid-model/src/main/resources/record_2.0/record-2.0.xsd")
    public Response viewClient(@PathParam("client_id") String clientId) {
        return serviceDelegator.viewClient(clientId);
    }
}
