package org.orcid.integration.blackbox.api.v3.dev1;

import static org.orcid.core.api.OrcidApiConstants.ACTIVITIES;
import static org.orcid.core.api.OrcidApiConstants.ADDRESS;
import static org.orcid.core.api.OrcidApiConstants.BIOGRAPHY;
import static org.orcid.core.api.OrcidApiConstants.DISTINCTION;
import static org.orcid.core.api.OrcidApiConstants.EDUCATION;
import static org.orcid.core.api.OrcidApiConstants.EMAIL;
import static org.orcid.core.api.OrcidApiConstants.EMPLOYMENT;
import static org.orcid.core.api.OrcidApiConstants.EXTERNAL_IDENTIFIERS;
import static org.orcid.core.api.OrcidApiConstants.FUNDING;
import static org.orcid.core.api.OrcidApiConstants.GROUP_ID_RECORD;
import static org.orcid.core.api.OrcidApiConstants.INVITED_POSITION;
import static org.orcid.core.api.OrcidApiConstants.KEYWORDS;
import static org.orcid.core.api.OrcidApiConstants.MEMBERSHIP;
import static org.orcid.core.api.OrcidApiConstants.OTHER_NAMES;
import static org.orcid.core.api.OrcidApiConstants.PEER_REVIEW;
import static org.orcid.core.api.OrcidApiConstants.PERSON;
import static org.orcid.core.api.OrcidApiConstants.PERSONAL_DETAILS;
import static org.orcid.core.api.OrcidApiConstants.PROFILE_ROOT_PATH;
import static org.orcid.core.api.OrcidApiConstants.PUTCODE;
import static org.orcid.core.api.OrcidApiConstants.QUALIFICATION;
import static org.orcid.core.api.OrcidApiConstants.RESEARCHER_URLS;
import static org.orcid.core.api.OrcidApiConstants.SERVICE;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.WORK;
import static org.orcid.core.api.OrcidApiConstants.WORKS;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.UriBuilder;

import org.orcid.api.common.OrcidClientHelper;
import org.orcid.jaxb.model.v3.dev1.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.dev1.record.Address;
import org.orcid.jaxb.model.v3.dev1.record.Distinction;
import org.orcid.jaxb.model.v3.dev1.record.Education;
import org.orcid.jaxb.model.v3.dev1.record.Employment;
import org.orcid.jaxb.model.v3.dev1.record.Funding;
import org.orcid.jaxb.model.v3.dev1.record.InvitedPosition;
import org.orcid.jaxb.model.v3.dev1.record.Keyword;
import org.orcid.jaxb.model.v3.dev1.record.Membership;
import org.orcid.jaxb.model.v3.dev1.record.OtherName;
import org.orcid.jaxb.model.v3.dev1.record.PeerReview;
import org.orcid.jaxb.model.v3.dev1.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.dev1.record.Qualification;
import org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.dev1.record.Service;
import org.orcid.jaxb.model.v3.dev1.record.Work;
import org.orcid.jaxb.model.v3.dev1.record.WorkBulk;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Will Simpson
 *
 */
public class MemberV3Dev1ApiClientImpl {

    private OrcidClientHelper orcidClientHelper;

    public MemberV3Dev1ApiClientImpl(URI baseUri, Client c) throws URISyntaxException {
        orcidClientHelper = new OrcidClientHelper(baseUri, c);
    }    
    
    public ClientResponse viewLocationXml(URI location, String accessToken) throws URISyntaxException {
        return orcidClientHelper.getClientResponseWithToken(location, VND_ORCID_XML, accessToken);
    }

    public ClientResponse viewActivities(String orcid, String accessToken) {
        URI activitiesUri = UriBuilder.fromPath(ACTIVITIES).build(orcid);
        return orcidClientHelper.getClientResponseWithToken(activitiesUri, VND_ORCID_XML, accessToken);
    }
    
    public ClientResponse viewWorkXml(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(WORK + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }

    public ClientResponse createWorkXml(String orcid, Work work, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(WORK).build(orcid), VND_ORCID_XML, work, accessToken);
    }

