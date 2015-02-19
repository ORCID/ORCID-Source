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

import static org.orcid.core.api.OrcidApiConstants.*;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.orcid.api.common.OrcidClientHelper;
import org.orcid.core.exception.OrcidNotificationAlreadyReadException;
import org.orcid.jaxb.model.notification.addactivities.NotificationAddActivities;
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

    public ClientResponse viewWorkXml(String orcid, String putCode, String accessToken) {
        return null;
    }

    public ClientResponse createWorkXml(String orcid, Work work, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(WORK).build(orcid), VND_ORCID_XML, work, accessToken);
    }
    
    public ClientResponse createWorkJson(String orcid, Work work, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(WORK).build(orcid), VND_ORCID_JSON, work, accessToken);
    }

    public ClientResponse updateWorkXml(String orcid, String putCode, Work work, String accessToken) {
        return null;
    }

    public ClientResponse deleteWorkXml(String orcid, String putCode, String accessToken) {
        return null;
    }

}
