package org.orcid.api.publicV2.server;

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
import static org.orcid.core.api.OrcidApiConstants.EXTERNAL_IDENTIFIERS;
import static org.orcid.core.api.OrcidApiConstants.FUNDING;
import static org.orcid.core.api.OrcidApiConstants.FUNDINGS;
import static org.orcid.core.api.OrcidApiConstants.FUNDING_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.JSON_LD;
import static org.orcid.core.api.OrcidApiConstants.KEYWORDS;
import static org.orcid.core.api.OrcidApiConstants.ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.OTHER_NAMES;
import static org.orcid.core.api.OrcidApiConstants.PEER_REVIEW;
import static org.orcid.core.api.OrcidApiConstants.PEER_REVIEWS;
import static org.orcid.core.api.OrcidApiConstants.PEER_REVIEW_SUMMARY;
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
import org.orcid.api.publicV2.server.delegator.PublicV2ApiServiceDelegator;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.jaxb.model.client_v2.ClientSummary;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopeConstants;
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
import org.orcid.jaxb.model.record_v2.Biography;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Keywords;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.OtherNames;
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
import org.springframework.stereotype.Component;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@Component
@Path("/v2.0")
public class PublicV2ApiServiceImplV2_0 {

    protected PublicV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work> serviceDelegator;

    @Resource
    protected SwaggerUIBuilder swaggerUIBuilder;

    public void setServiceDelegator(
            PublicV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work> serviceDelegator) {
        this.serviceDelegator = serviceDelegator;
    }

    /**
     * Serves the Swagger UI HTML page
     * 
     * @return a 200 response containing the HTML
     */    
    @GET
    @Produces(value = { MediaType.TEXT_HTML })
    @Path("/")
    @Operation(description = "Fetch the HTML swagger UI interface", hidden = true)
    public Response viewSwagger() {
        return swaggerUIBuilder.build();
    }    

