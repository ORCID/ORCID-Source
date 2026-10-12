package org.orcid.api.memberV3.server;

import static org.orcid.core.api.OrcidApiConstants.ACTIVITIES;
import static org.orcid.core.api.OrcidApiConstants.ADDRESS;
import static org.orcid.core.api.OrcidApiConstants.API_STATUS_PATH;
import static org.orcid.core.api.OrcidApiConstants.BIOGRAPHY;
import static org.orcid.core.api.OrcidApiConstants.BULK_WORKS;
import static org.orcid.core.api.OrcidApiConstants.CLIENT_PATH;
import static org.orcid.core.api.OrcidApiConstants.CSV_SEARCH_PATH;
import static org.orcid.core.api.OrcidApiConstants.DISTINCTION;
import static org.orcid.core.api.OrcidApiConstants.DISTINCTIONS;
import static org.orcid.core.api.OrcidApiConstants.DISTINCTION_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.EDUCATION;
import static org.orcid.core.api.OrcidApiConstants.EDUCATIONS;
import static org.orcid.core.api.OrcidApiConstants.EDUCATION_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.EMAIL;
import static org.orcid.core.api.OrcidApiConstants.EMPLOYMENT;
import static org.orcid.core.api.OrcidApiConstants.EMPLOYMENTS;
import static org.orcid.core.api.OrcidApiConstants.EMPLOYMENT_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.ERROR;
import static org.orcid.core.api.OrcidApiConstants.EXPANDED_SEARCH_PATH;
import static org.orcid.core.api.OrcidApiConstants.EXTERNAL_IDENTIFIERS;
import static org.orcid.core.api.OrcidApiConstants.FUNDING;
import static org.orcid.core.api.OrcidApiConstants.FUNDINGS;
import static org.orcid.core.api.OrcidApiConstants.FUNDING_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.GROUP_ID_RECORD;
import static org.orcid.core.api.OrcidApiConstants.INVITED_POSITION;
import static org.orcid.core.api.OrcidApiConstants.INVITED_POSITIONS;
import static org.orcid.core.api.OrcidApiConstants.INVITED_POSITION_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.KEYWORDS;
import static org.orcid.core.api.OrcidApiConstants.MEMBERSHIP;
import static org.orcid.core.api.OrcidApiConstants.MEMBERSHIPS;
import static org.orcid.core.api.OrcidApiConstants.MEMBERSHIP_SUMMARY;
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
import static org.orcid.core.api.OrcidApiConstants.QUALIFICATION;
import static org.orcid.core.api.OrcidApiConstants.QUALIFICATIONS;
import static org.orcid.core.api.OrcidApiConstants.QUALIFICATION_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.RESEARCHER_URLS;
import static org.orcid.core.api.OrcidApiConstants.RESEARCH_RESOURCE;
import static org.orcid.core.api.OrcidApiConstants.RESEARCH_RESOURCES;
import static org.orcid.core.api.OrcidApiConstants.RESEARCH_RESOURCE_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.SEARCH_PATH;
import static org.orcid.core.api.OrcidApiConstants.SERVICE;
import static org.orcid.core.api.OrcidApiConstants.SERVICES;
import static org.orcid.core.api.OrcidApiConstants.SERVICE_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.STATUS_PATH;
import static org.orcid.core.api.OrcidApiConstants.TEXT_CSV;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.WORK;
import static org.orcid.core.api.OrcidApiConstants.WORKS;
import static org.orcid.core.api.OrcidApiConstants.WORK_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.RECORD_SUMMARY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.orcid.api.common.swagger.SwaggerUIBuilder;
import org.orcid.api.member.MemberApiServiceImplHelper;
import org.orcid.api.memberV3.server.delegator.MemberV3ApiServiceDelegator;
import org.orcid.api.notificationsV3.server.delegator.NotificationsApiServiceDelegator;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.core.exception.OrcidNotificationAlreadyReadException;
import org.orcid.core.togglz.Features;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.release.notification.permission.NotificationPermission;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.InvitedPosition;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.Qualification;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.Service;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.WorkBulk;
import org.springframework.stereotype.Component;

import org.apache.hc.core5.http.ParseException;

import org.orcid.jaxb.model.v3.release.record.Record;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.orcid.jaxb.model.message.ScopeConstants;
import org.orcid.core.model.RecordSummary;
import org.orcid.jaxb.model.v3.release.client.ClientSummary;
import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecords;
import org.orcid.jaxb.model.v3.release.notification.permission.NotificationPermissions;
import org.orcid.jaxb.model.v3.release.record.Addresses;
import org.orcid.jaxb.model.v3.release.record.Biography;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.jaxb.model.v3.release.record.Person;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.release.record.PersonalDetails;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Distinctions;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Educations;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Employments;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositions;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Memberships;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Qualifications;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResourceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResources;
import org.orcid.jaxb.model.v3.release.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Services;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
import org.orcid.jaxb.model.v3.release.search.Search;
import org.orcid.jaxb.model.v3.release.search.expanded.ExpandedSearch;

@Component
@Path("/v3.0")
public class MemberV3ApiServiceImplV3_0 extends MemberApiServiceImplHelper {

    @Context
    private UriInfo uriInfo;
    
    @Context
    private HttpServletRequest httpRequest;

    @Resource
    protected SwaggerUIBuilder swaggerUIBuilder;
    
    protected MemberV3ApiServiceDelegator<Distinction, Education, Employment, PersonExternalIdentifier, InvitedPosition, Funding, GroupIdRecord, Membership, OtherName, PeerReview, Qualification, ResearcherUrl, Service, Work, WorkBulk, Address, Keyword, ResearchResource> serviceDelegator;

    private NotificationsApiServiceDelegator<NotificationPermission> notificationsServiceDelegator;

