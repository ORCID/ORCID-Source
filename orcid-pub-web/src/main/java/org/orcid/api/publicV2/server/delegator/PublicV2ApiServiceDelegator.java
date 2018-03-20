package org.orcid.api.publicV2.server.delegator;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface PublicV2ApiServiceDelegator<EDUCATION, EMPLOYMENT, EXTERNAL_IDENTIFIER, FUNDING, GROUP_ID_RECORD, OTHER_NAME, PEER_REVIEW, RESEARCHER_URL, WORK> {

    static final String LATEST_V2_VERSION = "2.0";

    Response viewStatusText();

    Response viewActivities(String orcid);

    Response viewWork(String orcid, Long putCode);
    
    Response viewWorks(String orcid);
    
    Response viewWorkCitation(String orcid, Long putCode);

    Response viewWorkSummary(String orcid, Long putCode);

    Response viewFunding(String orcid, Long putCode);
    
    Response viewFundings(String orcid);

    Response viewFundingSummary(String orcid, Long putCode);

    Response viewEducation(String orcid, Long putCode);
    
    Response viewEducations(String orcid);

    Response viewEducationSummary(String orcid, Long putCode);

    Response viewEmployment(String orcid, Long putCode);
    
    Response viewEmployments(String orcid);

    Response viewEmploymentSummary(String orcid, Long putCode);

    Response viewPeerReview(String orcid, Long putCode);
    
    Response viewPeerReviews(String orcid);

    Response viewPeerReviewSummary(String orcid, Long putCode);

    Response viewGroupIdRecord(Long putCode);

    Response viewGroupIdRecords(String pageSize, String pageNum);

    Response viewResearcherUrls(String orcid);

    Response viewResearcherUrl(String orcid, Long putCode);

    Response viewEmails(String orcid);

    Response viewOtherNames(String orcid);

    Response viewOtherName(String orcid, Long putCode);

    Response viewPersonalDetails(String orcid);

    Response viewExternalIdentifiers(String orcid);

    Response viewExternalIdentifier(String orcid, Long putCode);
    
    Response viewBiography(String orcid);
    
    Response viewKeywords(String orcid);
    
    Response viewKeyword(String orcid, Long putCode);
    
    Response viewAddresses(String orcid);
    
    Response viewAddress(String orcid, Long putCode);
    
    Response viewPerson(String orcid);
    
    Response viewRecord(String orcid);

    Response searchByQuery(Map<String, List<String>> solrParams);

    Response viewClient(String clientId);
    
    Response viewBulkWorks(String orcid, String putCodes);

}
