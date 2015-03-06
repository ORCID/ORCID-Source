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
package org.orcid.integration.api.memberV2;

import static org.orcid.core.api.OrcidApiConstants.ACTIVITIES;
import static org.orcid.core.api.OrcidApiConstants.PUTCODE;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.WORK;
import static org.orcid.core.api.OrcidApiConstants.EDUCATION;
import static org.orcid.core.api.OrcidApiConstants.EMPLOYMENT;
import static org.orcid.core.api.OrcidApiConstants.FUNDING;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.UriBuilder;

import org.orcid.api.common.OrcidClientHelper;
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Employment;
import org.orcid.jaxb.model.record.Funding;
import org.orcid.jaxb.model.record.Work;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Will Simpson
 *
 */
public class MemberV2ApiClientImpl {

    private OrcidClientHelper orcidClientHelper;

    public MemberV2ApiClientImpl(URI baseUri, Client c) throws URISyntaxException {
        orcidClientHelper = new OrcidClientHelper(baseUri, c);
    }    
    
    public ClientResponse viewLocationXml(URI location, String accessToken) throws URISyntaxException {
        return orcidClientHelper.getClientResponseWithToken(location, VND_ORCID_XML, accessToken);
    }

    public ClientResponse viewActivities(String orcid, String accessToken) {
        URI activitiesUri = UriBuilder.fromPath(ACTIVITIES).build(orcid);
        return orcidClientHelper.getClientResponseWithToken(activitiesUri, VND_ORCID_XML, accessToken);
    }
    
    public ClientResponse viewWorkXml(String orcid, String putCode, String accessToken) {
        URI workUri = UriBuilder.fromPath(WORK + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponseWithToken(workUri, VND_ORCID_XML, accessToken);
    }

    public ClientResponse createWorkXml(String orcid, Work work, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(WORK).build(orcid), VND_ORCID_XML, work, accessToken);
    }

    public ClientResponse createWorkJson(String orcid, Work work, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(WORK).build(orcid), VND_ORCID_JSON, work, accessToken);
    }

    public ClientResponse deleteWorkXml(String orcid, String putCode, String accessToken) {
        URI deleteWorkUri = UriBuilder.fromPath(WORK + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.deleteClientResponseWithToken(deleteWorkUri, VND_ORCID_XML, accessToken);
    }
    
    public ClientResponse viewEducationXml(String orcid, String putCode, String accessToken) {
        URI educationUri = UriBuilder.fromPath(EDUCATION + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponseWithToken(educationUri, VND_ORCID_XML, accessToken);
    }

    public ClientResponse createEducationXml(String orcid, Education education, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(EDUCATION).build(orcid), VND_ORCID_XML, education, accessToken);
    }

    public ClientResponse createEducationJson(String orcid, Education education, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(EDUCATION).build(orcid), VND_ORCID_JSON, education, accessToken);
    }
        
    public ClientResponse deleteEducationXml(String orcid, String putCode, String accessToken) {
        URI deleteEducationUri = UriBuilder.fromPath(EDUCATION + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.deleteClientResponseWithToken(deleteEducationUri, VND_ORCID_XML, accessToken);
    }           
    
    public ClientResponse viewEmploymentXml(String orcid, String putCode, String accessToken) {
        URI employmentUri = UriBuilder.fromPath(EMPLOYMENT + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponseWithToken(employmentUri, VND_ORCID_XML, accessToken);
    }

    public ClientResponse createEmploymentXml(String orcid, Employment employment, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(EMPLOYMENT).build(orcid), VND_ORCID_XML, employment, accessToken);
    }

    public ClientResponse createEmploymentJson(String orcid, Employment employment, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(EMPLOYMENT).build(orcid), VND_ORCID_JSON, employment, accessToken);
    }
    
    public ClientResponse deleteEmploymentXml(String orcid, String putCode, String accessToken) {
        URI deleteEmploymentUri = UriBuilder.fromPath(EMPLOYMENT + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.deleteClientResponseWithToken(deleteEmploymentUri, VND_ORCID_XML, accessToken);
    }
        
    public ClientResponse viewFundingXml(String orcid, String putCode, String accessToken) {
        URI fundingUri = UriBuilder.fromPath(FUNDING + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponseWithToken(fundingUri, VND_ORCID_XML, accessToken);
    }

    public ClientResponse createFundingXml(String orcid, Funding funding, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(FUNDING).build(orcid), VND_ORCID_XML, funding, accessToken);
    }

    public ClientResponse createFundingJson(String orcid, Funding funding, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(FUNDING).build(orcid), VND_ORCID_JSON, funding, accessToken);
    }
            
    public ClientResponse deleteFundingXml(String orcid, String putCode, String accessToken) {
        URI deleteFundingUri = UriBuilder.fromPath(FUNDING + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.deleteClientResponseWithToken(deleteFundingUri, VND_ORCID_XML, accessToken);
    }
                
    public ClientResponse updateLocationXml(URI location, String accessToken, Object jaxbRootElement){
        return orcidClientHelper.putClientResponseWithToken(location, VND_ORCID_XML, jaxbRootElement, accessToken);
    }   
}