    public ClientResponse createWorksXml(String orcid, WorkBulk bulk, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(WORKS).build(orcid), VND_ORCID_XML, bulk, accessToken);
    }

    public ClientResponse createWorkJson(String orcid, Work work, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(WORK).build(orcid), VND_ORCID_JSON, work, accessToken);
    }

    public ClientResponse createWorksJson(String orcid, WorkBulk bulk, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(WORKS).build(orcid), VND_ORCID_JSON, bulk, accessToken);
    }
    
    public ClientResponse updateWork(String orcid, Work work, String accessToken) {
        URI uri = UriBuilder.fromPath(WORK + PUTCODE).build(orcid, work.getPutCode());
        return orcidClientHelper.putClientResponseWithToken(uri, VND_ORCID_XML, work, accessToken);
    }
    
    public ClientResponse deleteWorkXml(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(WORK + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.deleteClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }
    
    public ClientResponse viewEducationXml(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(EDUCATION + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }

    public ClientResponse createEducationXml(String orcid, Education education, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(EDUCATION).build(orcid), VND_ORCID_XML, education, accessToken);
    }

    public ClientResponse createEducationJson(String orcid, Education education, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(EDUCATION).build(orcid), VND_ORCID_JSON, education, accessToken);
    }
        
    public ClientResponse updateEducation(String orcid, Education education, String accessToken) {
        URI uri = UriBuilder.fromPath(EDUCATION + PUTCODE).build(orcid, education.getPutCode());
        return orcidClientHelper.putClientResponseWithToken(uri, VND_ORCID_XML, education, accessToken);
    }
    
    public ClientResponse deleteEducationXml(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(EDUCATION + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.deleteClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }           
    
    public ClientResponse viewEmploymentXml(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(EMPLOYMENT + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }

    public ClientResponse createEmploymentXml(String orcid, Employment employment, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(EMPLOYMENT).build(orcid), VND_ORCID_XML, employment, accessToken);
    }

    public ClientResponse createEmploymentJson(String orcid, Employment employment, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(EMPLOYMENT).build(orcid), VND_ORCID_JSON, employment, accessToken);
    }
    
    public ClientResponse updateEmployment(String orcid, Employment employment, String accessToken) {
        URI uri = UriBuilder.fromPath(EMPLOYMENT + PUTCODE).build(orcid, employment.getPutCode());
        return orcidClientHelper.putClientResponseWithToken(uri, VND_ORCID_XML, employment, accessToken);
    }
    
    public ClientResponse deleteEmploymentXml(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(EMPLOYMENT + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.deleteClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }
        
    public ClientResponse viewFundingXml(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(FUNDING + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }

    public ClientResponse createFundingXml(String orcid, Funding funding, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(FUNDING).build(orcid), VND_ORCID_XML, funding, accessToken);
    }

    public ClientResponse createFundingJson(String orcid, Funding funding, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(FUNDING).build(orcid), VND_ORCID_JSON, funding, accessToken);
    }

    public ClientResponse updateFunding(String orcid, Funding funding, String accessToken) {
        URI uri = UriBuilder.fromPath(FUNDING + PUTCODE).build(orcid, funding.getPutCode());
        return orcidClientHelper.putClientResponseWithToken(uri, VND_ORCID_XML, funding, accessToken);
    }
    
    public ClientResponse deleteFundingXml(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(FUNDING + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.deleteClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }
                
    public ClientResponse updateLocationXml(URI location, String accessToken, Object jaxbRootElement){
        return orcidClientHelper.putClientResponseWithToken(location, VND_ORCID_XML, jaxbRootElement, accessToken);
    }   
    
    public ClientResponse viewPeerReviewXml(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(PEER_REVIEW + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }

    public ClientResponse createPeerReviewXml(String orcid, PeerReview peerReview, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(PEER_REVIEW).build(orcid), VND_ORCID_XML, peerReview, accessToken);
    }

    public ClientResponse createPeerReviewJson(String orcid, PeerReview peerReview, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(PEER_REVIEW).build(orcid), VND_ORCID_JSON, peerReview, accessToken);
    }
    
    public ClientResponse updatePeerReview(String orcid, PeerReview peerReview, String accessToken) {
        URI uri = UriBuilder.fromPath(PEER_REVIEW + PUTCODE).build(orcid, peerReview.getPutCode());
        return orcidClientHelper.putClientResponseWithToken(uri, VND_ORCID_XML, peerReview, accessToken);
    }
    
    public ClientResponse deletePeerReviewXml(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(PEER_REVIEW + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.deleteClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }
    
    public ClientResponse getGroupIdRecord(Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(GROUP_ID_RECORD + PUTCODE).build(putCode);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }
    
    public ClientResponse getGroupIdRecords(int pageSize, int page, String accessToken) {
        URI uri = UriBuilder.fromPath(GROUP_ID_RECORD).queryParam("page-size", pageSize).queryParam("page", page).build();
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }

    public ClientResponse getGroupIdByName(String name, String accessToken) {
        URI uri = UriBuilder.fromPath(GROUP_ID_RECORD).queryParam("name", name).build();
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }
    
    public ClientResponse createGroupIdRecord(GroupIdRecord groupId, String accessToken) {
        URI uri = UriBuilder.fromPath(GROUP_ID_RECORD).build();
        return orcidClientHelper.postClientResponseWithToken(uri, VND_ORCID_XML, groupId, accessToken);
    }
    
    public ClientResponse deleteGroupIdRecord(Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(GROUP_ID_RECORD + PUTCODE).build(putCode);
        return orcidClientHelper.deleteClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }
    
    public ClientResponse createResearcherUrls(String orcid, ResearcherUrl rUrl, String accessToken) {
        URI uri = UriBuilder.fromPath(RESEARCHER_URLS).build(orcid);
        return orcidClientHelper.postClientResponseWithToken(uri, VND_ORCID_XML, rUrl, accessToken);      
    }
    
    public ClientResponse updateResearcherUrls(String orcid, ResearcherUrl rUrl, String accessToken) {
        URI uri = UriBuilder.fromPath(RESEARCHER_URLS + PUTCODE).build(orcid, rUrl.getPutCode());
        return orcidClientHelper.putClientResponseWithToken(uri, VND_ORCID_XML, rUrl, accessToken);      
    }
    
    public ClientResponse getResearcherUrls(String orcid, String accessToken) {
        URI uri = UriBuilder.fromPath(RESEARCHER_URLS).build(orcid);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);        
    }
    
    public ClientResponse getResearcherUrl(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(RESEARCHER_URLS + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);        
    }
    
    public ClientResponse deleteResearcherUrl(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(RESEARCHER_URLS + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.deleteClientResponseWithToken(uri, VND_ORCID_XML, accessToken);      
    }
    
    public ClientResponse getEmails(String orcid, String accessToken) {
        URI uri = UriBuilder.fromPath(EMAIL).build(orcid);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);        
    }
    
    public ClientResponse viewPersonalDetailsXML(String orcid, String accessToken) {
        URI uri = UriBuilder.fromPath(PERSONAL_DETAILS).build(orcid);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }
    
    public ClientResponse createOtherName(String orcid, OtherName otherName, String accessToken) {
        URI uri = UriBuilder.fromPath(OTHER_NAMES).build(orcid);
        return orcidClientHelper.postClientResponseWithToken(uri, VND_ORCID_XML, otherName, accessToken);      
    }
    
    public ClientResponse updateOtherName(String orcid, OtherName otherName, String accessToken) {
        URI uri = UriBuilder.fromPath(OTHER_NAMES + PUTCODE).build(orcid, otherName.getPutCode());
        return orcidClientHelper.putClientResponseWithToken(uri, VND_ORCID_XML, otherName, accessToken);      
    }
    
    public ClientResponse deleteOtherName(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(OTHER_NAMES + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.deleteClientResponseWithToken(uri, VND_ORCID_XML, accessToken);      
    }
                
    public ClientResponse viewOtherNames(String orcid, String accessToken) {
        URI uri = UriBuilder.fromPath(OTHER_NAMES).build(orcid);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);        
    }
    
    public ClientResponse viewOtherName(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(OTHER_NAMES + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);        
    }
    
    public ClientResponse createExternalIdentifier(String orcid, PersonExternalIdentifier externalIdentifier, String accessToken) {
        URI uri = UriBuilder.fromPath(EXTERNAL_IDENTIFIERS).build(orcid);
        return orcidClientHelper.postClientResponseWithToken(uri, VND_ORCID_XML, externalIdentifier, accessToken);      
    }
    
    public ClientResponse updateExternalIdentifier(String orcid, PersonExternalIdentifier externalIdentifier, String accessToken) {
        URI uri = UriBuilder.fromPath(EXTERNAL_IDENTIFIERS + PUTCODE).build(orcid, externalIdentifier.getPutCode());
        return orcidClientHelper.putClientResponseWithToken(uri, VND_ORCID_XML, externalIdentifier, accessToken);      
    }
        
    public ClientResponse deleteExternalIdentifier(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(EXTERNAL_IDENTIFIERS + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.deleteClientResponseWithToken(uri, VND_ORCID_XML, accessToken);      
    }
                
    public ClientResponse viewExternalIdentifiers(String orcid, String accessToken) {
        URI uri = UriBuilder.fromPath(EXTERNAL_IDENTIFIERS).build(orcid);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);        
    }
    
    public ClientResponse viewExternalIdentifier(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(EXTERNAL_IDENTIFIERS + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);        
    }   
    
    public ClientResponse viewBiography(String orcid, String accessToken) {
        URI uri = UriBuilder.fromPath(BIOGRAPHY).build(orcid);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);        
    }
    
    public ClientResponse createKeyword(String orcid, Keyword keyword, String accessToken) {
        URI uri = UriBuilder.fromPath(KEYWORDS).build(orcid);
        return orcidClientHelper.postClientResponseWithToken(uri, VND_ORCID_XML, keyword, accessToken);      
    }
    
    public ClientResponse updateKeyword(String orcid, Keyword keyword, String accessToken) {
        URI uri = UriBuilder.fromPath(KEYWORDS + PUTCODE).build(orcid, keyword.getPutCode());
        return orcidClientHelper.putClientResponseWithToken(uri, VND_ORCID_XML, keyword, accessToken);      
    }
    
    public ClientResponse deleteKeyword(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(KEYWORDS + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.deleteClientResponseWithToken(uri, VND_ORCID_XML, accessToken);      
    }
                
    public ClientResponse viewKeywords(String orcid, String accessToken) {
        URI uri = UriBuilder.fromPath(KEYWORDS).build(orcid);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);        
    }
    
    public ClientResponse viewKeyword(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(KEYWORDS + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);        
    }                
    
    public ClientResponse createAddress(String orcid, Address address, String accessToken) {
        URI uri = UriBuilder.fromPath(ADDRESS).build(orcid);
        return orcidClientHelper.postClientResponseWithToken(uri, VND_ORCID_XML, address, accessToken);      
    }
    
    public ClientResponse updateAddress(String orcid, Address address, String accessToken) {
        URI uri = UriBuilder.fromPath(ADDRESS + PUTCODE).build(orcid, address.getPutCode());
        return orcidClientHelper.putClientResponseWithToken(uri, VND_ORCID_XML, address, accessToken);      
    }
    
    public ClientResponse deleteAddress(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(ADDRESS + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.deleteClientResponseWithToken(uri, VND_ORCID_XML, accessToken);      
    }
                
    public ClientResponse viewAddresses(String orcid, String accessToken) {
        URI uri = UriBuilder.fromPath(ADDRESS).build(orcid);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);        
    }
    
    public ClientResponse viewAddress(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(ADDRESS + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);        
    }
    
    public ClientResponse viewPerson(String orcid, String accessToken) {
        URI uri = UriBuilder.fromPath(PERSON).build(orcid);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);        
    }
    
    public ClientResponse viewRecord(String orcid, String accessToken) {
        URI uri = UriBuilder.fromPath(PROFILE_ROOT_PATH).build(orcid);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);        
    }
    
    public ClientResponse viewDistinctionXml(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(DISTINCTION + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }

    public ClientResponse createDistinctionXml(String orcid, Distinction distinction, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(DISTINCTION).build(orcid), VND_ORCID_XML, distinction, accessToken);
    }

    public ClientResponse createDistinctionJson(String orcid, Distinction distinction, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(DISTINCTION).build(orcid), VND_ORCID_JSON, distinction, accessToken);
    }
        
    public ClientResponse updateDistinction(String orcid, Distinction distinction, String accessToken) {
        URI uri = UriBuilder.fromPath(DISTINCTION + PUTCODE).build(orcid, distinction.getPutCode());
        return orcidClientHelper.putClientResponseWithToken(uri, VND_ORCID_XML, distinction, accessToken);
    }
    
    public ClientResponse deleteDistinctionXml(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(DISTINCTION + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.deleteClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }
    
    public ClientResponse viewInvitedPositionXml(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(INVITED_POSITION + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }

    public ClientResponse createInvitedPositionXml(String orcid, InvitedPosition invitedPosition, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(INVITED_POSITION).build(orcid), VND_ORCID_XML, invitedPosition, accessToken);
    }

    public ClientResponse createInvitedPositionJson(String orcid, InvitedPosition invitedPosition, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(INVITED_POSITION).build(orcid), VND_ORCID_JSON, invitedPosition, accessToken);
    }
        
    public ClientResponse updateInvitedPosition(String orcid, InvitedPosition invitedPosition, String accessToken) {
        URI uri = UriBuilder.fromPath(INVITED_POSITION + PUTCODE).build(orcid, invitedPosition.getPutCode());
        return orcidClientHelper.putClientResponseWithToken(uri, VND_ORCID_XML, invitedPosition, accessToken);
    }
    
    public ClientResponse deleteInvitedPositionXml(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(INVITED_POSITION + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.deleteClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }
    
    public ClientResponse viewMembershipXml(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(MEMBERSHIP + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }

    public ClientResponse createMembershipXml(String orcid, Membership membership, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(MEMBERSHIP).build(orcid), VND_ORCID_XML, membership, accessToken);
    }

    public ClientResponse createMembershipJson(String orcid, Membership membership, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(MEMBERSHIP).build(orcid), VND_ORCID_JSON, membership, accessToken);
    }
        
    public ClientResponse updateMembership(String orcid, Membership membership, String accessToken) {
        URI uri = UriBuilder.fromPath(MEMBERSHIP + PUTCODE).build(orcid, membership.getPutCode());
        return orcidClientHelper.putClientResponseWithToken(uri, VND_ORCID_XML, membership, accessToken);
    }
    
    public ClientResponse deleteMembershipXml(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(MEMBERSHIP + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.deleteClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }
    
    public ClientResponse viewQualificationXml(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(QUALIFICATION + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }

    public ClientResponse createQualificationXml(String orcid, Qualification qualification, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(QUALIFICATION).build(orcid), VND_ORCID_XML, qualification, accessToken);
    }

    public ClientResponse createQualificationJson(String orcid, Qualification qualification, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(QUALIFICATION).build(orcid), VND_ORCID_JSON, qualification, accessToken);
    }
        
    public ClientResponse updateQualification(String orcid, Qualification qualification, String accessToken) {
        URI uri = UriBuilder.fromPath(QUALIFICATION + PUTCODE).build(orcid, qualification.getPutCode());
        return orcidClientHelper.putClientResponseWithToken(uri, VND_ORCID_XML, qualification, accessToken);
    }
    
    public ClientResponse deleteQualificationXml(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(QUALIFICATION + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.deleteClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }
    
    public ClientResponse viewServiceXml(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(SERVICE + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }

    public ClientResponse createServiceXml(String orcid, Service service, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(SERVICE).build(orcid), VND_ORCID_XML, service, accessToken);
    }

    public ClientResponse createServiceJson(String orcid, Service service, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(SERVICE).build(orcid), VND_ORCID_JSON, service, accessToken);
    }
        
    public ClientResponse updateService(String orcid, Service service, String accessToken) {
        URI uri = UriBuilder.fromPath(SERVICE + PUTCODE).build(orcid, service.getPutCode());
        return orcidClientHelper.putClientResponseWithToken(uri, VND_ORCID_XML, service, accessToken);
    }
    
    public ClientResponse deleteServiceXml(String orcid, Long putCode, String accessToken) {
        URI uri = UriBuilder.fromPath(SERVICE + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.deleteClientResponseWithToken(uri, VND_ORCID_XML, accessToken);
    }
           
}
