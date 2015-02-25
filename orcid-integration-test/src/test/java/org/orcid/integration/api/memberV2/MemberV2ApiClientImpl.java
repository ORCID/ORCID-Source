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

import static org.orcid.core.api.OrcidApiConstants.PROFILE_GET_PATH;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.WORK;
import static org.orcid.core.api.OrcidApiConstants.PUTCODE;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.UriBuilder;

import org.orcid.api.common.OrcidClientHelper;
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

    public ClientResponse updateLocationXml(URI location, String accessToken, Object jaxbRootElement){
        return orcidClientHelper.putClientResponseWithToken(location, VND_ORCID_XML, jaxbRootElement, accessToken);
    }

    public ClientResponse updateWorkXml(String orcid, String putCode, Work work, String accessToken) {
        return null;
    }

    public ClientResponse deleteWorkXml(String orcid, String putCode, String accessToken) {
        return null;
    }

}