    public void setServiceDelegator(
            MemberV3ApiServiceDelegator<Distinction, Education, Employment, PersonExternalIdentifier, InvitedPosition, Funding, GroupIdRecord, Membership, OtherName, PeerReview, Qualification, ResearcherUrl, Service, Work, WorkBulk, Address, Keyword, ResearchResource> serviceDelegator) {
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
    public Response viewSwagger() {
        return swaggerUIBuilder.build();
    }

    /**
     * Serves the Swagger UI o2c OAuth page
     * 
     * @return a 200 response containing the HTML
     */
    @GET
    @Produces(value = { MediaType.TEXT_HTML })
    @Path("/o2c.html")
    public Response viewSwaggerO2c() {
        return swaggerUIBuilder.buildSwaggerO2CHTML();
    }

    /**
     * @return Plain text message indicating health of service
     */
    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(STATUS_PATH)
    public Response viewStatusSimple() {
        httpRequest.setAttribute("skipAccessLog", true);
        httpRequest.setAttribute("isMonitoring", true);
        return serviceDelegator.viewStatusSimple();
    }
    
    @GET
    @Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(API_STATUS_PATH)
    public Response viewStatusJson() {
        httpRequest.setAttribute("skipAccessLog", true);
        httpRequest.setAttribute("isMonitoring", true);
        return serviceDelegator.viewStatus();
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ACTIVITIES)
    @Operation(summary = "Fetch all activities", tags = "Activities", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch all activities", content = @Content(schema = @Schema(implementation = ActivitiesSummary.class)))
    public Response viewActivities(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewActivities(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORK + PUTCODE)
    @Operation(summary = "Fetch a Work", tags = "Works", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch a Work", content = @Content(schema = @Schema(implementation = Work.class)))
    public Response viewWork(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewWork(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORKS)
    @Operation(summary = "Fetch all works", tags = "Works", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch all works", content = @Content(schema = @Schema(implementation = Works.class)))
    public Response viewWorks(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewWorks(orcid);
    }
    
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(BULK_WORKS)
    @Operation(summary = "Fetch specified works", tags = "Works", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch specified works", content = @Content(schema = @Schema(implementation = WorkBulk.class)))
    @ApiResponse(responseCode = "400", description = "More than 100 put-codes (error 9042)", content = @Content(schema = @Schema(implementation = OrcidError.class)))
    public Response viewSpecifiedWorks(@PathParam("orcid") String orcid, @PathParam("putCodes") String putCodes) {
        return serviceDelegator.viewBulkWorks(orcid, putCodes);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORK_SUMMARY + PUTCODE)
    @Operation(summary = "Fetch a Work Summary", tags = "Works", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch a Work Summary", content = @Content(schema = @Schema(implementation = WorkSummary.class)))
    public Response viewWorkSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewWorkSummary(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORK)
    @Operation(summary = "Create a Work", tags = "Works", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "201", description = "Work created, see HTTP Location header for URI", headers = @Header(name = "Location", description = "URI of the new item", schema = @Schema(type = "string", format = "uri")))
    public Response createWork(@PathParam("orcid") String orcid, Work work) {
        return serviceDelegator.createWork(orcid, work);
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORKS)
    @Operation(summary = "Create a list of Works", tags = "Works", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "200", description = "Each element of the bulk is the created work or the error for that work", content = @Content(schema = @Schema(implementation = WorkBulk.class)))
    public Response createWorks(@PathParam("orcid") String orcid, WorkBulk works) {
        return serviceDelegator.createWorks(orcid, works);
    }
    
    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORK + PUTCODE)
    @Operation(summary = "Update a Work", tags = "Works", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "200", description = "Work updated", content = @Content(schema = @Schema(implementation = Work.class)))
    public Response updateWork(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Work work) {
        return serviceDelegator.updateWork(orcid, getPutCode(putCode), work);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORK + PUTCODE)
    @Operation(summary = "Delete a Work", tags = "Works", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "204", description = "Work deleted")
    public Response deleteWork(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteWork(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING + PUTCODE)
    @Operation(summary = "Fetch a Funding", tags = "Fundings", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch a Funding", content = @Content(schema = @Schema(implementation = Funding.class)))
    public Response viewFunding(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewFunding(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDINGS)
    @Operation(summary = "Fetch all fundings", tags = "Fundings", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch all fundings", content = @Content(schema = @Schema(implementation = Fundings.class)))
    public Response viewFundings(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewFundings(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING_SUMMARY + PUTCODE)
    @Operation(summary = "Fetch a Funding Summary", tags = "Fundings", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch a Funding Summary", content = @Content(schema = @Schema(implementation = FundingSummary.class)))
    public Response viewFundingSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewFundingSummary(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING)
    @Operation(summary = "Create a Funding", tags = "Fundings", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "201", description = "Funding created, see HTTP Location header for URI", headers = @Header(name = "Location", description = "URI of the new item", schema = @Schema(type = "string", format = "uri")))
    public Response createFunding(@PathParam("orcid") String orcid, Funding funding) {
        return serviceDelegator.createFunding(orcid, funding);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING + PUTCODE)
    @Operation(summary = "Update a Funding", tags = "Fundings", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "200", description = "Funding updated", content = @Content(schema = @Schema(implementation = Funding.class)))
    public Response updateFunding(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Funding funding) {
        return serviceDelegator.updateFunding(orcid, getPutCode(putCode), funding);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING + PUTCODE)
    @Operation(summary = "Delete a Funding", tags = "Fundings", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "204", description = "Funding deleted")
    public Response deleteFunding(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteFunding(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATION + PUTCODE)
    @Operation(summary = "Fetch an Education", tags = "Education", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Education.class)))
    public Response viewEducation(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewEducation(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATIONS)
    @Operation(summary = "Fetch all educations", tags = "Education", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch all educations", content = @Content(schema = @Schema(implementation = Educations.class)))
    public Response viewEducations(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewEducations(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATION_SUMMARY + PUTCODE)
    @Operation(summary = "Fetch an Education summary", tags = "Education", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch an Education summary", content = @Content(schema = @Schema(implementation = EducationSummary.class)))
    public Response viewEducationSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewEducationSummary(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATION)
    @Operation(summary = "Create an Education", tags = "Education", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "201", description = "Education created, see HTTP Location header for URI", headers = @Header(name = "Location", description = "URI of the new item", schema = @Schema(type = "string", format = "uri")))
    public Response createEducation(@PathParam("orcid") String orcid, Education education) {
        return serviceDelegator.createEducation(orcid, education);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATION + PUTCODE)
    @Operation(summary = "Update an Education", tags = "Education", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "200", description = "Education updated", content = @Content(schema = @Schema(implementation = Education.class)))
    public Response updateEducation(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Education education) {
        return serviceDelegator.updateEducation(orcid, getPutCode(putCode), education);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATION + PUTCODE)
    @Operation(summary = "Delete an Education", tags = "Education", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "204", description = "Education deleted")
    public Response deleteEducation(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteAffiliation(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENT + PUTCODE)
    @Operation(summary = "Fetch an Employment", tags = "Employment", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch an Employment", content = @Content(schema = @Schema(implementation = Employment.class)))
    public Response viewEmployment(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewEmployment(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENTS)
    @Operation(summary = "Fetch all employments", tags = "Employment", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch all employments", content = @Content(schema = @Schema(implementation = Employments.class)))
    public Response viewEmployments(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewEmployments(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENT_SUMMARY + PUTCODE)
    @Operation(summary = "Fetch an Employment Summary", tags = "Employment", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch an Employment Summary", content = @Content(schema = @Schema(implementation = EmploymentSummary.class)))
    public Response viewEmploymentSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewEmploymentSummary(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENT)
    @Operation(summary = "Create an Employment", tags = "Employment", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "201", description = "Employment created, see HTTP Location header for URI", headers = @Header(name = "Location", description = "URI of the new item", schema = @Schema(type = "string", format = "uri")))
    public Response createEmployment(@PathParam("orcid") String orcid, Employment employment) {
        return serviceDelegator.createEmployment(orcid, employment);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENT + PUTCODE)
    @Operation(summary = "Update an Employment", tags = "Employment", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "200", description = "Employment updated", content = @Content(schema = @Schema(implementation = Employment.class)))
    public Response updateEmployment(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Employment employment) {
        return serviceDelegator.updateEmployment(orcid, getPutCode(putCode), employment);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENT + PUTCODE)
    @Operation(summary = "Delete an Employment", tags = "Employment", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "204", description = "Employment deleted")
    public Response deleteEmployment(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteAffiliation(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEW + PUTCODE)
    @Operation(summary = "Fetch a Peer Review", tags = "Peer reviews", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch a Peer Review", content = @Content(schema = @Schema(implementation = PeerReview.class)))
    public Response viewPeerReview(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewPeerReview(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEWS)
    @Operation(summary = "Fetch all peer reviews", tags = "Peer reviews", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch all peer reviews", content = @Content(schema = @Schema(implementation = PeerReviews.class)))
    public Response viewPeerReviews(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewPeerReviews(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEW_SUMMARY + PUTCODE)
    @Operation(summary = "Fetch a Peer Review Summary", tags = "Peer reviews", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch a Peer Review Summary", content = @Content(schema = @Schema(implementation = PeerReviewSummary.class)))
    public Response viewPeerReviewSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewPeerReviewSummary(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEW)
    @Operation(summary = "Create a Peer Review", tags = "Peer reviews", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "201", description = "Peer Review created, see HTTP Location header for URI", headers = @Header(name = "Location", description = "URI of the new item", schema = @Schema(type = "string", format = "uri")))
    public Response createPeerReview(@PathParam("orcid") String orcid, PeerReview peerReview) {
        return serviceDelegator.createPeerReview(orcid, peerReview);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEW + PUTCODE)
    @Operation(summary = "Update a Peer Review", tags = "Peer reviews", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "200", description = "Peer Review updated", content = @Content(schema = @Schema(implementation = PeerReview.class)))
    public Response updatePeerReview(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, PeerReview peerReview) {
        return serviceDelegator.updatePeerReview(orcid, getPutCode(putCode), peerReview);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEW + PUTCODE)
    @Operation(summary = "Delete a Peer Review", tags = "Peer reviews", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "204", description = "Peer Review deleted")
    public Response deletePeerReview(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deletePeerReview(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(GROUP_ID_RECORD + PUTCODE)
    @Operation(summary = "Fetch a Group", tags = "Group IDs", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.GROUP_ID_RECORD_READ }))
    @ApiResponse(responseCode = "200", description = "Fetch a Group", content = @Content(schema = @Schema(implementation = GroupIdRecord.class)))
    @ApiResponse(responseCode = "400", description = "No group ID record has this put-code (error 9026)", content = @Content(schema = @Schema(implementation = OrcidError.class)))
    public Response viewGroupIdRecord(@PathParam("putCode") String putCode) {
        return serviceDelegator.viewGroupIdRecord(getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(GROUP_ID_RECORD)
    @Operation(summary = "Create a Group", tags = "Group IDs", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.GROUP_ID_RECORD_UPDATE }))
    @ApiResponse(responseCode = "201", description = "Group created, see HTTP Location header for URI", headers = @Header(name = "Location", description = "URI of the new item", schema = @Schema(type = "string", format = "uri")))
    public Response createGroupIdRecord(GroupIdRecord groupIdRecord) {
        return serviceDelegator.createGroupIdRecord(groupIdRecord);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(GROUP_ID_RECORD + PUTCODE)
    @Operation(summary = "Update a Group", tags = "Group IDs", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.GROUP_ID_RECORD_UPDATE }))
    @ApiResponse(responseCode = "200", description = "Peer Review updated", content = @Content(schema = @Schema(implementation = GroupIdRecord.class)))
    public Response updateGroupIdRecord(@PathParam("putCode") String putCode, GroupIdRecord groupIdRecord) {
        return serviceDelegator.updateGroupIdRecord(groupIdRecord, getPutCode(putCode));
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(GROUP_ID_RECORD + PUTCODE)
    @Operation(summary = "Delete a Group", tags = "Group IDs", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.GROUP_ID_RECORD_UPDATE }))
    @ApiResponse(responseCode = "204", description = "Group deleted")
    public Response deleteGroupIdRecord(@PathParam("putCode") String putCode) {
        return serviceDelegator.deleteGroupIdRecord(getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(GROUP_ID_RECORD)
    @Operation(summary = "Fetch Groups", tags = "Group IDs", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.GROUP_ID_RECORD_READ }))
    @ApiResponse(responseCode = "200", description = "A page of group-id records; one record when `group-id` or `name` is given", content = @Content(schema = @Schema(oneOf = { GroupIdRecords.class, GroupIdRecord.class })))
    public Response viewGroupIdRecords(@QueryParam("page-size") @DefaultValue("100") String pageSize, @QueryParam("page") @DefaultValue("1") String page,
            @Parameter(description = "Name of a single group-id record to fetch.") @QueryParam("name") String name, @Parameter(description = "Group ID of a single record to fetch, for example `issn:0000-0001`.") @QueryParam("group-id") String groupId) {
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
    public Response viewError() {
        throw new RuntimeException("Sample Error", new Exception("Sample Exception"));
    }

    // ===================
    // START NOTIFICATIONS
    // ===================
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PERMISSIONS_PATH)
    @Operation(summary = "Fetch all notifications for an ORCID ID", tags = "Notifications", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.PREMIUM_NOTIFICATION }))
    @ApiResponse(responseCode = "200", description = "The permission notifications this client sent to the record", content = @Content(schema = @Schema(implementation = NotificationPermissions.class)))
    public Response viewPermissionNotifications(@PathParam("orcid") String orcid) {
        return notificationsServiceDelegator.findPermissionNotifications(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PERMISSIONS_VIEW_PATH)
    @Operation(summary = "Fetch a notification by id", tags = "Notifications", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.PREMIUM_NOTIFICATION }))
    @ApiResponse(responseCode = "200", description = "The notification", content = @Content(schema = @Schema(implementation = NotificationPermission.class)))
    public Response viewPermissionNotification(@PathParam("orcid") String orcid, @PathParam("id") Long id) {
        return notificationsServiceDelegator.findPermissionNotification(orcid, id);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PERMISSIONS_VIEW_PATH)
    @Consumes()
    @Operation(summary = "Archive a notification", tags = "Notifications", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.PREMIUM_NOTIFICATION }))
    @ApiResponse(responseCode = "200", description = "The notification, now archived", content = @Content(schema = @Schema(implementation = NotificationPermission.class)))
    public Response flagAsArchivedPermissionNotification(@PathParam("orcid") String orcid, @PathParam("id") Long id) throws OrcidNotificationAlreadyReadException {
        return notificationsServiceDelegator.flagNotificationAsArchived(orcid, id);
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PERMISSIONS_PATH)
    @Operation(summary = "Add a notification", tags = "Notifications", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.PREMIUM_NOTIFICATION }))
    @ApiResponse(responseCode = "201", description = "Notification created; its URI is in the Location header", headers = @Header(name = "Location", description = "URI of the new item", schema = @Schema(type = "string", format = "uri")))
    public Response addPermissionNotification(@PathParam("orcid") String orcid, NotificationPermission notification) {
        return notificationsServiceDelegator.addPermissionNotification(uriInfo, orcid, notification);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(RESEARCHER_URLS)
    @Operation(summary = "Fetch all researcher urls for an ORCID ID", tags = "Researcher URLs", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch all researcher urls for an ORCID ID", content = @Content(schema = @Schema(implementation = ResearcherUrls.class)))
    public Response viewResearcherUrls(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewResearcherUrls(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(RESEARCHER_URLS + PUTCODE)
    @Operation(summary = "Fetch one researcher url for an ORCID ID", tags = "Researcher URLs", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch one researcher url for an ORCID ID", content = @Content(schema = @Schema(implementation = ResearcherUrl.class)))
    public Response viewResearcherUrl(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewResearcherUrl(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(RESEARCHER_URLS)
    @Operation(summary = "Add a new researcher url for an ORCID ID", tags = "Researcher URLs", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.PERSON_UPDATE }))
    @ApiResponse(responseCode = "201", description = "Created; the new item's URI is in the Location header", headers = @Header(name = "Location", description = "URI of the new item", schema = @Schema(type = "string", format = "uri")))
    public Response createResearcherUrl(@PathParam("orcid") String orcid, ResearcherUrl researcherUrl) {
        return serviceDelegator.createResearcherUrl(orcid, researcherUrl);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(RESEARCHER_URLS + PUTCODE)
    @Operation(summary = "Edits researcher url for an ORCID ID", tags = "Researcher URLs", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.PERSON_UPDATE }))
    @ApiResponse(responseCode = "200", description = "Edits researcher url for an ORCID ID", content = @Content(schema = @Schema(implementation = ResearcherUrl.class)))
    public Response editResearcherUrl(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, ResearcherUrl researcherUrl) {
        return serviceDelegator.updateResearcherUrl(orcid, getPutCode(putCode), researcherUrl);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(RESEARCHER_URLS + PUTCODE)
    @Operation(summary = "Delete one researcher url from an ORCID ID", tags = "Researcher URLs", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.PERSON_UPDATE }))
    @ApiResponse(responseCode = "204", description = "Deleted")
    public Response deleteResearcherUrl(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteResearcherUrl(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMAIL)
    @Operation(summary = "Fetch all emails for an ORCID ID", tags = "Emails", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch all emails for an ORCID ID", content = @Content(schema = @Schema(implementation = Emails.class)))
    public Response viewEmails(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewEmails(orcid);
    }

    // Other names
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(OTHER_NAMES + PUTCODE)
    @Operation(summary = "Fetch Other name", tags = "Other names", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch Other name", content = @Content(schema = @Schema(implementation = OtherName.class)))
    public Response viewOtherName(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewOtherName(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(OTHER_NAMES)
    @Operation(summary = "Fetch Other names", tags = "Other names", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch Other names", content = @Content(schema = @Schema(implementation = OtherNames.class)))
    public Response viewOtherNames(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewOtherNames(orcid);
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(OTHER_NAMES)
    @Operation(summary = "Add other name", tags = "Other names", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.PERSON_UPDATE }))
    @ApiResponse(responseCode = "201", description = "Created; the new item's URI is in the Location header", headers = @Header(name = "Location", description = "URI of the new item", schema = @Schema(type = "string", format = "uri")))
    public Response createOtherName(@PathParam("orcid") String orcid, OtherName otherName) {
        return serviceDelegator.createOtherName(orcid, otherName);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(OTHER_NAMES + PUTCODE)
    @Operation(summary = "Edit other name", tags = "Other names", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.PERSON_UPDATE }))
    @ApiResponse(responseCode = "200", description = "Edit other name", content = @Content(schema = @Schema(implementation = OtherName.class)))
    public Response editOtherName(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, OtherName otherName) {
        return serviceDelegator.updateOtherName(orcid, getPutCode(putCode), otherName);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(OTHER_NAMES + PUTCODE)
    @Operation(summary = "Delete other name", tags = "Other names", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.PERSON_UPDATE }))
    @ApiResponse(responseCode = "204", description = "Deleted")
    public Response deleteOtherName(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteOtherName(orcid, getPutCode(putCode));
    }

    // Personal details
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PERSONAL_DETAILS)
    @Operation(summary = "Fetch personal details for an ORCID ID", tags = "Personal details", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch personal details for an ORCID ID", content = @Content(schema = @Schema(implementation = PersonalDetails.class)))
    public Response viewPersonalDetails(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewPersonalDetails(orcid);
    }

    // External Identifiers
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EXTERNAL_IDENTIFIERS + PUTCODE)
    @Operation(summary = "Fetch external identifier", tags = "External identifiers", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch external identifier", content = @Content(schema = @Schema(implementation = PersonExternalIdentifier.class)))
    public Response viewExternalIdentifier(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewExternalIdentifier(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EXTERNAL_IDENTIFIERS)
    @Operation(summary = "Fetch external identifiers", tags = "External identifiers", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch external identifiers", content = @Content(schema = @Schema(implementation = PersonExternalIdentifiers.class)))
    public Response viewExternalIdentifiers(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewExternalIdentifiers(orcid);
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EXTERNAL_IDENTIFIERS)
    @Operation(summary = "Add external identifier", tags = "External identifiers", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.PERSON_UPDATE }))
    @ApiResponse(responseCode = "201", description = "Created; the new item's URI is in the Location header", headers = @Header(name = "Location", description = "URI of the new item", schema = @Schema(type = "string", format = "uri")))
    public Response createExternalIdentifier(@PathParam("orcid") String orcid, PersonExternalIdentifier externalIdentifier) {
        return serviceDelegator.createExternalIdentifier(orcid, externalIdentifier);
    }
    
    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EXTERNAL_IDENTIFIERS + PUTCODE)
    @Operation(summary = "Edit external identifier", tags = "External identifiers", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.PERSON_UPDATE }))
    @ApiResponse(responseCode = "200", description = "Edit external identifier", content = @Content(schema = @Schema(implementation = PersonExternalIdentifier.class)))
    public Response editExternalIdentifier(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, PersonExternalIdentifier externalIdentifier) {
        return serviceDelegator.updateExternalIdentifier(orcid, getPutCode(putCode), externalIdentifier);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EXTERNAL_IDENTIFIERS + PUTCODE)
    @Operation(summary = "Delete external identifier", tags = "External identifiers", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.PERSON_UPDATE }))
    @ApiResponse(responseCode = "204", description = "Deleted")
    public Response deleteExternalIdentifier(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteExternalIdentifier(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(BIOGRAPHY)
    @Operation(summary = "Get biography details", tags = "Biography", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Get biography details", content = @Content(schema = @Schema(implementation = Biography.class)))
    @ApiResponse(responseCode = "404", description = "The record has no biography (error 9041)", content = @Content(schema = @Schema(implementation = OrcidError.class)))
    public Response viewBiography(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewBiography(orcid);
    }

    // Keywords
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(KEYWORDS + PUTCODE)
    @Operation(summary = "Fetch keyword", tags = "Keywords", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch keyword", content = @Content(schema = @Schema(implementation = Keyword.class)))
    public Response viewKeyword(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewKeyword(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(KEYWORDS)
    @Operation(summary = "Fetch keywords", tags = "Keywords", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch keywords", content = @Content(schema = @Schema(implementation = Keywords.class)))
    public Response viewKeywords(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewKeywords(orcid);
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(KEYWORDS)
    @Operation(summary = "Add keyword", tags = "Keywords", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.PERSON_UPDATE }))
    @ApiResponse(responseCode = "201", description = "Created; the new item's URI is in the Location header", headers = @Header(name = "Location", description = "URI of the new item", schema = @Schema(type = "string", format = "uri")))
    public Response createKeyword(@PathParam("orcid") String orcid, Keyword keyword) {
        return serviceDelegator.createKeyword(orcid, keyword);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(KEYWORDS + PUTCODE)
    @Operation(summary = "Edit keyword", tags = "Keywords", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.PERSON_UPDATE }))
    @ApiResponse(responseCode = "200", description = "Edit keyword", content = @Content(schema = @Schema(implementation = Keyword.class)))
    public Response editKeyword(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Keyword keyword) {
        return serviceDelegator.updateKeyword(orcid, getPutCode(putCode), keyword);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(KEYWORDS + PUTCODE)
    @Operation(summary = "Delete keyword", tags = "Keywords", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.PERSON_UPDATE }))
    @ApiResponse(responseCode = "204", description = "Deleted")
    public Response deleteKeyword(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteKeyword(orcid, getPutCode(putCode));
    }

    // Address
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ADDRESS + PUTCODE)
    @Operation(summary = "Fetch an address", tags = "Addresses", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch an address", content = @Content(schema = @Schema(implementation = Address.class)))
    public Response viewAddress(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewAddress(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ADDRESS)
    @Operation(summary = "Fetch all addresses of a profile", tags = "Addresses", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch all addresses of a profile", content = @Content(schema = @Schema(implementation = Addresses.class)))
    public Response viewAddresses(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewAddresses(orcid);
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ADDRESS)
    @Operation(summary = "Add an address", tags = "Addresses", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.PERSON_UPDATE }))
    @ApiResponse(responseCode = "201", description = "Created; the new item's URI is in the Location header", headers = @Header(name = "Location", description = "URI of the new item", schema = @Schema(type = "string", format = "uri")))
    public Response createAddress(@PathParam("orcid") String orcid, Address address) {
        return serviceDelegator.createAddress(orcid, address);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ADDRESS + PUTCODE)
    @Operation(summary = "Edit an address", tags = "Addresses", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.PERSON_UPDATE }))
    @ApiResponse(responseCode = "200", description = "Edit an address", content = @Content(schema = @Schema(implementation = Address.class)))
    public Response editAddress(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Address address) {
        return serviceDelegator.updateAddress(orcid, getPutCode(putCode), address);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ADDRESS + PUTCODE)
    @Operation(summary = "Delete an address", tags = "Addresses", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.PERSON_UPDATE }))
    @ApiResponse(responseCode = "204", description = "Deleted")
    public Response deleteAddress(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteAddress(orcid, getPutCode(putCode));
    }

    // Person
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PERSON)
    @Operation(summary = "Fetch person details", tags = "Person", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch person details", content = @Content(schema = @Schema(implementation = Person.class)))
    public Response viewPerson(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewPerson(orcid);
    }

    // Record
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(OrcidApiConstants.RECORD_SIMPLE)
    @Operation(summary = "Fetch record details", tags = "Record", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch record details", content = @Content(schema = @Schema(implementation = Record.class)))
    public Response viewRecord(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewRecord(orcid);
    }
    
    // Record
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(OrcidApiConstants.RECORD_RECORD)
    @Operation(summary = "Fetch record details", tags = "Record", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch record details", content = @Content(schema = @Schema(implementation = Record.class)))
    public Response viewRecordRecord(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewRecord(orcid);
    }
    
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(SEARCH_PATH)
    @Operation(summary = "Search records", tags = "Search", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }))
    @Parameter(name = "start", in = ParameterIn.QUERY, description = "Offset of the first result (0-based).", schema = @Schema(type = "integer"))
    @Parameter(name = "rows", in = ParameterIn.QUERY, description = "Number of results to return (maximum 1000).", schema = @Schema(type = "integer"))
    @ApiResponse(responseCode = "200", description = "Search records", content = @Content(schema = @Schema(implementation = Search.class)))
    @ApiResponse(responseCode = "400", description = "The start or rows parameter is out of range (rows 1,000 at most; start 10,000 at most without a token)", content = @Content(schema = @Schema(implementation = OrcidError.class)))
    public Response searchByQuery(@Parameter(description = "Solr query, for example `family-name:Carberry` or `doi-self:10.1087/20120404`.") @QueryParam("q") @DefaultValue("") String query, @Context UriInfo uriInfo) throws ParseException {
        Map<String, List<String>> solrParams = new HashMap<>(uriInfo.getQueryParameters());
        Response xmlQueryResults = serviceDelegator.searchByQuery(solrParams);
        return xmlQueryResults;
    }
    
    @GET
    @Produces(TEXT_CSV)
    @Path(CSV_SEARCH_PATH)
    @Operation(summary = "Search records", tags = "Search", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }))
    @Parameter(name = "start", in = ParameterIn.QUERY, description = "Offset of the first result (0-based).", schema = @Schema(type = "integer"))
    @Parameter(name = "rows", in = ParameterIn.QUERY, description = "Number of results to return (maximum 1000).", schema = @Schema(type = "integer"))
    @Parameter(name = "fl", in = ParameterIn.QUERY, description = "Comma-separated fields to return, for example `orcid,given-names,family-name`.", schema = @Schema(type = "string"))
    @ApiResponse(responseCode = "200", description = "One row per result, with the requested fields", content = @Content(mediaType = "text/csv", schema = @Schema(type = "string")))
    @ApiResponse(responseCode = "400", description = "The start or rows parameter is out of range (rows 1,000 at most; start 10,000 at most without a token)", content = @Content(schema = @Schema(implementation = OrcidError.class)))
    public Response searchByQueryCSV(@Parameter(description = "Solr query, for example `family-name:Carberry` or `doi-self:10.1087/20120404`.") @QueryParam("q") @DefaultValue("") String query, @Context UriInfo uriInfo) throws ParseException {
        Map<String, List<String>> solrParams = new HashMap<>(uriInfo.getQueryParameters());
        Response csvQueryResults = serviceDelegator.searchByQueryCSV(solrParams);
        return csvQueryResults;
    }
    
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EXPANDED_SEARCH_PATH)
    @Operation(summary = "Search records", tags = "Search", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }))
    @Parameter(name = "start", in = ParameterIn.QUERY, description = "Offset of the first result (0-based).", schema = @Schema(type = "integer"))
    @Parameter(name = "rows", in = ParameterIn.QUERY, description = "Number of results to return (maximum 1000).", schema = @Schema(type = "integer"))
    @ApiResponse(responseCode = "200", description = "Matching records with their names, emails and institutions", content = @Content(schema = @Schema(implementation = ExpandedSearch.class)))
    @ApiResponse(responseCode = "400", description = "The start or rows parameter is out of range (rows 1,000 at most; start 10,000 at most without a token)", content = @Content(schema = @Schema(implementation = OrcidError.class)))
    public Response expandedSearchByQuery(@Parameter(description = "Solr query, for example `family-name:Carberry` or `doi-self:10.1087/20120404`.") @QueryParam("q") @DefaultValue("") String query, @Context UriInfo uriInfo) {
        Map<String, List<String>> solrParams = new HashMap<>(uriInfo.getQueryParameters());
        Response queryResults = serviceDelegator.expandedSearchByQuery(solrParams);
        return queryResults;
    }
    

    @GET
    @Path(CLIENT_PATH)
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Operation(summary = "Fetch client details", tags = "Clients", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }))
    @ApiResponse(responseCode = "200", description = "Fetch client details", content = @Content(schema = @Schema(implementation = ClientSummary.class)))
    public Response viewClient(@PathParam("client_id") String clientId) {
        return serviceDelegator.viewClient(clientId);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(DISTINCTION + PUTCODE)
    @Operation(summary = "Fetch an Distinction", tags = "Distinction", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Distinction.class)))
    public Response viewDistinction(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewDistinction(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(DISTINCTIONS)
    @Operation(summary = "Fetch all distinctions", tags = "Distinction", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch all distinctions", content = @Content(schema = @Schema(implementation = Distinctions.class)))
    public Response viewDistinctions(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewDistinctions(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(DISTINCTION_SUMMARY + PUTCODE)
    @Operation(summary = "Fetch an Distinction summary", tags = "Distinction", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch an Distinction summary", content = @Content(schema = @Schema(implementation = DistinctionSummary.class)))
    public Response viewDistinctionSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewDistinctionSummary(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(DISTINCTION)
    @Operation(summary = "Create an Distinction", tags = "Distinction", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "201", description = "Distinction created, see HTTP Location header for URI", headers = @Header(name = "Location", description = "URI of the new item", schema = @Schema(type = "string", format = "uri")))
    public Response createDistinction(@PathParam("orcid") String orcid, Distinction distinction) {
        return serviceDelegator.createDistinction(orcid, distinction);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(DISTINCTION + PUTCODE)
    @Operation(summary = "Update an Distinction", tags = "Distinction", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "200", description = "Distinction updated", content = @Content(schema = @Schema(implementation = Distinction.class)))
    public Response updateDistinction(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Distinction distinction) {
        return serviceDelegator.updateDistinction(orcid, getPutCode(putCode), distinction);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(DISTINCTION + PUTCODE)
    @Operation(summary = "Delete an Distinction", tags = "Distinction", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "204", description = "Distinction deleted")
    public Response deleteDistinction(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteAffiliation(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(INVITED_POSITION + PUTCODE)
    @Operation(summary = "Fetch an InvitedPosition", tags = "Invited position", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = InvitedPosition.class)))
    public Response viewInvitedPosition(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewInvitedPosition(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(INVITED_POSITIONS)
    @Operation(summary = "Fetch all invitedPositions", tags = "Invited position", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch all invitedPositions", content = @Content(schema = @Schema(implementation = InvitedPositions.class)))
    public Response viewInvitedPositions(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewInvitedPositions(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(INVITED_POSITION_SUMMARY + PUTCODE)
    @Operation(summary = "Fetch an InvitedPosition summary", tags = "Invited position", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch an InvitedPosition summary", content = @Content(schema = @Schema(implementation = InvitedPositionSummary.class)))
    public Response viewInvitedPositionSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewInvitedPositionSummary(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(INVITED_POSITION)
    @Operation(summary = "Create an InvitedPosition", tags = "Invited position", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "201", description = "InvitedPosition created, see HTTP Location header for URI", headers = @Header(name = "Location", description = "URI of the new item", schema = @Schema(type = "string", format = "uri")))
    public Response createInvitedPosition(@PathParam("orcid") String orcid, InvitedPosition invitedPosition) {
        return serviceDelegator.createInvitedPosition(orcid, invitedPosition);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(INVITED_POSITION + PUTCODE)
    @Operation(summary = "Update an InvitedPosition", tags = "Invited position", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "200", description = "InvitedPosition updated", content = @Content(schema = @Schema(implementation = InvitedPosition.class)))
    public Response updateInvitedPosition(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, InvitedPosition invitedPosition) {
        return serviceDelegator.updateInvitedPosition(orcid, getPutCode(putCode), invitedPosition);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(INVITED_POSITION + PUTCODE)
    @Operation(summary = "Delete an InvitedPosition", tags = "Invited position", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "204", description = "InvitedPosition deleted")
    public Response deleteInvitedPosition(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteAffiliation(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(MEMBERSHIP + PUTCODE)
    @Operation(summary = "Fetch an Membership", tags = "Membership", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Membership.class)))
    public Response viewMembership(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewMembership(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(MEMBERSHIPS)
    @Operation(summary = "Fetch all memberships", tags = "Membership", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch all memberships", content = @Content(schema = @Schema(implementation = Memberships.class)))
    public Response viewMemberships(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewMemberships(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(MEMBERSHIP_SUMMARY + PUTCODE)
    @Operation(summary = "Fetch an Membership summary", tags = "Membership", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch an Membership summary", content = @Content(schema = @Schema(implementation = MembershipSummary.class)))
    public Response viewMembershipSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewMembershipSummary(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(MEMBERSHIP)
    @Operation(summary = "Create an Membership", tags = "Membership", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "201", description = "Membership created, see HTTP Location header for URI", headers = @Header(name = "Location", description = "URI of the new item", schema = @Schema(type = "string", format = "uri")))
    public Response createMembership(@PathParam("orcid") String orcid, Membership membership) {
        return serviceDelegator.createMembership(orcid, membership);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(MEMBERSHIP + PUTCODE)
    @Operation(summary = "Update an Membership", tags = "Membership", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "200", description = "Membership updated", content = @Content(schema = @Schema(implementation = Membership.class)))
    public Response updateMembership(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Membership membership) {
        return serviceDelegator.updateMembership(orcid, getPutCode(putCode), membership);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(MEMBERSHIP + PUTCODE)
    @Operation(summary = "Delete an Membership", tags = "Membership", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "204", description = "Membership deleted")
    public Response deleteMembership(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteAffiliation(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(QUALIFICATION + PUTCODE)
    @Operation(summary = "Fetch an Qualification", tags = "Qualification", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Qualification.class)))
    public Response viewQualification(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewQualification(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(QUALIFICATIONS)
    @Operation(summary = "Fetch all qualifications", tags = "Qualification", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch all qualifications", content = @Content(schema = @Schema(implementation = Qualifications.class)))
    public Response viewQualifications(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewQualifications(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(QUALIFICATION_SUMMARY + PUTCODE)
    @Operation(summary = "Fetch an Qualification summary", tags = "Qualification", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch an Qualification summary", content = @Content(schema = @Schema(implementation = QualificationSummary.class)))
    public Response viewQualificationSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewQualificationSummary(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(QUALIFICATION)
    @Operation(summary = "Create an Qualification", tags = "Qualification", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "201", description = "Qualification created, see HTTP Location header for URI", headers = @Header(name = "Location", description = "URI of the new item", schema = @Schema(type = "string", format = "uri")))
    public Response createQualification(@PathParam("orcid") String orcid, Qualification qualification) {
        return serviceDelegator.createQualification(orcid, qualification);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(QUALIFICATION + PUTCODE)
    @Operation(summary = "Update an Qualification", tags = "Qualification", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "200", description = "Qualification updated", content = @Content(schema = @Schema(implementation = Qualification.class)))
    public Response updateQualification(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Qualification qualification) {
        return serviceDelegator.updateQualification(orcid, getPutCode(putCode), qualification);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(QUALIFICATION + PUTCODE)
    @Operation(summary = "Delete an Qualification", tags = "Qualification", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "204", description = "Qualification deleted")
    public Response deleteQualification(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteAffiliation(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(SERVICE + PUTCODE)
    @Operation(summary = "Fetch an Service", tags = "Service", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Service.class)))
    public Response viewService(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewService(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(SERVICES)
    @Operation(summary = "Fetch all services", tags = "Service", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch all services", content = @Content(schema = @Schema(implementation = Services.class)))
    public Response viewServices(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewServices(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(SERVICE_SUMMARY + PUTCODE)
    @Operation(summary = "Fetch an Service summary", tags = "Service", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch an Service summary", content = @Content(schema = @Schema(implementation = ServiceSummary.class)))
    public Response viewServiceSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewServiceSummary(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(SERVICE)
    @Operation(summary = "Create an Service", tags = "Service", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "201", description = "Service created, see HTTP Location header for URI", headers = @Header(name = "Location", description = "URI of the new item", schema = @Schema(type = "string", format = "uri")))
    public Response createService(@PathParam("orcid") String orcid, Service service) {
        return serviceDelegator.createService(orcid, service);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(SERVICE + PUTCODE)
    @Operation(summary = "Update an Service", tags = "Service", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "200", description = "Service updated", content = @Content(schema = @Schema(implementation = Service.class)))
    public Response updateService(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, Service service) {
        return serviceDelegator.updateService(orcid, getPutCode(putCode), service);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(SERVICE + PUTCODE)
    @Operation(summary = "Delete an Service", tags = "Service", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "204", description = "Service deleted")
    public Response deleteService(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteAffiliation(orcid, getPutCode(putCode));
    }
    
    //Research resources
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(RESEARCH_RESOURCE + PUTCODE)
    @Operation(summary = "Fetch a Research Resource", tags = "Research resources", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ResearchResource.class)))
    public Response viewResearchResource(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewResearchResource(orcid, getPutCode(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(RESEARCH_RESOURCES)
    @Operation(summary = "Fetch all Research Resources", tags = "Research resources", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch all Research Resources", content = @Content(schema = @Schema(implementation = ResearchResources.class)))
    public Response viewResearchResources(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewResearchResources(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(RESEARCH_RESOURCE_SUMMARY + PUTCODE)
    @Operation(summary = "Fetch a Research Resource summary", tags = "Research resources", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "200", description = "Fetch a Research Resource summary", content = @Content(schema = @Schema(implementation = ResearchResourceSummary.class)))
    public Response viewResearchResourceSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewResearchResourceSummary(orcid, getPutCode(putCode));
    }

    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(RESEARCH_RESOURCE)
    @Operation(summary = "Create a Research Resource", tags = "Research resources", security = { @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_LIMITED }), @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }) })
    @ApiResponse(responseCode = "201", description = "ResearchResource created, see HTTP Location header for URI", headers = @Header(name = "Location", description = "URI of the new item", schema = @Schema(type = "string", format = "uri")))
    public Response createResearchResource(@PathParam("orcid") String orcid, ResearchResource ResearchResource) {
        return serviceDelegator.createResearchResource(orcid, ResearchResource);
    }

    @PUT
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(RESEARCH_RESOURCE + PUTCODE)
    @Operation(summary = "Update a Research Resource", tags = "Research resources", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "200", description = "ResearchResource updated", content = @Content(schema = @Schema(implementation = ResearchResource.class)))
    public Response updateResearchResource(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode, ResearchResource ResearchResource) {
        return serviceDelegator.updateResearchResource(orcid, getPutCode(putCode), ResearchResource);
    }

    @DELETE
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(RESEARCH_RESOURCE + PUTCODE)
    @Operation(summary = "Delete an Research Resource", tags = "Research resources", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.ACTIVITIES_UPDATE }))
    @ApiResponse(responseCode = "204", description = "ResearchResource deleted")
    public Response deleteResearchResource(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.deleteResearchResource(orcid, getPutCode(putCode));
    }
    
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(RECORD_SUMMARY)
    @Operation(summary = "Fetch a summary of the record", tags = "Record summary", security = @SecurityRequirement(name = "orcid_auth", scopes = { ScopeConstants.READ_PUBLIC }))
    @ApiResponse(responseCode = "200", description = "Counts and highlights of the record's public items", content = @Content(schema = @Schema(implementation = RecordSummary.class)))
    public Response getRecordSummary(@PathParam("orcid") String orcid) {
        if(!Features.MAPI_SUMMARY_ENDPOINT.isActive()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return serviceDelegator.getRecordSummary(orcid);
    }
}