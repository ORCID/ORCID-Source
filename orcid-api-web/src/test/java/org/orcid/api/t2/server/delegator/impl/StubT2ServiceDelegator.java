/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.api.t2.server.delegator.impl;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.orcid.api.t2.server.delegator.T2OrcidApiServiceDelegator;
import org.orcid.jaxb.model.message.OrcidMessage;

public class StubT2ServiceDelegator implements T2OrcidApiServiceDelegator {

    @Override
    public Response viewStatusText() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response findBioDetails(String orcid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response findExternalIdentifiers(String orcid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response findFullDetails(String orcid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response findWorksDetails(String orcid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response searchByQuery(Map<String, List<String>> queryMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response createProfile(UriInfo uriInfo, OrcidMessage orcidMessage) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response updateBioDetails(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response addWorks(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response updateWorks(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response addExternalIdentifiers(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response deleteProfile(UriInfo uriInfo, String orcid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response registerWebhook(UriInfo uriInfo, String orcid, String webhookUri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response unregisterWebhook(UriInfo uriInfo, String orcid, String webhookUri) {
        // TODO Auto-generated method stub
        return null;
    }

}
