package org.orcid.api.publicV3.server;

import static org.orcid.core.api.OrcidApiConstants.ACTIVITIES;
import static org.orcid.core.api.OrcidApiConstants.ADDRESS;
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
import static org.orcid.core.api.OrcidApiConstants.EXPANDED_SEARCH_PATH;
import static org.orcid.core.api.OrcidApiConstants.EXTERNAL_IDENTIFIERS;
import static org.orcid.core.api.OrcidApiConstants.FUNDING;
import static org.orcid.core.api.OrcidApiConstants.FUNDINGS;
import static org.orcid.core.api.OrcidApiConstants.FUNDING_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.INVITED_POSITION;
import static org.orcid.core.api.OrcidApiConstants.INVITED_POSITIONS;
import static org.orcid.core.api.OrcidApiConstants.INVITED_POSITION_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.JSON_LD;
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
import static org.orcid.core.api.OrcidApiConstants.PERSON;
import static org.orcid.core.api.OrcidApiConstants.PERSONAL_DETAILS;
import static org.orcid.core.api.OrcidApiConstants.PUB_STATUS_PATH;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.apache.hc.core5.http.ParseException;
import org.orcid.api.common.swagger.SwaggerUIBuilder;
import org.orcid.api.publicV3.server.delegator.PublicV3ApiServiceDelegator;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.InvitedPosition;
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.Qualification;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.Service;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.springframework.stereotype.Component;

import org.orcid.jaxb.model.v3.release.record.Record;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.orcid.jaxb.model.v3.release.client.ClientSummary;
import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Addresses;
import org.orcid.jaxb.model.v3.release.record.Biography;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.jaxb.model.v3.release.record.Person;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.release.record.PersonalDetails;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.jaxb.model.v3.release.record.WorkBulk;
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
import org.orcid.jaxb.model.v3.release.search.expanded.ExpandedSearch;
import org.orcid.jaxb.model.v3.release.search.Search;

/**
* 
* @author Angel Montenegro
* 
*/
@Component
@Path("/v3.0")
public class PublicV3ApiServiceImplV3_0 {
  
  @Resource(name="publicV3ApiServiceDelegator")  
  protected PublicV3ApiServiceDelegator<Distinction, Education, Employment, PersonExternalIdentifier, InvitedPosition, Funding, GroupIdRecord, Membership, OtherName, PeerReview, Qualification, ResearcherUrl, Service, Work> serviceDelegator;

  
  @Resource
  protected SwaggerUIBuilder swaggerUIBuilder;
  
  
  @Context
  private HttpServletRequest httpRequest;  
  
  /**
   * Serves the Swagger UI HTML page
   * 
   * @return a 200 response containing the HTML
   */
 