    @GET
    @Produces(value = { MediaType.TEXT_PLAIN })
    @Path(STATUS_PATH)
    @Operation(description = "Check the server status", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = String.class)))})
    public Response viewStatusText() {
        return serviceDelegator.viewStatusText();
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ACTIVITIES)
    @Operation(description = "Fetch all Activities", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = ActivitiesSummary.class)))})
    public Response viewActivities(@PathParam("orcid") String orcid, @Context HttpServletRequest httpRequest) {
        return serviceDelegator.viewActivities(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON,
            OrcidApiConstants.APPLICATION_CITEPROC })
    @Path(WORK + PUTCODE)
    @Operation(description = "Fetch a Work", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = Work.class)))})
    public Response viewWork(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode, @Context HttpServletRequest httpRequest) {
        if (OrcidApiConstants.APPLICATION_CITEPROC.equals(httpRequest.getHeader("Accept")))
            return serviceDelegator.viewWorkCitation(orcid, putCode);
        return serviceDelegator.viewWork(orcid, putCode);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORK_SUMMARY + PUTCODE)
    @Operation(description = "Fetch a Work Summary", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = WorkSummary.class)))})
    public Response viewWorkSummary(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
        return serviceDelegator.viewWorkSummary(orcid, putCode);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(WORKS)
    @Operation(description = "Fetch all works", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = Works.class)))})
    public Response viewWorks(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewWorks(orcid);
    }
    
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(BULK_WORKS)
    @Operation(description = "Fetch specified works", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = WorkBulk.class)))})
    public Response viewSpecifiedWorks(@PathParam("orcid") String orcid, @PathParam("putCodes") String putCodes) {
        return serviceDelegator.viewBulkWorks(orcid, putCodes);
    }
    
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING + PUTCODE)
    @Operation(description = "Fetch a Funding", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = Funding.class)))})
    public Response viewFunding(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
        return serviceDelegator.viewFunding(orcid, putCode);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDING_SUMMARY + PUTCODE)
    @Operation(description = "Fetch a Funding Summary", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = FundingSummary.class)))})
    public Response viewFundingSummary(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
        return serviceDelegator.viewFundingSummary(orcid, putCode);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(FUNDINGS)
    @Operation(description = "Fetch all fundings", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = Fundings.class)))})    
    public Response viewFundings(@PathParam("orcid") String orcid) {        
        return serviceDelegator.viewFundings(orcid);
    }
    
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATION + PUTCODE)
    @Operation(description = "Fetch an Education", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = Education.class)))})
    public Response viewEducation(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
        return serviceDelegator.viewEducation(orcid, putCode);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATION_SUMMARY + PUTCODE)
    @Operation(description = "Fetch an Education Summary", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = EducationSummary.class)))})
    public Response viewEducationSummary(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
        return serviceDelegator.viewEducationSummary(orcid, putCode);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EDUCATIONS)
    @Operation(description = "Fetch all educations", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = Educations.class)))})    
    public Response viewEducations(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewEducations(orcid);
    }
    
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENT + PUTCODE)
    @Operation(description = "Fetch an Employment", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = Employment.class)))})
    public Response viewEmployment(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
        return serviceDelegator.viewEmployment(orcid, putCode);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENT_SUMMARY + PUTCODE)
    @Operation(description = "Fetch an Employment Summary", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = EmploymentSummary.class)))})
    public Response viewEmploymentSummary(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
        return serviceDelegator.viewEmploymentSummary(orcid, putCode);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMPLOYMENTS)
    @Operation(description = "Fetch all employments", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = Employments.class)))})    
    public Response viewEmployments(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewEmployments(orcid);
    }
    
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEW + PUTCODE)
    @Operation(description = "Fetch a Peer Review", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = PeerReview.class)))})
    public Response viewPeerReview(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
        return serviceDelegator.viewPeerReview(orcid, putCode);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEW_SUMMARY + PUTCODE)
    @Operation(description = "Fetch a Peer Review Summary", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = PeerReviewSummary.class)))})
    public Response viewPeerReviewSummary(@PathParam("orcid") String orcid, @PathParam("putCode") Long putCode) {
        return serviceDelegator.viewPeerReviewSummary(orcid, putCode);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PEER_REVIEWS)
    @Operation(description = "Fetch all peer reviews", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = PeerReviews.class)))})    
    public Response viewPeerReviews(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewPeerReviews(orcid);
    }
    
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(RESEARCHER_URLS)
    @Operation(description = "Fetch all researcher urls for an ORCID ID", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = ResearcherUrls.class)))})
    public Response viewResearcherUrls(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewResearcherUrls(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(RESEARCHER_URLS + PUTCODE)
    @Operation(description = "Fetch one researcher url for an ORCID ID", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = ResearcherUrl.class)))})
    public Response viewResearcherUrl(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewResearcherUrl(orcid, Long.valueOf(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EMAIL)
    @Operation(description = "Fetch all emails for an ORCID ID", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = Emails.class)))})
    public Response viewEmails(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewEmails(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PERSONAL_DETAILS)
    @Operation(description = "Fetch personal details for an ORCID ID", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = PersonalDetails.class)))})
    public Response viewPersonalDetails(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewPersonalDetails(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(OTHER_NAMES)
    @Operation(description = "Fetch Other names", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = OtherNames.class)))})
    public Response viewOtherNames(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewOtherNames(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(OTHER_NAMES + PUTCODE)
    @Operation(description = "Fetch Other name", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = OtherName.class)))})
    public Response viewOtherName(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewOtherName(orcid, Long.valueOf(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EXTERNAL_IDENTIFIERS)
    @Operation(description = "Fetch all person external identifiers", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = PersonExternalIdentifiers.class)))})
    public Response viewExternalIdentifiers(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewExternalIdentifiers(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(EXTERNAL_IDENTIFIERS + PUTCODE)
    @Operation(description = "Fetch external identifier", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = PersonExternalIdentifier.class)))})
    public Response viewExternalIdentifier(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewExternalIdentifier(orcid, Long.valueOf(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(KEYWORDS)
    @Operation(description = "Fetch keywords", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = Keywords.class)))})
    public Response viewKeywords(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewKeywords(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(KEYWORDS + PUTCODE)
    @Operation(description = "Fetch keyword", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = Keyword.class)))})
    public Response viewKeyword(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewKeyword(orcid, Long.valueOf(putCode));
    }
           
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ADDRESS)
    @Operation(description = "Fetch all addresses", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = Addresses.class)))})
    public Response viewAddresses(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewAddresses(orcid);
    }
            
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(ADDRESS + PUTCODE)
    @Operation(description = "Fetch an address", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = Address.class)))})
    public Response viewAddress(@PathParam("orcid") String orcid, @PathParam("putCode") String putCode) {
        return serviceDelegator.viewAddress(orcid, Long.valueOf(putCode));
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(BIOGRAPHY)
    @Operation(description = "Get biography details", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = Biography.class)))})
    public Response viewBiography(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewBiography(orcid);
    }

    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(PERSON)
    @Operation(description = "Fetch person details", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = Person.class)))})
    public Response viewPerson(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewPerson(orcid);
    }
    
    //Record 
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON , JSON_LD})
    @Path(OrcidApiConstants.RECORD_SIMPLE)
    @Operation(description = "Fetch record details", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = Record.class)))})
    public Response viewRecord(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewRecord(orcid);
    }

    //Record 
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(OrcidApiConstants.RECORD_RECORD)
    @Operation(description = "Fetch record details2", hidden=true)
    public Response viewRecordRecord(@PathParam("orcid") String orcid) {
        return serviceDelegator.viewRecord(orcid);
    }
    
    @GET
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path(SEARCH_PATH)
    @Operation(description = "Search records", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = Search.class)))})
    public Response searchByQuery(@QueryParam("q") @DefaultValue("") String query, @Context UriInfo uriInfo) {
        Map<String, List<String>> solrParams = uriInfo.getQueryParameters();
        Response jsonQueryResults = serviceDelegator.searchByQuery(solrParams);
        return jsonQueryResults;
    }

    @GET
    @Path(CLIENT_PATH)
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Operation(description = "Fetch client details", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = ClientSummary.class)))})
    public Response viewClient(@PathParam("client_id") String clientId) {
        return serviceDelegator.viewClient(clientId);
    }
    
}
