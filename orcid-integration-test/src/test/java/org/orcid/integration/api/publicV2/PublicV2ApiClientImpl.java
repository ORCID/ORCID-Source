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
package org.orcid.integration.api.publicV2;

import static org.orcid.core.api.OrcidApiConstants.ACTIVITIES;
import static org.orcid.core.api.OrcidApiConstants.PUTCODE;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.EDUCATION;
import static org.orcid.core.api.OrcidApiConstants.EDUCATION_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.EMPLOYMENT;
import static org.orcid.core.api.OrcidApiConstants.EMPLOYMENT_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.FUNDING;
import static org.orcid.core.api.OrcidApiConstants.FUNDING_SUMMARY;
import static org.orcid.core.api.OrcidApiConstants.WORK;
import static org.orcid.core.api.OrcidApiConstants.WORK_SUMMARY;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.UriBuilder;

import org.orcid.api.common.OrcidClientHelper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class PublicV2ApiClientImpl {

    private OrcidClientHelper orcidClientHelper;

    public PublicV2ApiClientImpl(URI baseUri, Client c) throws URISyntaxException {
        orcidClientHelper = new OrcidClientHelper(baseUri, c);
    }  

    public ClientResponse viewActivities(String orcid) {
        URI activitiesUri = UriBuilder.fromPath(ACTIVITIES).build(orcid);
        return orcidClientHelper.getClientResponse(activitiesUri, VND_ORCID_XML);
    }
    
    public ClientResponse viewWorkXml(String orcid, String putCode) {
        URI uri = UriBuilder.fromPath(WORK + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponse(uri, VND_ORCID_XML);
    }
 
    public ClientResponse viewWorkSummaryXml(String orcid, String putCode) {
        URI uri = UriBuilder.fromPath(WORK_SUMMARY + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponse(uri, VND_ORCID_XML);
    }
    
    public ClientResponse viewFundingXml(String orcid, String putCode) {
        URI uri = UriBuilder.fromPath(FUNDING + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponse(uri, VND_ORCID_XML);
    }
 
    public ClientResponse viewFundingSummaryXml(String orcid, String putCode) {
        URI uri = UriBuilder.fromPath(FUNDING_SUMMARY + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponse(uri, VND_ORCID_XML);
    }
    
    public ClientResponse viewEducationXml(String orcid, String putCode) {
        URI uri = UriBuilder.fromPath(EDUCATION + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponse(uri, VND_ORCID_XML);
    }
 
    public ClientResponse viewEducationSummaryXml(String orcid, String putCode) {
        URI uri = UriBuilder.fromPath(EDUCATION_SUMMARY + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponse(uri, VND_ORCID_XML);
    }
    
    public ClientResponse viewEmploymentXml(String orcid, String putCode) {
        URI uri = UriBuilder.fromPath(EMPLOYMENT + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponse(uri, VND_ORCID_XML);
    }
 
    public ClientResponse viewEmploymentSummaryXml(String orcid, String putCode) {
        URI uri = UriBuilder.fromPath(EMPLOYMENT_SUMMARY + PUTCODE).build(orcid, putCode);
        return orcidClientHelper.getClientResponse(uri, VND_ORCID_XML);
    }
}
