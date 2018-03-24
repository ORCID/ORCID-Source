package org.orcid.integration.api.pub;

import static org.orcid.core.api.OrcidApiConstants.ACTIVITIES;
import static org.orcid.core.api.OrcidApiConstants.PUTCODE;
import static org.orcid.core.api.OrcidApiConstants.RESEARCHER_URLS;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.EDUCATION;
import static org.orcid.core.api.OrcidApiConstants.EDUCATION_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.EMPLOYMENT;
import static org.orcid.core.api.OrcidApiConstants.EMPLOYMENT_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.FUNDING;
import static org.orcid.core.api.OrcidApiConstants.FUNDING_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.WORK;
import static org.orcid.core.api.OrcidApiConstants.WORK_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.PEER_REVIEW;
import static org.orcid.core.api.OrcidApiConstants.PEER_REVIEW_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.EMAIL;
import static org.orcid.core.api.OrcidApiConstants.PERSONAL_DETAILS;
import static org.orcid.core.api.OrcidApiConstants.OTHER_NAMES;
import static org.orcid.core.api.OrcidApiConstants.EXTERNAL_IDENTIFIERS;
import static org.orcid.core.api.OrcidApiConstants.BIOGRAPHY;
import static org.orcid.core.api.OrcidApiConstants.KEYWORDS;
import static org.orcid.core.api.OrcidApiConstants.ADDRESS;
import static org.orcid.core.api.OrcidApiConstants.PERSON;
import static org.orcid.core.api.OrcidApiConstants.PROFILE_ROOT_PATH;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.orcid.api.common.OrcidClientHelper;
import org.orcid.pojo.ajaxForm.PojoUtil;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class PublicV2ApiClientImpl {

    private OrcidClientHelper orcidClientHelper;

    public PublicV2ApiClientImpl(URI baseUri, Client c) throws URISyntaxException {
        orcidClientHelper = new OrcidClientHelper(baseUri, c);
    }  

    public ClientResponse viewActivities(String orcid) {
        return viewActivities(orcid, null);
    }
    
    public ClientResponse viewActivities(String orcid, String token) {
        URI uri = UriBuilder.fromPath(ACTIVITIES).build(orcid);
        return getClientReponse(uri, token);
    }
    
    public ClientResponse viewWorkXml(String orcid, String putCode) {
        return viewWorkXml(orcid, putCode, null);
    }
    
    public ClientResponse viewWorkXml(String orcid, String putCode, String token) {
        URI uri = UriBuilder.fromPath(WORK + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, token);
    }        
 
    public ClientResponse viewWorkSummaryXml(String orcid, String putCode) {
        return viewWorkSummaryXml(orcid, putCode, null);
    }
    
    public ClientResponse viewWorkSummaryXml(String orcid, String putCode, String token) {
        URI uri = UriBuilder.fromPath(WORK_SUMMARY + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, token);
    }
    
    public ClientResponse viewFundingXml(String orcid, String putCode) {        
        return viewFundingXml(orcid, putCode, null);
    }
    
    public ClientResponse viewFundingXml(String orcid, String putCode, String token) {
        URI uri = UriBuilder.fromPath(FUNDING + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, token);
    }
    
    public ClientResponse viewFundingSummaryXml(String orcid, String putCode) {
        return viewFundingSummaryXml(orcid, putCode, null);
    }
    
    public ClientResponse viewFundingSummaryXml(String orcid, String putCode, String token) {
        URI uri = UriBuilder.fromPath(FUNDING_SUMMARY + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, token);
    }
    
    public ClientResponse viewEducationXml(String orcid, String putCode) {
        return viewEducationXml(orcid, putCode, null);
    }
    
    public ClientResponse viewEducationXml(String orcid, String putCode, String token) {
        URI uri = UriBuilder.fromPath(EDUCATION + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, token);
    }
    
    public ClientResponse viewEducationSummaryXml(String orcid, String putCode) {
        return viewEducationSummaryXml(orcid, putCode, null);
    }
    
    public ClientResponse viewEducationSummaryXml(String orcid, String putCode, String token) {
        URI uri = UriBuilder.fromPath(EDUCATION_SUMMARY + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, token);
    }
    
    public ClientResponse viewEmploymentXml(String orcid, String putCode) {        
        return viewEmploymentXml(orcid, putCode, null);
    }
 
    public ClientResponse viewEmploymentXml(String orcid, String putCode, String token) {
        URI uri = UriBuilder.fromPath(EMPLOYMENT + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, token);
    }
    
    public ClientResponse viewEmploymentSummaryXml(String orcid, String putCode) {
        return viewEmploymentSummaryXml(orcid, putCode, null);
    }
    
    public ClientResponse viewEmploymentSummaryXml(String orcid, String putCode, String token) {
        URI uri = UriBuilder.fromPath(EMPLOYMENT_SUMMARY + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, token);
    }
    
    public ClientResponse viewPeerReviewXml(String orcid, String putCode) {
        return viewPeerReviewXml(orcid, putCode, null);
    }
    
    public ClientResponse viewPeerReviewXml(String orcid, String putCode, String token) {
        URI uri = UriBuilder.fromPath(PEER_REVIEW + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, token);
    }
    
    public ClientResponse viewPeerReviewSummaryXml(String orcid, String putCode) {
        return viewPeerReviewSummaryXml(orcid, putCode, null);
    }
    
    public ClientResponse viewPeerReviewSummaryXml(String orcid, String putCode, String token) {
        URI uri = UriBuilder.fromPath(PEER_REVIEW_SUMMARY + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, token);
    }    
    
    public ClientResponse viewResearcherUrlsXML(String orcid) {
        URI uri = UriBuilder.fromPath(RESEARCHER_URLS).build(orcid);
        return getClientReponse(uri, null);        
    }
    
    public ClientResponse viewResearcherUrlXML(String orcid, String putCode) {
        URI uri = UriBuilder.fromPath(RESEARCHER_URLS + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, null);        
    }     
    
    public ClientResponse viewEmailXML(String orcid) {
        URI uri = UriBuilder.fromPath(EMAIL).build(orcid);
        return getClientReponse(uri, null); 
    }
    
    public ClientResponse viewPersonalDetailsXML(String orcid) {
        URI uri = UriBuilder.fromPath(PERSONAL_DETAILS).build(orcid);
        return getClientReponse(uri, null);
    }        
    
    public ClientResponse viewOtherNamesXML(String orcid) {
        URI uri = UriBuilder.fromPath(OTHER_NAMES).build(orcid);
        return getClientReponse(uri, null);
    }
    
    public ClientResponse viewOtherNameXML(String orcid, Long putCode) {
        URI uri = UriBuilder.fromPath(OTHER_NAMES + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, null);
    }
    
    public ClientResponse viewExternalIdentifiersXML(String orcid) {
        URI uri = UriBuilder.fromPath(EXTERNAL_IDENTIFIERS).build(orcid);
        return getClientReponse(uri, null);
    }
    
    public ClientResponse viewExternalIdentifierXML(String orcid, Long putCode) {
        URI uri = UriBuilder.fromPath(EXTERNAL_IDENTIFIERS + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, null);
    }
       
    public ClientResponse viewBiographyXML(String orcid) {
        URI uri = UriBuilder.fromPath(BIOGRAPHY).build(orcid);
        return getClientReponse(uri, null);
    }

    public ClientResponse viewBiographyJson(String orcid) {
        URI uri = UriBuilder.fromPath(BIOGRAPHY).build(orcid);
        return getClientReponse(uri, null, MediaType.APPLICATION_JSON);
    }
        
    public ClientResponse viewKeywordsXML(String orcid) {
        URI uri = UriBuilder.fromPath(KEYWORDS).build(orcid);
        return getClientReponse(uri, null);
    }
    
    public ClientResponse viewKeywordsXML(String orcid, Long putCode) {
        URI uri = UriBuilder.fromPath(KEYWORDS + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, null);
    }
             
    public ClientResponse viewAddressesXML(String orcid) {
        URI uri = UriBuilder.fromPath(ADDRESS).build(orcid);
        return getClientReponse(uri, null);
    }
    
    public ClientResponse viewAddressXML(String orcid, Long putCode) {
        URI uri = UriBuilder.fromPath(ADDRESS + PUTCODE).build(orcid, putCode);
        return getClientReponse(uri, null);
    }
    
    public ClientResponse viewPersonXML(String orcid) {
        URI uri = UriBuilder.fromPath(PERSON).build(orcid);
        return getClientReponse(uri, null);
    }

    public ClientResponse viewPersonJson(String orcid) {
        URI uri = UriBuilder.fromPath(PERSON).build(orcid);
        return getClientReponse(uri, null, MediaType.APPLICATION_JSON);
    }

    public ClientResponse viewRecordXML(String orcid) {
        URI uri = UriBuilder.fromPath(PROFILE_ROOT_PATH).build(orcid);
        return getClientReponse(uri, null);
    }
    
    public ClientResponse viewInvalidEndpoint(String orcid) {
        URI uri = UriBuilder.fromPath(PROFILE_ROOT_PATH + "/invalid").build(orcid);
        return getClientReponse(uri, null);
    }
    
    private ClientResponse getClientReponse(URI uri, String token) {
        return getClientReponse(uri, token, VND_ORCID_XML);
    }

    private ClientResponse getClientReponse(URI uri, String token, String mediaType) {
        ClientResponse result = null;
        if (PojoUtil.isEmpty(token)) {
            result = orcidClientHelper.getClientResponse(uri, mediaType);
        } else {
            result = orcidClientHelper.getClientResponseWithToken(uri, mediaType, token);
        }
        return result;
    }
}