  @GET
  @Produces(value = { MediaType.TEXT_HTML })  
  public Response viewSwagger() {
      return swaggerUIBuilder.build();
  }

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
  @Path(PUB_STATUS_PATH)
  public Response viewStatusJson() {
      httpRequest.setAttribute("skipAccessLog", true);
      httpRequest.setAttribute("isMonitoring", true);
      return serviceDelegator.viewStatus();
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(ACTIVITIES)
  @Operation(summary = "Fetch all activities", tags = "Activities")
  @ApiResponse(responseCode = "200", description = "A summary of every activity on the record", content = @Content(schema = @Schema(implementation = ActivitiesSummary.class)))
  public Response viewActivities(@PathParam("orcid") String orcid, @Context HttpServletRequest httpRequest) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewActivities(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON,
          OrcidApiConstants.APPLICATION_CITEPROC })
  @Path(WORK + PUTCODE)
  @Operation(summary = "Fetch a work", tags = "Works")
  @ApiResponse(responseCode = "200", description = "The work", content = { @Content(schema = @Schema(implementation = Work.class)),
          @Content(mediaType = "application/vnd.citationstyles.csl+json", schema = @Schema(type = "object", description = "CSL-JSON item, for citation tools")) })
  public Response viewWork(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode, @Context HttpServletRequest httpRequest) {
      serviceDelegator.trackEvents(httpRequest);
      if (OrcidApiConstants.APPLICATION_CITEPROC.equals(httpRequest.getHeader("Accept")))
          return serviceDelegator.viewWorkCitation(orcid, putCode);
      return serviceDelegator.viewWork(orcid, putCode);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(WORK_SUMMARY + PUTCODE)
  @Operation(summary = "Fetch a work summary", tags = "Works")
  @ApiResponse(responseCode = "200", description = "The summary of the work", content = @Content(schema = @Schema(implementation = WorkSummary.class)))
  public Response viewWorkSummary(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewWorkSummary(orcid, putCode);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(WORKS)
  @Operation(summary = "Fetch all works", tags = "Works")
  @ApiResponse(responseCode = "200", description = "The record's works", content = @Content(schema = @Schema(implementation = Works.class)))
  public Response viewWorks(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewWorks(orcid);
  }
  
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(BULK_WORKS)
  @Operation(summary = "Fetch works by put-code", tags = "Works")
  @ApiResponse(responseCode = "200", description = "The requested works; an error element stands in for each put-code that could not be read", content = @Content(schema = @Schema(implementation = WorkBulk.class)))
  @ApiResponse(responseCode = "400", description = "More than 100 put-codes (error 9042)", content = @Content(schema = @Schema(implementation = OrcidError.class)))
  public Response viewSpecifiedWorks(@PathParam("orcid") String orcid, @PathParam("putCodes") String putCodes) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewBulkWorks(orcid, putCodes);
  }
  
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(FUNDING + PUTCODE)
  @Operation(summary = "Fetch a funding", tags = "Fundings")
  @ApiResponse(responseCode = "200", description = "The funding", content = @Content(schema = @Schema(implementation = Funding.class)))
  public Response viewFunding(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewFunding(orcid, putCode);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(FUNDING_SUMMARY + PUTCODE)
  @Operation(summary = "Fetch a funding summary", tags = "Fundings")
  @ApiResponse(responseCode = "200", description = "The summary of the funding", content = @Content(schema = @Schema(implementation = FundingSummary.class)))
  public Response viewFundingSummary(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewFundingSummary(orcid, putCode);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(FUNDINGS)
  @Operation(summary = "Fetch all fundings", tags = "Fundings")
  @ApiResponse(responseCode = "200", description = "The record's fundings", content = @Content(schema = @Schema(implementation = Fundings.class)))
  public Response viewFundings(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewFundings(orcid);
  }
  
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(EDUCATION + PUTCODE)
  @Operation(summary = "Fetch an education", tags = "Education")
  @ApiResponse(responseCode = "200", description = "The education", content = @Content(schema = @Schema(implementation = Education.class)))
  public Response viewEducation(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewEducation(orcid, putCode);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(EDUCATION_SUMMARY + PUTCODE)
  @Operation(summary = "Fetch an education summary", tags = "Education")
  @ApiResponse(responseCode = "200", description = "The summary of the education", content = @Content(schema = @Schema(implementation = EducationSummary.class)))
  public Response viewEducationSummary(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewEducationSummary(orcid, putCode);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(EDUCATIONS)
  @Operation(summary = "Fetch all educations", tags = "Education")
  @ApiResponse(responseCode = "200", description = "The record's educations", content = @Content(schema = @Schema(implementation = Educations.class)))
  public Response viewEducations(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewEducations(orcid);
  }
  
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(EMPLOYMENT + PUTCODE)
  @Operation(summary = "Fetch an employment", tags = "Employment")
  @ApiResponse(responseCode = "200", description = "The employment", content = @Content(schema = @Schema(implementation = Employment.class)))
  public Response viewEmployment(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewEmployment(orcid, putCode);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(EMPLOYMENT_SUMMARY + PUTCODE)
  @Operation(summary = "Fetch an employment summary", tags = "Employment")
  @ApiResponse(responseCode = "200", description = "The summary of the employment", content = @Content(schema = @Schema(implementation = EmploymentSummary.class)))
  public Response viewEmploymentSummary(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewEmploymentSummary(orcid, putCode);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(EMPLOYMENTS)
  @Operation(summary = "Fetch all employments", tags = "Employment")
  @ApiResponse(responseCode = "200", description = "The record's public employments", content = @Content(schema = @Schema(implementation = Employments.class)))
  public Response viewEmployments(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewEmployments(orcid);
  }
  
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(PEER_REVIEW + PUTCODE)
  @Operation(summary = "Fetch a peer review", tags = "Peer reviews")
  @ApiResponse(responseCode = "200", description = "The peer review", content = @Content(schema = @Schema(implementation = PeerReview.class)))
  public Response viewPeerReview(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewPeerReview(orcid, putCode);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(PEER_REVIEW_SUMMARY + PUTCODE)
  @Operation(summary = "Fetch a peer review summary", tags = "Peer reviews")
  @ApiResponse(responseCode = "200", description = "The summary of the peer review", content = @Content(schema = @Schema(implementation = PeerReviewSummary.class)))
  public Response viewPeerReviewSummary(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewPeerReviewSummary(orcid, putCode);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(PEER_REVIEWS)
  @Operation(summary = "Fetch all peer reviews", tags = "Peer reviews")
  @ApiResponse(responseCode = "200", description = "The record's peer reviews", content = @Content(schema = @Schema(implementation = PeerReviews.class)))
  public Response viewPeerReviews(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewPeerReviews(orcid);
  }
  
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(RESEARCHER_URLS)
  @Operation(summary = "Fetch all researcher URLs", tags = "Researcher URLs")
  @ApiResponse(responseCode = "200", description = "The record's researcher URLs", content = @Content(schema = @Schema(implementation = ResearcherUrls.class)))
  public Response viewResearcherUrls(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewResearcherUrls(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(RESEARCHER_URLS + PUTCODE)
  @Operation(summary = "Fetch a researcher URL", tags = "Researcher URLs")
  @ApiResponse(responseCode = "200", description = "The researcher URL", content = @Content(schema = @Schema(implementation = ResearcherUrl.class)))
  public Response viewResearcherUrl(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewResearcherUrl(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(EMAIL)
  @Operation(summary = "Fetch all emails", tags = "Emails")
  @ApiResponse(responseCode = "200", description = "The record's emails", content = @Content(schema = @Schema(implementation = Emails.class)))
  public Response viewEmails(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewEmails(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(PERSONAL_DETAILS)
  @Operation(summary = "Fetch personal details", tags = "Personal details")
  @ApiResponse(responseCode = "200", description = "The record's name, other names and biography", content = @Content(schema = @Schema(implementation = PersonalDetails.class)))
  public Response viewPersonalDetails(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewPersonalDetails(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(OTHER_NAMES)
  @Operation(summary = "Fetch all other names", tags = "Other names")
  @ApiResponse(responseCode = "200", description = "The record's other names", content = @Content(schema = @Schema(implementation = OtherNames.class)))
  public Response viewOtherNames(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewOtherNames(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(OTHER_NAMES + PUTCODE)
  @Operation(summary = "Fetch an other name", tags = "Other names")
  @ApiResponse(responseCode = "200", description = "The other name", content = @Content(schema = @Schema(implementation = OtherName.class)))
  public Response viewOtherName(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewOtherName(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(EXTERNAL_IDENTIFIERS)
  @Operation(summary = "Fetch all person external identifiers", tags = "External identifiers")
  @ApiResponse(responseCode = "200", description = "The record's person external identifiers", content = @Content(schema = @Schema(implementation = PersonExternalIdentifiers.class)))
  public Response viewExternalIdentifiers(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewExternalIdentifiers(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(EXTERNAL_IDENTIFIERS + PUTCODE)
  @Operation(summary = "Fetch a person external identifier", tags = "External identifiers")
  @ApiResponse(responseCode = "200", description = "The person external identifier", content = @Content(schema = @Schema(implementation = PersonExternalIdentifier.class)))
  public Response viewExternalIdentifier(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewExternalIdentifier(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(KEYWORDS)
  @Operation(summary = "Fetch all keywords", tags = "Keywords")
  @ApiResponse(responseCode = "200", description = "The record's keywords", content = @Content(schema = @Schema(implementation = Keywords.class)))
  public Response viewKeywords(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);  
      return serviceDelegator.viewKeywords(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(KEYWORDS + PUTCODE)
  @Operation(summary = "Fetch a keyword", tags = "Keywords")
  @ApiResponse(responseCode = "200", description = "The keyword", content = @Content(schema = @Schema(implementation = Keyword.class)))
  public Response viewKeyword(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewKeyword(orcid, Long.valueOf(putCode));
  }
         
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(ADDRESS)
  @Operation(summary = "Fetch all addresses", tags = "Addresses")
  @ApiResponse(responseCode = "200", description = "The record's addresses", content = @Content(schema = @Schema(implementation = Addresses.class)))
  public Response viewAddresses(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewAddresses(orcid);
  }
          
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(ADDRESS + PUTCODE)
  @Operation(summary = "Fetch an address", tags = "Addresses")
  @ApiResponse(responseCode = "200", description = "The address", content = @Content(schema = @Schema(implementation = Address.class)))
  public Response viewAddress(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewAddress(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(BIOGRAPHY)
  @Operation(summary = "Fetch the biography", tags = "Biography")
  @ApiResponse(responseCode = "200", description = "The biography", content = @Content(schema = @Schema(implementation = Biography.class)))
  @ApiResponse(responseCode = "404", description = "The record has no biography (error 9041), or no record has this iD (9016)", content = @Content(schema = @Schema(implementation = OrcidError.class)))
  public Response viewBiography(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewBiography(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(PERSON)
  @Operation(summary = "Fetch the person section", tags = "Person")
  @ApiResponse(responseCode = "200", description = "The person section of the record", content = @Content(schema = @Schema(implementation = Person.class)))
  public Response viewPerson(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewPerson(orcid);
  }
  
  //Record 
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON, JSON_LD })
  @Path(OrcidApiConstants.RECORD_SIMPLE)
  @Operation(summary = "Fetch the record", tags = "Record")
  @ApiResponse(responseCode = "200", description = "The record", content = { @Content(schema = @Schema(implementation = Record.class)),
          @Content(mediaType = "application/ld+json", schema = @Schema(type = "object", description = "Schema.org JSON-LD")) })
  public Response viewRecord(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewRecord(orcid);
  }
  
  //Record 
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(OrcidApiConstants.RECORD_RECORD)
  @Operation(summary = "Fetch the record at its /record path", tags = "Record")
  @ApiResponse(responseCode = "200", description = "The record", content = @Content(schema = @Schema(implementation = Record.class)))
  public Response viewRecordRecord(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewRecord(orcid);
  }
  
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(SEARCH_PATH)
  @Operation(summary = "Search records", tags = "Search")
  @Parameter(name = "start", in = ParameterIn.QUERY, description = "Offset of the first result (0-based).", schema = @Schema(type = "integer"))
  @Parameter(name = "rows", in = ParameterIn.QUERY, description = "Number of results to return (maximum 1000).", schema = @Schema(type = "integer"))
  @ApiResponse(responseCode = "200", description = "The iDs of the matching records", content = @Content(schema = @Schema(implementation = Search.class)))
  @ApiResponse(responseCode = "400", description = "The start or rows parameter is not a number, or is out of range: rows 1,000 at most; start 10,000 at most without a token (error 9012)", content = @Content(schema = @Schema(implementation = OrcidError.class)))
  public Response searchByQuery(@Parameter(description = "Solr query, for example `family-name:Carberry` or `doi-self:10.1087/20120404`.") @QueryParam("q") @DefaultValue("") String query, @Context UriInfo uriInfo) throws ParseException{
      serviceDelegator.trackEvents(httpRequest);
      Map<String, List<String>> solrParams = new HashMap<>(uriInfo.getQueryParameters());
      Response jsonQueryResults = serviceDelegator.searchByQuery(solrParams);
      return jsonQueryResults;
  }
  
  @GET
  @Produces(TEXT_CSV)
  @Path(CSV_SEARCH_PATH)
  @Operation(summary = "Search records, as CSV", tags = "Search")
  @Parameter(name = "start", in = ParameterIn.QUERY, description = "Offset of the first result (0-based).", schema = @Schema(type = "integer"))
  @Parameter(name = "rows", in = ParameterIn.QUERY, description = "Number of results to return (maximum 1000).", schema = @Schema(type = "integer"))
  @Parameter(name = "fl", in = ParameterIn.QUERY, description = "Comma-separated fields to return, for example `orcid,given-names,family-name`.", schema = @Schema(type = "string"))
  @ApiResponse(responseCode = "200", description = "One row per result, with the requested fields", content = @Content(mediaType = "text/csv", schema = @Schema(type = "string")))
  public Response searchByQueryCSV(@Parameter(description = "Solr query, for example `family-name:Carberry` or `doi-self:10.1087/20120404`.") @QueryParam("q") @DefaultValue("") String query, @Context UriInfo uriInfo) throws ParseException{
      serviceDelegator.trackEvents(httpRequest);
      Map<String, List<String>> solrParams = new HashMap<>(uriInfo.getQueryParameters());
      Response csvQueryResults = serviceDelegator.searchByQueryCSV(solrParams);
      return csvQueryResults;
  }
  
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(EXPANDED_SEARCH_PATH)
  @Operation(summary = "Search records, with more fields per result", tags = "Search")
  @Parameter(name = "start", in = ParameterIn.QUERY, description = "Offset of the first result (0-based).", schema = @Schema(type = "integer"))
  @Parameter(name = "rows", in = ParameterIn.QUERY, description = "Number of results to return (maximum 1000).", schema = @Schema(type = "integer"))
  @ApiResponse(responseCode = "200", description = "Matching records with their names, emails and institutions", content = @Content(schema = @Schema(implementation = ExpandedSearch.class)))
  @ApiResponse(responseCode = "400", description = "The start or rows parameter is not a number, or is out of range: rows 1,000 at most; start 10,000 at most without a token (error 9012)", content = @Content(schema = @Schema(implementation = OrcidError.class)))
  public Response expandedSearchByQuery(@Parameter(description = "Solr query, for example `family-name:Carberry` or `doi-self:10.1087/20120404`.") @QueryParam("q") @DefaultValue("") String query, @Context UriInfo uriInfo) throws ParseException{
      serviceDelegator.trackEvents(httpRequest);
      Map<String, List<String>> solrParams = new HashMap<>(uriInfo.getQueryParameters());
      Response queryResults = serviceDelegator.expandedSearchByQuery(solrParams);
      return queryResults;
  }
  @GET
  @Path(CLIENT_PATH)
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Operation(summary = "Fetch a client's details", tags = "Clients")
  @ApiResponse(responseCode = "200", description = "The client's public details", content = @Content(schema = @Schema(implementation = ClientSummary.class)))
  public Response viewClient(@PathParam("client_id") String clientId) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewClient(clientId);
  }
  
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(DISTINCTION + PUTCODE)
  @Operation(summary = "Fetch a distinction", tags = "Distinction")
  @ApiResponse(responseCode = "200", description = "The distinction", content = @Content(schema = @Schema(implementation = Distinction.class)))
  public Response viewDistinction(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewDistinction(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(DISTINCTIONS)
  @Operation(summary = "Fetch all distinctions", tags = "Distinction")
  @ApiResponse(responseCode = "200", description = "The record's distinctions", content = @Content(schema = @Schema(implementation = Distinctions.class)))
  public Response viewDistinctions(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewDistinctions(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(DISTINCTION_SUMMARY + PUTCODE)
  @Operation(summary = "Fetch a distinction summary", tags = "Distinction")
  @ApiResponse(responseCode = "200", description = "The summary of the distinction", content = @Content(schema = @Schema(implementation = DistinctionSummary.class)))
  public Response viewDistinctionSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewDistinctionSummary(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(INVITED_POSITION + PUTCODE)
  @Operation(summary = "Fetch an invited position", tags = "Invited position")
  @ApiResponse(responseCode = "200", description = "The invited position", content = @Content(schema = @Schema(implementation = InvitedPosition.class)))
  public Response viewInvitedPosition(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewInvitedPosition(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(INVITED_POSITIONS)
  @Operation(summary = "Fetch all invited positions", tags = "Invited position")
  @ApiResponse(responseCode = "200", description = "The record's invited positions", content = @Content(schema = @Schema(implementation = InvitedPositions.class)))
  public Response viewInvitedPositions(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewInvitedPositions(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(INVITED_POSITION_SUMMARY + PUTCODE)
  @Operation(summary = "Fetch an invited position summary", tags = "Invited position")
  @ApiResponse(responseCode = "200", description = "The summary of the invited position", content = @Content(schema = @Schema(implementation = InvitedPositionSummary.class)))
  public Response viewInvitedPositionSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewInvitedPositionSummary(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(MEMBERSHIP + PUTCODE)
  @Operation(summary = "Fetch a membership", tags = "Membership")
  @ApiResponse(responseCode = "200", description = "The membership", content = @Content(schema = @Schema(implementation = Membership.class)))
  public Response viewMembership(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewMembership(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(MEMBERSHIPS)
  @Operation(summary = "Fetch all memberships", tags = "Membership")
  @ApiResponse(responseCode = "200", description = "The record's memberships", content = @Content(schema = @Schema(implementation = Memberships.class)))
  public Response viewMemberships(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewMemberships(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(MEMBERSHIP_SUMMARY + PUTCODE)
  @Operation(summary = "Fetch a membership summary", tags = "Membership")
  @ApiResponse(responseCode = "200", description = "The summary of the membership", content = @Content(schema = @Schema(implementation = MembershipSummary.class)))
  public Response viewMembershipSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewMembershipSummary(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(QUALIFICATION + PUTCODE)
  @Operation(summary = "Fetch a qualification", tags = "Qualification")
  @ApiResponse(responseCode = "200", description = "The qualification", content = @Content(schema = @Schema(implementation = Qualification.class)))
  public Response viewQualification(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewQualification(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(QUALIFICATIONS)
  @Operation(summary = "Fetch all qualifications", tags = "Qualification")
  @ApiResponse(responseCode = "200", description = "The record's qualifications", content = @Content(schema = @Schema(implementation = Qualifications.class)))
  public Response viewQualifications(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewQualifications(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(QUALIFICATION_SUMMARY + PUTCODE)
  @Operation(summary = "Fetch a qualification summary", tags = "Qualification")
  @ApiResponse(responseCode = "200", description = "The summary of the qualification", content = @Content(schema = @Schema(implementation = QualificationSummary.class)))
  public Response viewQualificationSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewQualificationSummary(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(SERVICE + PUTCODE)
  @Operation(summary = "Fetch a service", tags = "Service")
  @ApiResponse(responseCode = "200", description = "The service", content = @Content(schema = @Schema(implementation = Service.class)))
  public Response viewService(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewService(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(SERVICES)
  @Operation(summary = "Fetch all services", tags = "Service")
  @ApiResponse(responseCode = "200", description = "The record's services", content = @Content(schema = @Schema(implementation = Services.class)))
  public Response viewServices(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewServices(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(SERVICE_SUMMARY + PUTCODE)
  @Operation(summary = "Fetch a service summary", tags = "Service")
  @ApiResponse(responseCode = "200", description = "The summary of the service", content = @Content(schema = @Schema(implementation = ServiceSummary.class)))
  public Response viewServiceSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewServiceSummary(orcid, Long.valueOf(putCode));
  }
  
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(RESEARCH_RESOURCE + PUTCODE)
  @Operation(summary = "Fetch a research resource", tags = "Research resources")
  @ApiResponse(responseCode = "200", description = "The research resource", content = @Content(schema = @Schema(implementation = ResearchResource.class)))
  public Response viewResearchResource(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewResearchResource(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(RESEARCH_RESOURCES)
  @Operation(summary = "Fetch all research resources", tags = "Research resources")
  @ApiResponse(responseCode = "200", description = "The record's research resources", content = @Content(schema = @Schema(implementation = ResearchResources.class)))
  public Response viewResearchResources(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewResearchResources(orcid);
  }
  
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(RESEARCH_RESOURCE_SUMMARY + PUTCODE)
  @Operation(summary = "Fetch a research resource summary", tags = "Research resources")
  @ApiResponse(responseCode = "200", description = "The summary of the research resource", content = @Content(schema = @Schema(implementation = ResearchResourceSummary.class)))
  public Response viewResearchResourceSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewResearchResourceSummary(orcid, Long.valueOf(putCode));
  }
  
}
