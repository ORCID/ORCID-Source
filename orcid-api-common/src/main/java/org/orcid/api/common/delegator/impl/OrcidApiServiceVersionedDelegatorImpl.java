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
package org.orcid.api.common.delegator.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.orcid.api.common.delegator.OrcidApiServiceDelegator;
import org.orcid.core.version.OrcidMessageVersionConverterChain;
import org.orcid.jaxb.model.message.OrcidMessage;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidApiServiceVersionedDelegatorImpl implements OrcidApiServiceDelegator {

    @Resource
    private OrcidApiServiceDelegator orcidApiServiceDelegator;

    @Resource
    private OrcidMessageVersionConverterChain orcidMessageVersionConverterChain;

    private String externalVersion;

    public String getExternalVersion() {
        return externalVersion;
    }

    public void setExternalVersion(String externalVersion) {
        this.externalVersion = externalVersion;
    }

    @Override
    public Response viewStatusText() {
        return orcidApiServiceDelegator.viewStatusText();
    }

    @Override
    public Response findBioDetails(String orcid) {
        Response response = orcidApiServiceDelegator.findBioDetails(orcid);
        return downgradeResponse(response);
    }

    @Override
    public Response findBioDetailsFromPublicCache(String orcid) {
        Response response = orcidApiServiceDelegator.findBioDetailsFromPublicCache(orcid);
        return downgradeResponse(response);
    }

    @Override
    public Response findExternalIdentifiers(String orcid) {
        Response response = orcidApiServiceDelegator.findExternalIdentifiers(orcid);
        return downgradeResponse(response);
    }

    @Override
    public Response findExternalIdentifiersFromPublicCache(String orcid) {
        Response response = orcidApiServiceDelegator.findExternalIdentifiersFromPublicCache(orcid);
        return downgradeResponse(response);
    }

    @Override
    public Response findFullDetails(String orcid) {
        Response response = orcidApiServiceDelegator.findFullDetails(orcid);
        return downgradeResponse(response);
    }

    @Override
    public Response findFullDetailsFromPublicCache(String orcid) {
        Response response = orcidApiServiceDelegator.findFullDetailsFromPublicCache(orcid);
        return downgradeResponse(response);
    }

    @Override
    public Response findWorksDetails(String orcid) {
        Response response = orcidApiServiceDelegator.findWorksDetails(orcid);
        return downgradeResponse(response);
    }

    @Override
    public Response findWorksDetailsFromPublicCache(String orcid) {
        Response response = orcidApiServiceDelegator.findFullDetailsFromPublicCache(orcid);
        return downgradeResponse(response);
    }

    @Override
    public Response searchByQuery(Map<String, List<String>> queryMap) {
        Response response = orcidApiServiceDelegator.searchByQuery(queryMap);
        return downgradeResponse(response);
    }

    private Response downgradeResponse(Response response) {
        OrcidMessage orcidMessage = (OrcidMessage) response.getEntity();
        if (orcidMessage != null) {
            orcidMessageVersionConverterChain.downgradeMessage(orcidMessage, externalVersion);
        }
        return Response.fromResponse(response).entity(orcidMessage).build();
    }

}
