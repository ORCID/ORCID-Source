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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

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
  @Produces(value = { MediaType.APPLICATION_JSON })
  @Path(STATUS_PATH)
  public Response viewStatusSimple() {
      httpRequest.setAttribute("skipAccessLog", true);
      httpRequest.setAttribute("isMonitoring", true);
      return serviceDelegator.viewStatusSimple();
  }
  
  @GET
  @Produces(value = { MediaType.APPLICATION_JSON })
  @Path(PUB_STATUS_PATH)
  public Response viewStatusJson() {
      httpRequest.setAttribute("skipAccessLog", true);
      httpRequest.setAttribute("isMonitoring", true);
      return serviceDelegator.viewStatus();
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(ACTIVITIES)
  public Response viewActivities(@PathParam("orcid") String orcid, @Context HttpServletRequest httpRequest) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewActivities(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON,
          OrcidApiConstants.APPLICATION_CITEPROC })
  @Path(WORK + PUTCODE)
  public Response viewWork(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode, @Context HttpServletRequest httpRequest) {
      serviceDelegator.trackEvents(httpRequest);
      if (OrcidApiConstants.APPLICATION_CITEPROC.equals(httpRequest.getHeader("Accept")))
          return serviceDelegator.viewWorkCitation(orcid, putCode);
      return serviceDelegator.viewWork(orcid, putCode);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(WORK_SUMMARY + PUTCODE)
  public Response viewWorkSummary(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewWorkSummary(orcid, putCode);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(WORKS)
  public Response viewWorks(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewWorks(orcid);
  }
  
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(BULK_WORKS)
  public Response viewSpecifiedWorks(@PathParam("orcid") String orcid, @PathParam("putCodes") String putCodes) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewBulkWorks(orcid, putCodes);
  }
  
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(FUNDING + PUTCODE)
  public Response viewFunding(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewFunding(orcid, putCode);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(FUNDING_SUMMARY + PUTCODE)
  public Response viewFundingSummary(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewFundingSummary(orcid, putCode);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(FUNDINGS)
  public Response viewFundings(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewFundings(orcid);
  }
  
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(EDUCATION + PUTCODE)
  public Response viewEducation(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewEducation(orcid, putCode);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(EDUCATION_SUMMARY + PUTCODE)
  public Response viewEducationSummary(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewEducationSummary(orcid, putCode);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(EDUCATIONS)
  public Response viewEducations(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewEducations(orcid);
  }
  
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(EMPLOYMENT + PUTCODE)
  public Response viewEmployment(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewEmployment(orcid, putCode);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(EMPLOYMENT_SUMMARY + PUTCODE)
  public Response viewEmploymentSummary(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewEmploymentSummary(orcid, putCode);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(EMPLOYMENTS)
  public Response viewEmployments(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewEmployments(orcid);
  }
  
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(PEER_REVIEW + PUTCODE)
  public Response viewPeerReview(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewPeerReview(orcid, putCode);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(PEER_REVIEW_SUMMARY + PUTCODE)
  public Response viewPeerReviewSummary(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewPeerReviewSummary(orcid, putCode);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(PEER_REVIEWS)
  public Response viewPeerReviews(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewPeerReviews(orcid);
  }
  
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(RESEARCHER_URLS)
  public Response viewResearcherUrls(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewResearcherUrls(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(RESEARCHER_URLS + PUTCODE)
  public Response viewResearcherUrl(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewResearcherUrl(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(EMAIL)
  public Response viewEmails(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewEmails(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(PERSONAL_DETAILS)
  public Response viewPersonalDetails(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewPersonalDetails(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(OTHER_NAMES)
  public Response viewOtherNames(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewOtherNames(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(OTHER_NAMES + PUTCODE)
  public Response viewOtherName(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewOtherName(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(EXTERNAL_IDENTIFIERS)
  public Response viewExternalIdentifiers(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewExternalIdentifiers(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(EXTERNAL_IDENTIFIERS + PUTCODE)
  public Response viewExternalIdentifier(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewExternalIdentifier(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(KEYWORDS)
  public Response viewKeywords(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);  
      return serviceDelegator.viewKeywords(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(KEYWORDS + PUTCODE)
  public Response viewKeyword(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewKeyword(orcid, Long.valueOf(putCode));
  }
         
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(ADDRESS)
  public Response viewAddresses(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewAddresses(orcid);
  }
          
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(ADDRESS + PUTCODE)
  public Response viewAddress(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewAddress(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(BIOGRAPHY)
  public Response viewBiography(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewBiography(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(PERSON)
  public Response viewPerson(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewPerson(orcid);
  }
  
  //Record 
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON, JSON_LD })
  @Path(OrcidApiConstants.RECORD_SIMPLE)
  public Response viewRecord(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewRecord(orcid);
  }
  
  //Record 
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(OrcidApiConstants.RECORD_RECORD)
  public Response viewRecordRecord(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewRecord(orcid);
  }
  
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(SEARCH_PATH)
  public Response searchByQuery(@QueryParam("q") @DefaultValue("") String query, @Context UriInfo uriInfo) {
      serviceDelegator.trackEvents(httpRequest);
      Map<String, List<String>> solrParams = new HashMap<>(uriInfo.getQueryParameters());
      Response jsonQueryResults = serviceDelegator.searchByQuery(solrParams);
      return jsonQueryResults;
  }
  
  @GET
  @Produces(TEXT_CSV)
  @Path(CSV_SEARCH_PATH)
  public Response searchByQueryCSV(@QueryParam("q") @DefaultValue("") String query, @Context UriInfo uriInfo) {
      serviceDelegator.trackEvents(httpRequest);
      Map<String, List<String>> solrParams = new HashMap<>(uriInfo.getQueryParameters());
      Response csvQueryResults = serviceDelegator.searchByQueryCSV(solrParams);
      return csvQueryResults;
  }
  
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(EXPANDED_SEARCH_PATH)
  public Response expandedSearchByQuery(@QueryParam("q") @DefaultValue("") String query, @Context UriInfo uriInfo) {
      serviceDelegator.trackEvents(httpRequest);
      Map<String, List<String>> solrParams = new HashMap<>(uriInfo.getQueryParameters());
      Response queryResults = serviceDelegator.expandedSearchByQuery(solrParams);
      return queryResults;
  }
  @GET
  @Path(CLIENT_PATH)
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  public Response viewClient(@PathParam("client_id") String clientId) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewClient(clientId);
  }
  
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(DISTINCTION + PUTCODE)
  public Response viewDistinction(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewDistinction(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(DISTINCTIONS)
  public Response viewDistinctions(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewDistinctions(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(DISTINCTION_SUMMARY + PUTCODE)
  public Response viewDistinctionSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewDistinctionSummary(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(INVITED_POSITION + PUTCODE)
  public Response viewInvitedPosition(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewInvitedPosition(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(INVITED_POSITIONS)
  public Response viewInvitedPositions(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewInvitedPositions(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(INVITED_POSITION_SUMMARY + PUTCODE)
  public Response viewInvitedPositionSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewInvitedPositionSummary(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(MEMBERSHIP + PUTCODE)
  public Response viewMembership(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewMembership(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(MEMBERSHIPS)
  public Response viewMemberships(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewMemberships(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(MEMBERSHIP_SUMMARY + PUTCODE)
  public Response viewMembershipSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewMembershipSummary(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(QUALIFICATION + PUTCODE)
  public Response viewQualification(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewQualification(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(QUALIFICATIONS)
  public Response viewQualifications(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewQualifications(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(QUALIFICATION_SUMMARY + PUTCODE)
  public Response viewQualificationSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewQualificationSummary(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(SERVICE + PUTCODE)
  public Response viewService(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewService(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(SERVICES)
  public Response viewServices(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewServices(orcid);
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(SERVICE_SUMMARY + PUTCODE)
  public Response viewServiceSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewServiceSummary(orcid, Long.valueOf(putCode));
  }
  
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(RESEARCH_RESOURCE + PUTCODE)
  public Response viewResearchResource(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewResearchResource(orcid, Long.valueOf(putCode));
  }

  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(RESEARCH_RESOURCES)
  public Response viewResearchResources(@PathParam("orcid") String orcid) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewResearchResources(orcid);
  }
  
  @GET
  @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
  @Path(RESEARCH_RESOURCE_SUMMARY + PUTCODE)
  public Response viewResearchResourceSummary(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
      serviceDelegator.trackEvents(httpRequest);
      return serviceDelegator.viewResearchResourceSummary(orcid, Long.valueOf(putCode));
  }
  
}
