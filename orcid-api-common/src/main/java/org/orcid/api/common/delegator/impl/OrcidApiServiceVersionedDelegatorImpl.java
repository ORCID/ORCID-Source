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
import org.orcid.core.manager.ValidationManager;
import org.orcid.core.version.OrcidMessageVersionConverterChain;
import org.orcid.jaxb.model.message.OrcidMessage;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidApiServiceVersionedDelegatorImpl implements OrcidApiServiceDelegator {

    @Resource(name = "orcidApiServiceDelegator")
    private OrcidApiServiceDelegator orcidApiServiceDelegator;

    @Resource
    private OrcidMessageVersionConverterChain orcidMessageVersionConverterChain;

    private ValidationManager outgoingValidationManager;

    private String externalVersion;

    public void setExternalVersion(String externalVersion) {
        this.externalVersion = externalVersion;
    }

    public void setOutgoingValidationManager(ValidationManager outgoingValidationManager) {
        this.outgoingValidationManager = outgoingValidationManager;
    }

    @Override
    public Response viewStatusText() {
        return orcidApiServiceDelegator.viewStatusText();
    }

    @Override
    public Response findBioDetails(String orcid) {
        Response response = orcidApiServiceDelegator.findBioDetails(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findBioDetailsFromPublicCache(String orcid) {
        Response response = orcidApiServiceDelegator.findBioDetailsFromPublicCache(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findExternalIdentifiers(String orcid) {
        Response response = orcidApiServiceDelegator.findExternalIdentifiers(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findExternalIdentifiersFromPublicCache(String orcid) {
        Response response = orcidApiServiceDelegator.findExternalIdentifiersFromPublicCache(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findFullDetails(String orcid) {
        Response response = orcidApiServiceDelegator.findFullDetails(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findFullDetailsFromPublicCache(String orcid) {
        Response response = orcidApiServiceDelegator.findFullDetailsFromPublicCache(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findAffiliationsDetails(String orcid) {
        Response response = orcidApiServiceDelegator.findAffiliationsDetails(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findAffiliationsDetailsFromPublicCache(String orcid) {
        Response response = orcidApiServiceDelegator.findAffiliationsDetailsFromPublicCache(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findWorksDetails(String orcid) {
        Response response = orcidApiServiceDelegator.findWorksDetails(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findWorksDetailsFromPublicCache(String orcid) {
        Response response = orcidApiServiceDelegator.findWorksDetailsFromPublicCache(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response searchByQuery(Map<String, List<String>> queryMap) {
        Response response = orcidApiServiceDelegator.searchByQuery(queryMap);
        return downgradeAndValidateResponse(response);
    }

    private Response downgradeResponse(Response response) {
        OrcidMessage orcidMessage = (OrcidMessage) response.getEntity();
        if (orcidMessage != null) {
            orcidMessageVersionConverterChain.downgradeMessage(orcidMessage, externalVersion);
        }
        return Response.fromResponse(response).entity(orcidMessage).build();
    }

    private void validateOutgoingResponse(Response response) {
        OrcidMessage orcidMessage = (OrcidMessage) response.getEntity();
        outgoingValidationManager.validateMessage(orcidMessage);
    }

    private Response downgradeAndValidateResponse(Response response) {
        Response downgradedResponse = downgradeResponse(response);
        validateOutgoingResponse(downgradedResponse);
        return downgradedResponse;
    }

}
